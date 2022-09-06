package com.zero.zerolivewallpaper.wallpaper;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.zero.zerolivewallpaper.R;

import com.zero.zerolivewallpaper.wallpaper.common.ConvenienceClass;
import com.zero.zerolivewallpaper.wallpaper.common.ShaderHelper;
import com.zero.zerolivewallpaper.wallpaper.common.RawResourceReader;
import com.zero.zerolivewallpaper.wallpaper.common.TextureHelper;

class GLLayer
{
    private final Context activityContext;

    private final float[] vertices = {
            //-1.0f, -1.0f,
            //1.0f, -1.0f,
            //-1.0f, 1.0f,
            ////1.0f, 1.0f,
            //-1.0f, 1.0f,
            //1.0f, -1.0f,
            //1.0f, 1.0f
            -1, 1, 0,
            -1, -1, 0,
            1, 1, 0,
            -1, -1, 0,
            1, -1, 0,
            1, 1, 0
    };

    private final float[] quadNormalData = {
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f
    };

    private final float[] textureVertices = {
            0, 0,
            0, 1,
            1, 0,
            0, 1,
            1, 1,
            1, 0
    };

    private int width;
    private int height;
    private int[] depthMap;

    protected final int bytesPerFloat = 4;

    private FloatBuffer verticesBuffer;
    private FloatBuffer normalsBuffer;
    private FloatBuffer textureBuffer;

    private float[] viewPos = new float[]{0f, 0f, 0f};

    private int vertexShader;
    private int fragmentShader;

    private int[] program;
    private int simpleShader;
    private int parallaxShader;
    private int edgeLightingShader;
    private int mirroringShader;
    private int overlayShader;
    private int displacementShader;

    public int[] sPos = new int[]{50, 50};
    public int[] ePos = new int[]{50, 50};


    private void initializeBuffers() {
        ByteBuffer buff = ByteBuffer.allocateDirect(vertices.length * bytesPerFloat);
        buff.order(ByteOrder.nativeOrder());
        verticesBuffer = buff.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);

