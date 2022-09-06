package com.zero.zerolivewallpaper.wallpaper;

import android.content.Context;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import android.util.Log;

import com.zero.zerolivewallpaper.wallpaper.effects.Effect;
import com.zero.zerolivewallpaper.wallpaper.effects.MirrorEffect;
import com.zero.zerolivewallpaper.wallpaper.effects.MultiLayerEffect;
import com.zero.zerolivewallpaper.wallpaper.effects.StaticEffect;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WallpaperRenderer implements GLSurfaceView.Renderer
{
    private static final String TAG = "Renderer";

    private final Context context;

    private final Effect currentEffect;

    private int width;
    private int height;
    private int textures[];
    private int effectId;
    private String uri;

    class EffectResolver
    {
        private Map<Integer, Effect> effectMap = new HashMap<Integer, Effect>();

        public EffectResolver()
        {
            effectMap.put(1, new StaticEffect());
            effectMap.put(3, new MirrorEffect());
            effectMap.put(4, new MultiLayerEffect());
        }

        public Effect ResolveEffectType(int effectId)
        {
            return effectMap.get(effectId);
        }
    }
    //enum Effect
    //{
    //    EFFECT_STATIC(0),
    //    EFFECT_MIRROR(1),
    //    EFFECT_MULTILAYER(2),
    //    EFFECT_LIVE_WALLPAPER(3),
    //    EFFECT_PARALLAX(4);
//
    //    private int value;
//
    //    private Effect(int val)
    //    {
    //        value = val;
    //    }
//
    //    public int getValue()
    //    {
    //        return value;
    //    }
    //}

    //private Effect currentEffect;


    WallpaperRenderer(Context context)
    {
        this.context = context;

        //currentEffect = new ParallaxEffect();
        //currentEffect = new StaticEffect();
        //currentEffect = new MirrorEffect();
        EffectResolver er = new EffectResolver();

        currentEffect = er.ResolveEffectType(3);

        //currentEffect = new MultiLayerEffect();

        Log.e(TAG, "******** Current Effect Set");
    }

    // Preview Constructor
    WallpaperRenderer(Context context, int effectID, String uri)
    {
        this.context = context;
        this.effectId = effectID;
        this.uri = uri;

        EffectResolver er = new EffectResolver();

        currentEffect = er.ResolveEffectType(3);
    }

    //public interface GetRenderer
    //{
//
    //}

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig)
    {
        // Nothing
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1)
    {
        GLES20.glViewport(0, 0, i, i1);

        width = i;
        height = i1;

        currentEffect.LoadBmp("iamuri");
        Log.e(TAG, "****** Bitmap Loaded");
        currentEffect.InitializeEffect(width, height);
        Log.e(TAG, "****** Current Effect Initialized");
        //Matrix.frustumM();
    }

    @Override
    public void onDrawFrame(GL10 gl10)
    {
        currentEffect.Draw();
    }

    public void start()
    {
        //currentEffect.LoadBmp("iamuri");
        //currentEffect.InitializeEffect(width, height);
    }

    void stop()
    {

    }

    void setOffset(float offset)
    {
        if(currentEffect.isScrollable)
        {
            currentEffect.offset = offset;
        }
    }

//    void LoadBitmap()
//    {
//        textures = new int[2];
//        int bmp = R.drawable.chip_one;
//        Bitmap bitmap = BackgroundHelper.decodeScaledFromRes(context.getResources(), bmp);
//        Bitmap grayBitmap = ToGrayScale(bitmap);
//
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//
//        try {
//            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//        } catch (NullPointerException e) {
//            Log.e(TAG, "Null pointer exception while generating texture", e);
//            return;
//        }
//
//        textures[1] = TextureHelper.LoadTextureFromBitmap(grayBitmap);
//        currentEffect.SetTextures(textures);
//    }

//    Bitmap ToGrayScale(Bitmap bmp)
//    {
//        int width = bmp.getWidth();
//        int height = bmp.getHeight();
//
//        Bitmap grayscalebmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Bitmap blackbmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        //Bitmap grayscalebmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
//
//        Canvas cnvs = new Canvas(grayscalebmp);
//        Canvas blackCanvas = new Canvas(blackbmp);
//        Paint paint = new Paint();
//        //ColorMatrix cm = new ColorMatrix();
//        ColorMatrix cm = new ColorMatrix(new float[]{
//                0.2989f, 0.5870f, 0.1140f, 0, 0,
//                0.2989f, 0.5870f, 0.1140f, 0, 0,
//                0.2989f, 0.5870f, 0.1140f, 0, 0,
//                0, 0, 0, 1, 0
//        });
//
//        ColorMatrix bcm = new ColorMatrix(new float[]{
//                0f, 0f, 0f, 0f, 0f,
//                0f, 0f, 0f, 0f, 0f,
//                0f, 0f, 0f, 0f, 0f,
//                0f, 0f, 0f, 1f, 0f
//        });
//
//        //float[] cma = cm.getArray();
//        //for (int i = 0; i < cma.length; i++)
//        //{
//        //    System.out.println(cma[i]);
//        //}
////
//        //cm.setSaturation(0);
////
//        //cma = cm.getArray();
//        //for (int i = 0; i < cma.length; i++)
//        //{
//        //    System.out.println(cma[i]);
//        //}
//
//        ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(cm);
//        ColorMatrixColorFilter bcmcf = new ColorMatrixColorFilter(bcm);
//
//        paint.setColorFilter(cmcf);
//        cnvs.drawBitmap(bmp, 0, 0, paint);
//
//        paint.setColorFilter(bcmcf);
//        blackCanvas.drawBitmap(bmp, 0, 0, paint);
//
//        ConvenienceClass.backdrop = TextureHelper.LoadTextureFromBitmap(blackbmp);
//
//        //int pixel;
//        //for (int x = 0; x < width; x++)
//        //{
//        //    for (int y = 0; y < height; y++)
//        //    {
//        //        pixel = grayscalebmp.getPixel(x, y);
//
//        //        int a = Color.alpha(pixel);
//        //        int r = Color.red(pixel);
//        //        int g = Color.green(pixel);
//        //        int b = Color.blue(pixel);
//
//        //        int gray = (int)(0.2989*r + 0.5870*g + 0.1140*b);
//
//        //        if(gray > 180)
//        //        {
//        //            gray = 100;
//        //            grayscalebmp.setPixel(x, y, Color.argb(a, gray, gray, gray));
//        //        }
//        //    }
//        //}
//
//        return grayscalebmp;
//    }
}
