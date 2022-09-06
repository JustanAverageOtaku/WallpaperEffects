package com.zero.zerolivewallpaper.wallpaper.effects;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.zero.zerolivewallpaper.R;
import com.zero.zerolivewallpaper.wallpaper.common.ConvenienceClass;
import com.zero.zerolivewallpaper.wallpaper.common.MyApplication;
import com.zero.zerolivewallpaper.wallpaper.common.RawResourceReader;
import com.zero.zerolivewallpaper.wallpaper.common.ShaderHelper;
import com.zero.zerolivewallpaper.wallpaper.common.TextureHelper;
import com.zero.zerolivewallpaper.wallpaper.BackgroundHelper;

public class MultiLayerEffect extends EffectWithParalax
{
    private int currentTexture = 0;
    float[] reflectionColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    float[] flip = new float[]{0.0f, 0.0f};

    protected String GetVertexShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.mirror_vertex);
    }

    protected String GetFragmentShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.mirror_fragment);
    }

    public void InitializeShader()
    {
        final String vShader = GetVertexShader();
        final String fShader = GetFragmentShader();

        int vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vShader);
        int fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fShader);

        effectShader = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"a_Position", "a_TexturePosition"});
    }

    public void ExecuteShader()
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(effectShader);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        int positionHandle = GLES20.glGetAttribLocation(effectShader, "a_Position");
        int texturePositionHandle = GLES20.glGetAttribLocation(effectShader, "a_TexturePosition");
        int textureHandle = GLES20.glGetUniformLocation(effectShader, "u_Texture");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(effectShader, "u_MVPMatrix");
        int flipHandle = GLES20.glGetUniformLocation(effectShader, "u_Flip");
        int reflectionColorHandle = GLES20.glGetUniformLocation(effectShader, "u_ReflectionColor");

        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(texturePositionHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, currentTexture);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, MVPMatrix, 0);
        GLES20.glUniform4f(reflectionColorHandle, reflectionColor[0], reflectionColor[1], reflectionColor[2], reflectionColor[3]);

        GLES20.glUniform2f(flipHandle, flip[0], flip[1]);

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

    public void Draw()
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        float radius = 2.0f; //4.0f;

        //deltaArrayNew = new float[1][2];

        float deltaX = (float) -(offset + (parallax.getDegX() / (180 * 0.5f)));
        float deltaY = (float) (parallax.getDegY() / (180.0 * ConvenienceClass.y_movement));

        deltaArrayNew[0][0] = deltaX;
        deltaArrayNew[0][1] = deltaY;

        float[] endPos = new float[]{0.0f, 0.0f, radius};
        float[] cloneEndPos = endPos.clone();

        endPos[0] = deltaArrayNew[0][0] * ConvenienceClass.virtual_x_offset;
        endPos[1] = deltaArrayNew[0][1] * ConvenienceClass.virtual_y_offset;
        endPos[2] = radius;

        cloneEndPos[0] = -deltaArrayNew[0][0] * ConvenienceClass.x_offset;
        cloneEndPos[1] = -deltaArrayNew[0][1] * ConvenienceClass.y_offset;
        cloneEndPos[2] = radius;

        endPos = Normalize(endPos);
        endPos[0] *= radius;
        endPos[1] *= radius;
        endPos[2] *= radius;

        cloneEndPos = Normalize(cloneEndPos);
        cloneEndPos[0] *= radius;
        cloneEndPos[1] *= radius;
        cloneEndPos[2] *= radius;

        currentTexture = textures[0];
        Matrix.setLookAtM(viewMatrix, 0, deltaX, deltaY, radius, 0f, 0f, 0f, 0f, 1f, 0f);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, 0f);
        Matrix.scaleM(modelMatrix, 0, 3f, 3f, 0f);
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);
        ExecuteShader();

        currentTexture = textures[1];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.setLookAtM(viewMatrix, 0, deltaX * 2f, deltaY * 2f, radius, 0f, 0f, 0f, 0f, 1f, 0f);
        Matrix.translateM(modelMatrix, 0, 0, 0f, 0f);
        Matrix.scaleM(modelMatrix, 0, 2.5f, 2.5f, 0f);
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);
        ExecuteShader();
        //System.out.println(cloneEndPos[2]);
        currentTexture = textures[2];
        Matrix.setLookAtM(viewMatrix, 0, cloneEndPos[0], cloneEndPos[1], (radius * 2.0f) - cloneEndPos[2], 0f, 0f, 0f, 0f, 1f, 0f);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, -deltaX, 0f, 0f);
        Matrix.scaleM(modelMatrix, 0, 2.5f, 2.5f, 0f);
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);
        ExecuteShader();

        currentTexture = textures[3];
        Matrix.setLookAtM(viewMatrix, 0, deltaX, deltaY, (radius * 2.0f) - cloneEndPos[2], 0f, 0f, 0f, 0f, 1f, 0f);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, -deltaX * 1.5f, 0f, 0f);
        Matrix.scaleM(modelMatrix, 0, 2.5f, 2.5f, 0f);
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);
        ExecuteShader();
    }

    public void InitializeEffect(int height, int width)
    {
        //Matrix.frustumM(projectionMatrix, 0, -0.27f, 0.27f, -0.3f, 0.3f, 1, 7);
        //Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 4f, 0f, 0f, 0f, 0f, 1f, 0f);
        Matrix.frustumM(projectionMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, 1, 100);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 2f, 0f, 0f, 0f, 0f, 1f, 0f);

        double fallback = 0.0 + 50 * (0.05 / 100.0);
        double sensitivity = 0.1 + 50 * (0.5 / 100.0);
        parallax.setFallback(fallback);
        parallax.setSensitivity(sensitivity);
        parallax.start();

        InitializeBuffers();
        //InitializeNormalBuffers();

        InitializeShader();
    }

    public void LoadBmp(String uri)
    {
        textures = new int[4];
        int bmp = R.drawable.base_layer_skull;
        Bitmap bitmap = BackgroundHelper.decodeScaledFromRes(MyApplication.getInstance().getResources(), bmp);
        textures[0] = TextureHelper.LoadTextureFromBitmap(bitmap);

        bmp = R.drawable.money_layer_skull;
        bitmap = BackgroundHelper.decodeScaledFromRes(MyApplication.getInstance().getResources(), bmp); //Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        textures[1] = TextureHelper.LoadTextureFromBitmap(bitmap);

        bmp = R.drawable.skull_layer_skull;
        bitmap = BackgroundHelper.decodeScaledFromRes(MyApplication.getInstance().getResources(), bmp);
        textures[2] = TextureHelper.LoadTextureFromBitmap(bitmap);

        bmp = R.drawable.lightning_layer_skull;
        bitmap = BackgroundHelper.decodeScaledFromRes(MyApplication.getInstance().getResources(), bmp);
        textures[3] = TextureHelper.LoadTextureFromBitmap(bitmap);
    }
}
