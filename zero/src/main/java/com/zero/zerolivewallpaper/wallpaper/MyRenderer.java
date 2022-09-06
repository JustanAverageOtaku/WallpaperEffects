package com.zero.zerolivewallpaper.wallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.zero.zerolivewallpaper.R;
import com.zero.zerolivewallpaper.wallpaper.common.ConvenienceClass;
import com.zero.zerolivewallpaper.wallpaper.common.TextureHelper;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.zero.zerolivewallpaper.Constants.DEPTH_MAX;
import static com.zero.zerolivewallpaper.Constants.DEPTH_MIN;
import static com.zero.zerolivewallpaper.Constants.DIM_MAX;
import static com.zero.zerolivewallpaper.Constants.FALLBACK_MAX;
import static com.zero.zerolivewallpaper.Constants.FALLBACK_MIN;
import static com.zero.zerolivewallpaper.Constants.PREF_BACKGROUND;
import static com.zero.zerolivewallpaper.Constants.PREF_BACKGROUND_DEFAULT;
import static com.zero.zerolivewallpaper.Constants.SCROLL_AMOUNT_MAX;
import static com.zero.zerolivewallpaper.Constants.SCROLL_AMOUNT_MIN;
import static com.zero.zerolivewallpaper.Constants.SENSITIVITY_MAX;
import static com.zero.zerolivewallpaper.Constants.SENSITIVITY_MIN;
import static com.zero.zerolivewallpaper.Constants.ZOOM_MAX;
import static com.zero.zerolivewallpaper.Constants.ZOOM_MIN;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

class MyRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "Renderer";

    // Screen
    private int orientation;
    private float deltaXMax;
    private float deltaYMax;

    // Wall Paper
    private int width;
    private int height;
    // Values
    private boolean deltaInit;
    private float[][] deltaArrayNew;
    private float[][] deltaArrayOld;

    // Preferences
    private final SharedPreferences sharedPreferences;
    private boolean prefSensor;
    private boolean prefScroll;
    private boolean prefLimit;
    private double prefDepth;
    private double prefScrollAmount;
    private float prefZoom;
    private int prefDim;
    private String prefWallpaperId;

    // External
    private double offset;

    // Internal
    private String loadedWallpaperId;
    private boolean isPreview;
    private boolean hasOverlay = false;
    private boolean isFallback = false;

    private List<BackgroundHelper.Layer> layerList;
    
    // Opengl stuff
    private GLLayer glLayer;
    private final float[] MVPMatrix = new float[16];
    private final float[] modelMatrix = new float[16]; // newly Added
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] startPos = new float[]{0, 0, 4};
    private float[] endPos = new float[]{0, 0, 4};
    private int[] textures;

    private final Parallax parallax;
    private final Context context;

    // Default constructor
    MyRenderer(Context context)
    {

        this.context = context;
        parallax = new Parallax(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Matrix.setIdentityM(modelMatrix, 0);

        // Not a preview
        isPreview = false;

        start();
    }

    // Preview constructor
    MyRenderer(Context context, String prefWallpaperId) {

        this.context = context;
        parallax = new Parallax(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Is a preview
        this.prefWallpaperId = prefWallpaperId;
        isPreview = true;

        start();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Nothing
        //GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        System.out.println("Width: " + width + "  Height: " + height);

        // Refit wallpaper to match screen orientation
        if (orientation == ORIENTATION_PORTRAIT)
        {
            this.width = width;
            this.height = height;
            float ratio = (float) width / height;
            deltaXMax = (0.5f * ratio) / prefZoom;
            deltaYMax = (1 - prefZoom);
            //float customZoomX = width / 1080f;
            float customZoomX = width / 1280f;
            customZoomX = (float)width / 1485f;//ConvenienceClass.width;
            float customZoomY = height / 1920f;
            customZoomY = (float)height / 3300;//ConvenienceClass.height;
            //if(customZoomY > 1.0f)
            //{
                //customZoomY = 1f - (customZoomY % 1f);
            //}
            //System.out.println("Pref ZoomX: " + customZoomX);
            //System.out.println("Pref ZoomY: " + customZoomY);
            //Matrix.frustumM(projectionMatrix, 0, -ratio * ConvenienceClass.horizontalZoom, ratio * ConvenienceClass.horizontalZoom, -ConvenienceClass.verticalZoom, ConvenienceClass.verticalZoom, 3, 7);
            //Matrix.frustumM(projectionMatrix, 0,ratio * -customZoomX, ratio * customZoomX, 0.6f * -customZoomY, 0.6f * customZoomY, 3, 7);
            Matrix.frustumM(projectionMatrix, 0, -customZoomX, customZoomX, -customZoomY, customZoomY, 3, 7);
        }
        else
        {
            this.width = height;
            this.height = width;
            float ratio = (float) height / width;
            deltaXMax = (1 - prefZoom);
            deltaYMax = (0.5f * ratio) / prefZoom;
            Matrix.frustumM(projectionMatrix, 0, -prefZoom, prefZoom, -ratio * prefZoom, ratio * prefZoom, 3, 7);
        }

        // Create layers only if wallpaper has changed
        if (!prefWallpaperId.equals(loadedWallpaperId))
        {
            generateLayers();
        }

        //GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_ALWAYS);
        //GLES20.glDepthFunc(GLES20.GL_LESS);
        //GLES20.glClearDepthf(1.0f);
    }

    float VectorMagnitude(float[] vector)
    {
        return (float)sqrt(pow(vector[0], 2) + pow(vector[1], 2) + pow(vector[2], 2));
    }

    float[] Normalize(float[] vector)
    {
        float magnitude = VectorMagnitude(vector);
        return new float[]{vector[0]/magnitude, vector[1]/magnitude, vector[2]/magnitude};
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        long time = SystemClock.uptimeMillis() % 900000L;
        //long time = SystemClock.uptimeMillis() % 100L;
        int radius = 4;
        float angleInDegrees = (360.0f / 900000.0f) * ((int) time);
        //float rotationAngle = 60.f * (float)Math.sin(angleInDegrees);
        float rotationAngle = (float)Math.sin(angleInDegrees) * 0.5f;
        //float xpos = radius * (float)(sin(rotationAngle));
        //float zpos = radius * (float)(sin(rotationAngle));
        // Set the camera position (View matrix) (float)sin(angleInDegrees)
        // Calculate the projection and view transformation

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, radius, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        glLayer.SetViewPos(0f, 0f, radius);

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Initialize arrays
        if (!deltaInit)
        {
            deltaArrayNew = new float[textures.length][2];
            deltaArrayOld = new float[textures.length][2];
            deltaInit = true;
        }

        // Compute deltas textures.length - 1
        for (int i = 0; i < textures.length - 1; i++)
        {
            // Get layer z
            double z;
            if (!isFallback)
            {
                z = 60; //layerList.get(i).getZ();
            }
            else
            {
                z = 60;
            }

            // Compute the launcher page offset
            double scrollOffset;
            if (prefScroll && z != 0)
            {
                scrollOffset = offset / (prefScrollAmount * z);
            }
            else
            {
                scrollOffset = 0;
            }

            // Compute the x-y offset
            float deltaX = (float) -(scrollOffset + (parallax.getDegX() / 180.0 * (prefDepth * ConvenienceClass.x_movement)));
            float deltaY = (float) (parallax.getDegY() / 180.0 * (prefDepth * ConvenienceClass.y_movement));

            // Limit max offset
            if ((abs(deltaX) > deltaXMax || abs(deltaY) > deltaYMax) && prefLimit) {
                deltaArrayNew = deltaArrayOld.clone();
                break;
            }

            deltaArrayOld = deltaArrayNew.clone();

            deltaArrayNew[i][0] = (abs(deltaX) > 0.001f) ? deltaX:0f;
            deltaArrayNew[i][1] = (abs(deltaY) > 0f) ? deltaY:0f;
        }

        int layerCount;
        if (hasOverlay) {
            layerCount = textures.length - 1;
        } else {
            layerCount = textures.length - 1; //textures.length;
        }

        float[] cloneEndPos = endPos.clone();
        //int x_offset = 10;
        //int y_offset = 10;
        //int virtual_x_offset = 150;
        //int virtual_y_offset = 100;
        boolean isParallax = false;
        boolean isEdgeLighting = false;
        //boolean isEdgeLighting = true;
        for (int i = 0; i < layerCount; i++) {
            endPos[0] = deltaArrayNew[i][0] * ConvenienceClass.virtual_x_offset;
            endPos[1] = deltaArrayNew[i][1] * ConvenienceClass.virtual_y_offset;
            endPos[2] = radius;

            cloneEndPos[0] = -deltaArrayNew[i][0] * ConvenienceClass.x_offset;
            cloneEndPos[1] = -deltaArrayNew[i][1] * ConvenienceClass.y_offset;
            cloneEndPos[2] = radius;

            endPos = Normalize(endPos);
            endPos[0] *= radius;
            endPos[1] *= radius;
            endPos[2] *= radius;

            cloneEndPos = Normalize(cloneEndPos);
            cloneEndPos[0] *= radius;
            cloneEndPos[1] *= radius;
            cloneEndPos[2] *= radius;

            Matrix.setIdentityM(modelMatrix, 0);

            //if (i == 1 || layerCount == 1 && !isEdgeLighting)
            //{
            //isParallax = true;
                //isParallax = false;
                //Matrix.scaleM(modelMatrix, 0, 0.5f, 0.5f, 0);
                //Matrix.setLookAtM(viewMatrix, 0, cloneEndPos[0], cloneEndPos[1], cloneEndPos[2], 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, radius, cloneEndPos[0], cloneEndPos[1], 0f, 0f, 1.0f, 0.0f);
                //Matrix.translateM(modelMatrix, 0, -deltaArrayNew[0][0], -deltaArrayNew[0][1], 0f);
            glLayer.SetViewPos(endPos[0], endPos[1], endPos[2]);
                //Matrix.setLookAtM(viewMatrix, 0, endPos[0], endPos[1], endPos[2], 0f, 0f, 0f, 0f, 1.0f, 0.0f); *0.7f
                //Matrix.translateM(modelMatrix, 0, deltaArrayNew[i][0], deltaArrayNew[i][1], radius - cloneEndPos[2]);
                //Matrix.translateM(modelMatrix, 0, endPos[0], endPos[1], radius - endPos[2]);
                //System.out.println(endPos[0] + ", " + endPos[1] + ", " + endPos[2]);
                //System.out.println((radius - endPos[0]) + ", " + (radius - endPos[1]) + ", " + (radius - endPos[2])); radius - cloneEndPos[2]
                //glLayer.SetViewPos(endPos[0], endPos[1], endPos[2]);
                //glLayer.SetViewPos(deltaArrayNew[i][0]*0.5f, deltaArrayNew[i][1]*0.5f, endPos[2]);
                //glLayer.SetViewPos(cloneEndPos[0], cloneEndPos[1], cloneEndPos[2]);
                //glLayer.SetViewPos(0, 0, 4);
            //}
            //else if (i == 2)
            //{
            //    //isParallax = false;
            //    isParallax = true;
            //    Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, radius, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //    glLayer.SetViewPos(endPos[0], endPos[1], endPos[2]);
            //    //Matrix.scaleM(modelMatrix, 0, 0.8f, 0.8f, 0);
            //    //Matrix.setLookAtM(viewMatrix, 0, cloneEndPos[0], cloneEndPos[1], cloneEndPos[2], 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //    //Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, radius, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //    //Matrix.translateM(modelMatrix, 0, 0.1f, 0.2f, 0f);
            //    Matrix.translateM(modelMatrix, 0, deltaArrayNew[0][0], deltaArrayNew[0][1], 0f);
            //    //Matrix.setLookAtM(viewMatrix, 0, cloneEndPos[0], cloneEndPos[1], cloneEndPos[2], 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //}
            //else if (i == 0)
            //{
            //    isParallax = false;
            //    //float yscale = Math.min(2.f + rotationAngle, 2.f);
            //    Matrix.scaleM(modelMatrix, 0, 1.5f - abs(rotationAngle), 1.5f - abs(rotationAngle), 0);
            //    //Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, radius, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //    //Matrix.setLookAtM(viewMatrix, 0, deltaArrayNew[i][0], deltaArrayNew[i][1], radius, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //    //Matrix.translateM(modelMatrix, 0, deltaArrayNew[i][0], deltaArrayNew[i][1], radius - cloneEndPos[2]);
            //    //float xzoom = 0.7f;
            //    //float yzoom = 0.75f;
            //    //Matrix.frustumM(projectionMatrix, 0, -xzoom, xzoom, -yzoom, yzoom, 3, 7);
            //}
            Matrix.translateM(modelMatrix, 0, 0f, 0f, 0.5f);
            Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);
            //glLayer.DrawDisplaced(textures[i], MVPMatrix, deltaArrayNew[i], i);
            //glLayer.DrawParallaxed(textures[i], MVPMatrix, modelMatrix);
            glLayer.DrawSimple(MVPMatrix, textures[i]);

            //UtilityDrawCall(new float[]{0.0f, 0.0f}, textures[i]);

            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, 1f, 0f, 1.5f);
            Matrix.rotateM(modelMatrix, 0, -90f, 0f, 1f, 0f);
            Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);
            //glLayer.DrawSimple(MVPMatrix, ConvenienceClass.backdrop);
            UtilityDrawCall(new float[]{1.0f, 0.0f}, textures[i]);

            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, -1f, 0f, 1.5f);
            Matrix.rotateM(modelMatrix, 0, 90f, 0f, 1f, 0f);
            Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);
            //glLayer.DrawSimple(MVPMatrix, ConvenienceClass.backdrop);
            UtilityDrawCall(new float[]{1.0f, 0.0f}, textures[i]);

            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, 0f, 1f, 1.5f);
            Matrix.rotateM(modelMatrix, 0, 90f, 1f, 0f, 0f);
            Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);
            //glLayer.DrawSimple(MVPMatrix, ConvenienceClass.backdrop);
            UtilityDrawCall(new float[]{0.0f, 1.0f}, textures[i]);

            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, 0f, -1f, 1.5f);
            Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
            Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);
            //glLayer.DrawSimple(MVPMatrix, ConvenienceClass.backdrop);
            UtilityDrawCall(new float[]{0.0f, 1.0f}, textures[i]);

            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, radius, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, 0f, 0f, 1.5f);
            Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);
            //glLayer.DrawOverlay(MVPMatrix);
            //glLayer.DrawSimple(MVPMatrix, textures[i]);
            //glLayer.Draw(textures[i], modelMatrix, MVPMatrix, ConvenienceClass.height_scale, i, isParallax);
            //glLayer.DrawMirrored(MVPMatrix, new float[]{1.0f, 1.0f}, textures[i]);
        }

        // Overlay
        //if (hasOverlay) {
        //    // Has an overlay
        //    float[] layerMatrix = MVPMatrix.clone();
        //    //glLayer.draw(textures[textures.length - 1], layerMatrix, i);
        //    //glLayer.drawWithShiftedPixels(textures[textures.length - 1], layerMatrix, MVPMatrix, viewPos, textures.length - 1);
        //}
    }

    void UtilityDrawCall(float[] flip, int texture)
    {
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);

        glLayer.DrawMirrored(MVPMatrix, flip, texture);
    }

    // This method must be called every time the renderer is started or to reload the settings
    public void start() {

        reloadSettings();

        deltaInit = false;

        // Get current screen orientation
        orientation = context.getResources().getConfiguration().orientation;

        if (prefSensor) parallax.start();
    }

    // Only pauses the sensor! OpenGL view is managed elsewhere
    void stop() {
        if (prefSensor) parallax.stop();
    }

    private void reloadSettings() {
        // If preview render use provided id, else load it from settings
        if (!isPreview) {
            prefWallpaperId = sharedPreferences.getString(PREF_BACKGROUND, PREF_BACKGROUND_DEFAULT);
        }

        prefSensor = sharedPreferences.getBoolean(context.getString(R.string.pref_sensor_key), context.getResources().getBoolean(R.bool.pref_sensor_default));
        prefLimit = sharedPreferences.getBoolean(context.getString(R.string.pref_limit_key), context.getResources().getBoolean(R.bool.pref_limit_default));

        String depthString = sharedPreferences.getString(context.getString(R.string.pref_depth_key), context.getString(R.string.pref_depth_default));
        prefDepth = DEPTH_MIN + Double.parseDouble(depthString) * (DEPTH_MAX / 100.0);

        String sensitivityString = sharedPreferences.getString(context.getString(R.string.pref_sensitivity_key), context.getString(R.string.pref_sensitivity_default));
        double sensitivity = SENSITIVITY_MIN + Double.parseDouble(sensitivityString) * (SENSITIVITY_MAX / 100.0);

        String fallbackString = sharedPreferences.getString(context.getString(R.string.pref_fallback_key), context.getString(R.string.pref_fallback_default));
        double fallback = FALLBACK_MIN + Double.parseDouble(fallbackString) * (FALLBACK_MAX / 100.0);

        String zoomString = sharedPreferences.getString(context.getString(R.string.pref_zoom_key), context.getString(R.string.pref_zoom_default));
        prefZoom = (float) (ZOOM_MIN + (100 - Double.parseDouble(zoomString)) * ((ZOOM_MAX - ZOOM_MIN) / 100.0));

        prefScroll = sharedPreferences.getBoolean(context.getString(R.string.pref_scroll_key), context.getResources().getBoolean(R.bool.pref_scroll_default));

        String scrollAmountString = sharedPreferences.getString(context.getString(R.string.pref_scroll_amount_key), context.getString(R.string.pref_scroll_amount_default));
        prefScrollAmount = SCROLL_AMOUNT_MIN + Double.parseDouble(scrollAmountString) * (SCROLL_AMOUNT_MAX / 100.0);

        String dimString = sharedPreferences.getString(context.getString(R.string.pref_dim_key), context.getString(R.string.pref_dim_default));
        prefDim = (int) ((Double.parseDouble(dimString)) * (DIM_MAX / 100.0));

        // Set parallax settings
        parallax.setFallback(fallback);
        parallax.setSensitivity(sensitivity);
    }

    private void generateLayers() {
        // Clean old textures (if any) before loading the new ones
        clearTextures();

        // Assume that the layer is fallback
        int layerCount;
        isFallback = false;
        hasOverlay = true;

        //List<BackgroundHelper.Layer[]> l = new ArrayList<>();
        //l.add(new BackgroundHelper.Layer(R.drawable.mask, 0));

        if (!prefWallpaperId.equals(PREF_BACKGROUND_DEFAULT)) {
            // If the wallpaper is not the fallback one

            //layerList = BackgroundHelper.loadFromFile(prefWallpaperId, context);
            layerList = BackgroundHelper.loadFromFile(prefWallpaperId, context);
            if (layerList != null) {
                // Layer loaded correctly
                prefWallpaperId = PREF_BACKGROUND_DEFAULT;
                isFallback = false;
                layerCount = ConvenienceClass.layers; //layerList.size();
                //System.out.println(layerCount);
            } else {
                deployFallbackWallpaper();
                return;
            }
        } else {
            deployFallbackWallpaper();
            return;
        }

        // Useful info
        int width = 0;
        int height = 0;

        // Create glTexture array
        textures = new int[layerCount + 1]; //new int[layerCount + 1];
        //GLES20.glGenTextures(layerCount + 1, textures, 0); // Layer + Overlay
        GLES20.glGenTextures(layerCount + 1, textures, 0);

        Bitmap tempBitmap;
        //Bitmap graybmp = null;
        Bitmap[] graybmp = new Bitmap[layerCount];
        int[] bitmaps = new int[layerCount];
        bitmaps[0] = ConvenienceClass.bmp;
        //bitmaps[1] = ConvenienceClass.bmpone;
        //bitmaps[2] = ConvenienceClass.bmptwo;
        //bitmaps[1] = R.drawable.peek_a_boo;
        //bitmaps[0] = R.drawable.guy_with_bat;
        //bitmaps[0] = R.drawable.astronaut_with_astronaus;
        //bitmaps[0] = R.drawable.image;
        //bitmaps[1] = R.drawable.layer_two;
        //bitmaps[2] = R.drawable.layer_one;
        //bitmaps[0] = R.drawable.rock_removed;
        //bitmaps[1] = R.drawable.mask;
        //bitmaps[2] = R.drawable.rocks_nrm;
        //bitmaps[3] = R.drawable.rocks_disp;
        for (int i = 0; i < textures.length; i++) {
            if (i < textures.length - 1)
            {
                // Load bitmap
                //File bitmapFile = layerList.get(i).getFile();
                tempBitmap = BackgroundHelper.decodeScaledFromRes(context.getResources(), bitmaps[i]); //BackgroundHelper.decodeScaledFromFile(bitmapFile);
                graybmp[i] = ToGrayScale(tempBitmap);


                //BitmapFactory.Options o = new BitmapFactory.Options();
                //o.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
                //Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), ConvenienceClass.bmp, o);

                //int w = bmp.getWidth();
                //int h = bmp.getHeight();
                //tempBitmap.setcol
                //tempBitmap.getWidth();

                width = tempBitmap.getWidth();
                height = tempBitmap.getHeight();
                if(i == 0)
                {
                    float customZoomX = (float)this.width / width;//ConvenienceClass.width;
                    float customZoomY = (float)this.height / height;//ConvenienceClass.height;
                    System.out.println(customZoomX + " " + customZoomY);
                    //Matrix.frustumM(projectionMatrix, 0, -customZoomX, customZoomX, -customZoomY, customZoomY, 1, 7);
                    //Matrix.frustumM(projectionMatrix, 0, -0.65f, 0.65f, -0.7f, 0.7f, 3, 7);
                    Matrix.frustumM(projectionMatrix, 0, -0.3f, 0.3f, -0.3f, 0.3f, 1, 7);
                }
                ConvenienceClass.width = width;
                ConvenienceClass.height = height;
                //System.out.println( "Width:" + width + " Height: " + height);
                //System.out.println((float)width/height);
            }
            else
            {
                // Generate overlay
                tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                tempBitmap.eraseColor(Color.argb(prefDim, 0, 0, 0));
            }

            if (i == 0)
            {
                // Solid black background
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            }

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            try {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, tempBitmap, 0);
            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer wile genrating layers", e);
                deployFallbackWallpaper();
                return;
            }

            // Free memory
            tempBitmap.recycle();
        }

        glLayer = new GLLayer(this.context, width, height, layerCount);
        glLayer.SetDisplacementMap(graybmp);

        //glLayer = new GLLayer(this.context, false, layerCount);
        // Set the loaded wallpaper id
        loadedWallpaperId = prefWallpaperId;
    }

    Bitmap ToGrayScale(Bitmap bmp)
    {
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Bitmap grayscalebmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Bitmap blackbmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //Bitmap grayscalebmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        Canvas cnvs = new Canvas(grayscalebmp);
        Canvas blackCanvas = new Canvas(blackbmp);
        Paint paint = new Paint();
        //ColorMatrix cm = new ColorMatrix();
        ColorMatrix cm = new ColorMatrix(new float[]{
                    0.2989f, 0.5870f, 0.1140f, 0, 0,
                    0.2989f, 0.5870f, 0.1140f, 0, 0,
                    0.2989f, 0.5870f, 0.1140f, 0, 0,
                    0, 0, 0, 1, 0
                });

        ColorMatrix bcm = new ColorMatrix(new float[]{
                0f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
        });

        //float[] cma = cm.getArray();
        //for (int i = 0; i < cma.length; i++)
        //{
        //    System.out.println(cma[i]);
        //}
//
        //cm.setSaturation(0);
//
        //cma = cm.getArray();
        //for (int i = 0; i < cma.length; i++)
        //{
        //    System.out.println(cma[i]);
        //}

        ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(cm);
        ColorMatrixColorFilter bcmcf = new ColorMatrixColorFilter(bcm);

        paint.setColorFilter(cmcf);
        cnvs.drawBitmap(bmp, 0, 0, paint);

        paint.setColorFilter(bcmcf);
        blackCanvas.drawBitmap(bmp, 0, 0, paint);

        ConvenienceClass.backdrop = TextureHelper.LoadTextureFromBitmap(blackbmp);

        //int pixel;
        //for (int x = 0; x < width; x++)
        //{
        //    for (int y = 0; y < height; y++)
        //    {
        //        pixel = grayscalebmp.getPixel(x, y);

        //        int a = Color.alpha(pixel);
        //        int r = Color.red(pixel);
        //        int g = Color.green(pixel);
        //        int b = Color.blue(pixel);

        //        int gray = (int)(0.2989*r + 0.5870*g + 0.1140*b);

        //        if(gray > 180)
        //        {
        //            gray = 100;
        //            grayscalebmp.setPixel(x, y, Color.argb(a, gray, gray, gray));
        //        }
        //    }
        //}

        return grayscalebmp;
    }

    void setOffset(float offset) {
        this.offset = offset;
    }

    private void deployFallbackWallpaper() {
        clearTextures();

        isFallback = true;
        hasOverlay = false;

        Bitmap fallbackBitmap = BackgroundHelper.decodeScaledFromRes(context.getResources(), R.drawable.fallback);

        textures = new int[1];

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, fallbackBitmap, 0);

        glLayer = new GLLayer(context, true, 1);

        loadedWallpaperId = PREF_BACKGROUND_DEFAULT;
    }

    private void clearTextures() {
        if (textures != null) {
            GLES20.glDeleteTextures(textures.length, textures, 0);
        }
    }
}
