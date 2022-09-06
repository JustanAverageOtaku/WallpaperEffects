package com.zero.zerolivewallpaper.wallpaper.effects;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.zero.zerolivewallpaper.wallpaper.common.ConvenienceClass;
import com.zero.zerolivewallpaper.wallpaper.common.MyApplication;
import com.zero.zerolivewallpaper.wallpaper.common.RawResourceReader;
import com.zero.zerolivewallpaper.R;
import com.zero.zerolivewallpaper.wallpaper.common.ShaderHelper;
import com.zero.zerolivewallpaper.wallpaper.common.TextureHelper;
import com.zero.zerolivewallpaper.wallpaper.BackgroundHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ParallaxEffect extends EffectWithParalax 
{
    private final float[] quadNormalData = {
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f
    };

    private FloatBuffer normalsBuffer;
    private float[] viewPos = new float[]{0f, 0f, 4f};
    
    private void InitializeNormalBuffers()
    {
        ByteBuffer buff = ByteBuffer.allocateDirect(quadNormalData.length * bytesPerFloat);
        buff.order(ByteOrder.nativeOrder());
        normalsBuffer = buff.asFloatBuffer();
        normalsBuffer.put(quadNormalData);
        normalsBuffer.position(0);
    }
    
    public String GetVertexShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.texture_shifter_vertex);
    }
    
    public String GetFragmentShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.texture_shifter_fragment);
    }
    
    public void InitializeShader()
    {
        final String vShader = GetVertexShader();
        final String fShader = GetFragmentShader();

        int vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vShader);
        int fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fShader);

        effectShader = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"a_Position", "a_Normal", "a_TexCoordinate"});
    }
    
    public void ExecuteShader()
    {
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        int positionHandle = GLES20.glGetAttribLocation(effectShader, "a_Position");
        int textureCoordinateHandle = GLES20.glGetAttribLocation(effectShader, "a_TexCoordinate");
        int normalHandle = GLES20.glGetAttribLocation(effectShader, "a_Normal");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(effectShader, "u_MVPMatrix");
        int mMatrixHandle = GLES20.glGetUniformLocation(effectShader, "u_ModelMatrix");
        int viewPositionHandle = GLES20.glGetUniformLocation(effectShader, "u_ViewPosition");
        int heightScaleHandle = GLES20.glGetUniformLocation(effectShader, "u_HeightScale");
        int textureDiffusedHandle = GLES20.glGetUniformLocation(effectShader, "u_Texture");
        int depthMapHandle = GLES20.glGetUniformLocation(effectShader, "u_DepthMap");

        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        // Passing in the textureDiffusedHandle handle to u_Texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glUniform1i(textureDiffusedHandle, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);
        GLES20.glUniform1i(depthMapHandle, 1);

        // Passing in the positionHandle yada yada
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalsBuffer);
        GLES20.glEnableVertexAttribArray(normalHandle);
        // First MVPHandle then MMHandle;
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, MVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, modelMatrix, 0);

        GLES20.glUniform3f(viewPositionHandle, viewPos[0], viewPos[1], viewPos[2]);

        GLES20.glUniform1f(heightScaleHandle, ConvenienceClass.height_scale);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
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
    public void Draw()
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//        double z = 60;
//        float radius = 4.0f;
//        deltaArrayNew = new float[1][2];
//
//        float deltaX = (float) -((parallax.getDegX() / 180.0 * (ConvenienceClass.x_movement)));
//        float deltaY = (float) (parallax.getDegY() / 180.0 * (ConvenienceClass.y_movement));
//
//        deltaArrayNew[0][0] = deltaX;
//        deltaArrayNew[0][1] = deltaY;
//
//        float[] endPos = new float[]{0.0f, 0.0f, 4.0f};
//        float[] cloneEndPos = endPos.clone();
//
//        endPos[0] = deltaArrayNew[0][0] * ConvenienceClass.virtual_x_offset;
//        endPos[1] = deltaArrayNew[0][1] * ConvenienceClass.virtual_y_offset;
//        endPos[2] = radius;
//
//        cloneEndPos[0] = -deltaArrayNew[0][0] * ConvenienceClass.x_offset;
//        cloneEndPos[1] = -deltaArrayNew[0][1] * ConvenienceClass.y_offset;
//        cloneEndPos[2] = radius;
//
//        endPos = Normalize(endPos);
//        endPos[0] *= radius;
//        endPos[1] *= radius;
//        endPos[2] *= radius;
//
//        cloneEndPos = Normalize(cloneEndPos);
//        cloneEndPos[0] *= radius;
//        cloneEndPos[1] *= radius;
//        cloneEndPos[2] *= radius;
//
//        Matrix.setIdentityM(modelMatrix, 0);
//
//        viewPos = endPos;
        Matrix.translateM(modelMatrix, 0, 0f, 0f, 4f);
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(effectShader);
        ExecuteShader();
    }

    @Override
    public void InitializeEffect(int height, int width)
    {
        Matrix.frustumM(projectionMatrix, 0, -0.7f, 0.7f, -0.7f, 0.7f, 3, 7);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 4f, 0f, 0f, 0f, 0f, 1f, 0f);

        double fallback = 0.0 + 50 * (0.05 / 100.0);
        double sensitivity = 0.1 + 50 * (0.5 / 100.0);
        parallax.setFallback(fallback);
        parallax.setSensitivity(sensitivity);
        parallax.start();

        InitializeBuffers();
        InitializeNormalBuffers();

        InitializeShader();
    }

    public void LoadBmp(String uri)
    {
        textures = new int[2];
        int bmp = R.drawable.chip_one;
        Bitmap bitmap = BackgroundHelper.decodeScaledFromRes(MyApplication.getInstance().getResources(), bmp);
        Bitmap grayBitmap = ToGrayScale(bitmap);

        textures[1] = TextureHelper.LoadTextureFromBitmap(grayBitmap);
    }

    Bitmap ToGrayScale(Bitmap bmp)
    {
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Bitmap grayscalebmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Bitmap blackbmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas cnvs = new Canvas(grayscalebmp);
        Canvas blackCanvas = new Canvas(blackbmp);
        Paint paint = new Paint();

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

        ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(cm);
        ColorMatrixColorFilter bcmcf = new ColorMatrixColorFilter(bcm);

        paint.setColorFilter(cmcf);
        cnvs.drawBitmap(bmp, 0, 0, paint);

        paint.setColorFilter(bcmcf);
        blackCanvas.drawBitmap(bmp, 0, 0, paint);

        ConvenienceClass.backdrop = TextureHelper.LoadTextureFromBitmap(blackbmp);

        return grayscalebmp;
    }
}