        buff = ByteBuffer.allocateDirect(textureVertices.length * bytesPerFloat);
        buff.order(ByteOrder.nativeOrder());
        textureBuffer = buff.asFloatBuffer();
        textureBuffer.put(textureVertices);
        textureBuffer.position(0);
    }

    private void initializeAllBuffers()
    {
        initializeBuffers();
        ByteBuffer buff = ByteBuffer.allocateDirect(quadNormalData.length * bytesPerFloat);
        buff.order(ByteOrder.nativeOrder());
        normalsBuffer = buff.asFloatBuffer();
        normalsBuffer.put(quadNormalData);
        normalsBuffer.position(0);
    }

    private void initializeProgram()
    {
        final String vShader = GetSimpleVertexShader();
        final String fShader = GetSimpleFragmentShader();

        vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vShader);
        fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fShader);

        simpleShader = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"aPosition", "aTexPosition"});
    }

    private void initializePixelShifterProgram()
    {
        final String vShader = GetPixelShifterVertexShader();
        final String fShader = GetPixelShifterFragmentShader();

        vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vShader);
        fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fShader);

        parallaxShader = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"a_Position", "a_Normal", "a_TexCoordinate"});
    }

    private void initializeEdgeLightingShader()
    {
        final String vShader = GetEdgeLightingVertexShader();
        final String fShader = GetEdgeLightingFragmentShader();

        vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vShader);
        fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fShader);

        edgeLightingShader = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"a_Position", "a_TextureCoordinates"});
    }

    private void InitializeMirroringShader()
    {
        final String vShader = GetMirroringVertexShader();
        final String fShader = GetMirroringFragmentShader();

        vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vShader);
        fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fShader);

        mirroringShader = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"a_Position", "a_TexturePosition"});
    }

    private void InitializeOverlayShader()
    {
        final String vShader = GetOverlayVertexShader();
        final String fShader = GetOverlayFragmentShader();

        vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vShader);
        fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fShader);

        overlayShader = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"a_Position", "a_TexturePosition"});
    }

    private void InitializeDisplacementShader()
    {
        final String vShader = GetDisplacementVertexShader();
        final String fShader = GetDisplacementFragmentShader();

        vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vShader);
        fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fShader);

        displacementShader = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"a_Position", "a_TexturePosition"});
    }

    void ExecuteSimpleShader(int texture, float[] mvpMatrix)
    {
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        int positionHandle = GLES20.glGetAttribLocation(simpleShader, "aPosition");
        int textureHandle = GLES20.glGetUniformLocation(simpleShader, "uTexture");
        int texturePositionHandle = GLES20.glGetAttribLocation(simpleShader, "aTexPosition");
        int MVPMatrixHandle = GLES20.glGetUniformLocation(simpleShader, "uMVPMatrix");

        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(texturePositionHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    void ExecuteMirroringShader(float[] mvpMatrix, float[] flip, int texture)
    {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        int positionHandle = GLES20.glGetAttribLocation(mirroringShader, "a_Position");
        int texturePositionHandle = GLES20.glGetAttribLocation(mirroringShader, "a_TexturePosition");
        int textureHandle = GLES20.glGetUniformLocation(mirroringShader, "u_Texture");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(mirroringShader, "u_MVPMatrix");
        int flipHandle = GLES20.glGetUniformLocation(mirroringShader, "u_Flip");
        int reflectionColorHandle = GLES20.glGetUniformLocation(mirroringShader, "u_ReflectionColor");

        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(texturePositionHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniform4f(reflectionColorHandle, ConvenienceClass.reflectionColor[0], ConvenienceClass.reflectionColor[1], ConvenienceClass.reflectionColor[2], ConvenienceClass.reflectionColor[3]);

        GLES20.glUniform2f(flipHandle, flip[0], flip[1]);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    void ExecuteOverlayShader(float[] mvpMatrix)
    {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        int positionHandle = GLES20.glGetAttribLocation(overlayShader, "a_Position");
        int texturePositionHandle = GLES20.glGetAttribLocation(overlayShader, "a_TexturePosition");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(overlayShader, "u_MVPMatrix");
        int overlayColorHandle = GLES20.glGetUniformLocation(overlayShader, "u_OverlayColor");

        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(texturePositionHandle);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniform4f(overlayColorHandle, ConvenienceClass.overlayColor[0], ConvenienceClass.overlayColor[1], ConvenienceClass.overlayColor[2], ConvenienceClass.overlayColor[3]);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    void ExecuteExtrusionShader(int texture, float[] mvpMatrix, float[] modelMatrix, float heightScale, int i)
    {
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        int positionHandle = GLES20.glGetAttribLocation(parallaxShader, "a_Position");
        int textureCoordinateHandle = GLES20.glGetAttribLocation(parallaxShader, "a_TexCoordinate");
        int normalHandle = GLES20.glGetAttribLocation(parallaxShader, "a_Normal");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(parallaxShader, "u_MVPMatrix");
        int mMatrixHandle = GLES20.glGetUniformLocation(parallaxShader, "u_ModelMatrix");
        int viewPositionHandle = GLES20.glGetUniformLocation(parallaxShader, "u_ViewPosition");
        int heightScaleHandle = GLES20.glGetUniformLocation(parallaxShader, "u_HeightScale");
        int textureDiffusedHandle = GLES20.glGetUniformLocation(parallaxShader, "u_Texture");
        int depthMapHandle = GLES20.glGetUniformLocation(parallaxShader, "u_DepthMap");

        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        // Passing in the textureDiffusedHandle handle to u_Texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(textureDiffusedHandle, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, depthMap[i]);
        GLES20.glUniform1i(depthMapHandle, 1);

        // Passing in the positionHandle yada yada
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalsBuffer);
        GLES20.glEnableVertexAttribArray(normalHandle);
        // First MVPHandle then MMHandle;
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, modelMatrix, 0);

        GLES20.glUniform3f(viewPositionHandle, viewPos[0], viewPos[1], viewPos[2]);

        GLES20.glUniform1f(heightScaleHandle, heightScale);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    void ExecuteDisplacementShader(float[] mvpMatrix, float[] gyro, int texture, int i)
    {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        int positionHandle = GLES20.glGetAttribLocation(displacementShader, "a_Position");
        int textureCoordinateHandle = GLES20.glGetAttribLocation(displacementShader, "a_TexturePosition");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(displacementShader, "u_MVPMatrix");
        int gyroHandle = GLES20.glGetUniformLocation(displacementShader, "u_Gyro");
        int textureHandle = GLES20.glGetUniformLocation(displacementShader, "u_Texture");
        int depthMapHandle = GLES20.glGetUniformLocation(displacementShader, "u_DepthMap");

        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, depthMap[i]);
        GLES20.glUniform1i(depthMapHandle, 1);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniform2f(gyroHandle, gyro[0], gyro[1]);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    void ExecuteEdgeLightingShader(float[] mvpMatrix, float displacement)
    {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        int positionHandle = GLES20.glGetAttribLocation(edgeLightingShader, "a_Position");
        int textureCoordinateHandle = GLES20.glGetAttribLocation(edgeLightingShader, "a_TextureCoordinates");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(edgeLightingShader, "u_MVPMatrix");
        int xDisplacementHandle = GLES20.glGetUniformLocation(edgeLightingShader, "u_XDisplacement");
        int yDisplacementHandle = GLES20.glGetUniformLocation(edgeLightingShader, "u_YDisplacement");
        int startPosHandle = GLES20.glGetUniformLocation(edgeLightingShader, "u_StartPos");
        int endPosHandle = GLES20.glGetUniformLocation(edgeLightingShader, "u_EndPos");

        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glUniform2f(startPosHandle, sPos[0], sPos[1]);
        GLES20.glUniform2f(endPosHandle, ePos[0], ePos[1]);

        GLES20.glUniform1f(xDisplacementHandle, Math.abs(displacement));
        GLES20.glUniform1f(yDisplacementHandle, 1 - Math.abs(displacement));

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    void draw(int texture, float[] mvpMatrix, int i) {
        //GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        //GLES20.glUseProgram(program[0]);
//
        //GLES20.glEnable(GLES20.GL_BLEND);
//
        //// Note: android uses premultiplied alpha for bitmaps!
        //GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//
        //int positionHandle = GLES20.glGetAttribLocation(program[0], "aPosition");
        //int textureHandle = GLES20.glGetUniformLocation(program[0], "uTexture");
        //int texturePositionHandle = GLES20.glGetAttribLocation(program[0], "aTexPosition");
        //int MVPMatrixHandle = GLES20.glGetUniformLocation(program[0], "uMVPMatrix");
//
        //GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        //GLES20.glEnableVertexAttribArray(texturePositionHandle);
//
        //GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        //GLES20.glUniform1i(textureHandle, 0);
//
        //GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        //GLES20.glEnableVertexAttribArray(positionHandle);
//
        //GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0);
//
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
//
        //GLES20.glDisable(GLES20.GL_BLEND);
    }

    void Draw(int texture, float[] modelMatrix, float[] mvpMatrix, float heightScale, float i, boolean isParallax)
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        if (isParallax)
        {
            GLES20.glUseProgram(parallaxShader);
            ExecuteExtrusionShader(texture, mvpMatrix, modelMatrix, heightScale, (int)i);
        }
        else
        {
            //long time = SystemClock.uptimeMillis() % 100L;
            //int ohmsPassed = (int)time/100;
            //EdgeLighting.LoopWithFixedStartingPoint(sPos, ePos, ohmsPassed);
            //GLES20.glUseProgram(edgeLightingShader);
            //ExecuteEdgeLightingShader(mvpMatrix, i);
            GLES20.glUseProgram(simpleShader);
            ExecuteSimpleShader(texture, mvpMatrix);
        }

    }

    void DrawSimple(float[] mvpMatrix, int texture)
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glUseProgram(simpleShader);
        ExecuteSimpleShader(texture, mvpMatrix);
    }

    void DrawParallaxed(int texture, float[] mvpMatrix, float[] modelMatrix)
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glUseProgram(parallaxShader);
        ExecuteExtrusionShader(texture, mvpMatrix, modelMatrix, ConvenienceClass.height_scale, 0);
    }

    void DrawMirrored(float[] mvpMatrix, float[] flip, int texture)
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glUseProgram(mirroringShader);
        ExecuteMirroringShader(mvpMatrix, flip, texture);
    }

    void DrawOverlay(float[] mvpMatrix)
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glUseProgram(overlayShader);
        ExecuteOverlayShader(mvpMatrix);
    }

    void DrawDisplaced(int texture, float[] mvpMatrix, float[] gyro, int i)
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glUseProgram(displacementShader);
        ExecuteDisplacementShader(mvpMatrix, gyro, texture, i);
    }

    void SetViewPos(float x, float y, float z)
    {
        viewPos[0] = x;
        viewPos[1] = y;
        viewPos[2] = z;
    }

    void SetDisplacementMap(Bitmap[] dispMap)
    {
        //depthMap = TextureHelper.LoadTextureFromBitmap(dispMap);
        for (int i = 0; i < dispMap.length; i++)
        {
            //if (i == 1)
            //{
            //    depthMap[i] = TextureHelper.loadTexture(MyApplication.getInstance(), R.drawable.bird_midground_spec_disp);
            //    continue;
            //}
            depthMap[i] = TextureHelper.LoadTextureFromBitmap(dispMap[i]);
            //depthMap[i] = TextureHelper.loadTexture(MyApplication.getInstance(), R.drawable.guy_with_bat_disp);
        }
        System.out.println("********************************************************* " + dispMap.length);
        //depthMap = TextureHelper.loadTexture(MyApplication.getInstance(), R.drawable.guy_with_bat_disp);
    }

    protected String GetSimpleVertexShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.texture_vertex_shader);
    }

    protected String GetSimpleFragmentShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.texture_fragment_shader);
    }

    protected String GetPixelShifterVertexShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.texture_shifter_vertex);
    }

    protected String GetPixelShifterFragmentShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.texture_shifter_fragment);
    }

    protected String GetEdgeLightingVertexShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.edge_vertex_shader);
    }

    protected String GetEdgeLightingFragmentShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.edge_frgament_shader);
    }

    protected String GetMirroringVertexShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.mirror_vertex);
    }

    protected String GetMirroringFragmentShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.mirror_fragment);
    }

    protected String GetOverlayVertexShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.overlay_vertex);
    }

    protected String GetOverlayFragmentShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.overlay_fragment);
    }

    protected String GetDisplacementVertexShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.texture_displacement_vertex_shader);
    }

    protected String GetDisplacementFragmentShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.texture_displacement_fragment_shader);
    }

    GLLayer(Context c_activityContext, boolean fallBack, int numberOfShaders) {
        this.activityContext = c_activityContext;
        this.program = new int[numberOfShaders];
        initializeBuffers();
        initializeProgram();
    }

    GLLayer(Context c_activityContext, int width, int height, int numberOfLayers)
    {
        this.width = width;
        this.height = height;
        this.program = new int[numberOfLayers];
        this.depthMap = new int[numberOfLayers];
        this.activityContext = c_activityContext;
        initializeAllBuffers();
        initializeProgram();
        InitializeMirroringShader();
        InitializeOverlayShader();
        InitializeDisplacementShader();
        initializePixelShifterProgram();
        initializeEdgeLightingShader();
    }

}