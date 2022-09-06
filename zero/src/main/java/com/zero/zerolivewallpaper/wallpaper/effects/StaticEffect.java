package com.zero.zerolivewallpaper.wallpaper.effects;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.zero.zerolivewallpaper.wallpaper.common.MyApplication;
import com.zero.zerolivewallpaper.wallpaper.common.RawResourceReader;
import com.zero.zerolivewallpaper.R;
import com.zero.zerolivewallpaper.wallpaper.common.ShaderHelper;
import com.zero.zerolivewallpaper.wallpaper.common.TextureHelper;
import com.zero.zerolivewallpaper.wallpaper.BackgroundHelper;

public class StaticEffect extends Effect
{
    protected String GetVertexShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.texture_vertex_shader);
    }

    protected String GetFragmentShader()
    {
        return RawResourceReader.readTextFileFromRawResource(R.raw.texture_fragment_shader);
    }

    public void InitializeShader()
    {
        final String vShader = GetVertexShader();
        final String fShader = GetFragmentShader();

        int vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vShader);
        int fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fShader);

        effectShader = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"aPosition", "aTexPosition"});
    }

    public void ExecuteShader()
    {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        int positionHandle = GLES20.glGetAttribLocation(effectShader, "aPosition");
        int textureHandle = GLES20.glGetUniformLocation(effectShader, "uTexture");
        int texturePositionHandle = GLES20.glGetAttribLocation(effectShader, "aTexPosition");
        int MVPMatrixHandle = GLES20.glGetUniformLocation(effectShader, "uMVPMatrix");

        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(texturePositionHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glUniform1f(textureHandle, 0);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, MVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public void Draw()
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);


        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(effectShader);
        ExecuteShader();
    }

    public void InitializeEffect(int height, int width)
    {
        float imgAspectRatio = (float)imgWidth / (float)imgHeight;
        float viewAspectRatio = (float)width / (float)height;
        float xScale = 1.0f;
        float yScale = 1.0f;

        if (imgAspectRatio > viewAspectRatio) {
            yScale = viewAspectRatio / imgAspectRatio;
            System.out.println("++++++++++++++++++ yo");
        } else {
            xScale = imgAspectRatio / viewAspectRatio;
            System.out.println("++++++++++++++++++ bo");
        }
        xScale = (float)width / (float)imgWidth;
        yScale = (float)height / (float)imgHeight;
        Matrix.frustumM(projectionMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, 1, 100);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f);

        InitializeBuffers();
        InitializeShader();
    }

    public void LoadBmp(String uri)
    {
        textures = new int[1];
        int bmp = R.drawable.chip_one;
        Bitmap bitmap = BackgroundHelper.decodeScaledFromRes(MyApplication.getInstance().getResources(), bmp);

        imgWidth = bitmap.getWidth();
        imgHeight = bitmap.getHeight();

        textures[0] = TextureHelper.LoadTextureFromBitmap(bitmap);
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
//            System.out.println("Null pointer exception while generating texture");
//            return;
//        }

    }
}
