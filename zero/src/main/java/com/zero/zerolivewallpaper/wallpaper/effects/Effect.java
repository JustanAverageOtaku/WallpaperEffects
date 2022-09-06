package com.zero.zerolivewallpaper.wallpaper.effects;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class Effect
{
    public boolean isScrollable = false;

    protected float imgWidth = 0.0f;
    protected float imgHeight = 0.0f;

    protected final float[] MVPMatrix = new float[16];
    protected final float[] viewMatrix = new float[16];
    protected final float[] projectionMatrix = new float[16];

    protected int[] textures;

    protected final float[] vertices = {
            -1, 1, 0,
            -1, -1, 0,
            1, 1, 0,
            -1, -1, 0,
            1, -1, 0,
            1, 1, 0
    };

    protected final float[] textureVertices = {
            0, 0,
            0, 1,
            1, 0,
            0, 1,
            1, 1,
            1, 0
    };

    public double offset = 0;

    protected final int bytesPerFloat = 4;

    protected FloatBuffer verticesBuffer;
    protected FloatBuffer textureBuffer;

    protected int effectShader;

    protected void InitializeBuffers()
    {
        ByteBuffer buffer = ByteBuffer.allocateDirect(vertices.length * bytesPerFloat);
        buffer.order(ByteOrder.nativeOrder());
        verticesBuffer = buffer.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);

        buffer = ByteBuffer.allocateDirect(textureVertices.length * bytesPerFloat);
        buffer.order(ByteOrder.nativeOrder());
        textureBuffer = buffer.asFloatBuffer();
        textureBuffer.put(textureVertices);
        textureBuffer.position(0);
    }

    public void SetTextures(int[] ts)
    {
        textures = ts;
    }

    public void InitializeShader()
    {
        System.out.println("Please Override Me!");
    }

    public void ExecuteShader()
    {
        System.out.println("Please Override Me!");
    }

    protected String GetVertexShader()
    {
        return "Please Override Me!";
    }

    protected String GetFragmentShader()
    {
        return "Please Override Me!";
    }

    public void Draw()
    {
        System.out.println("Please Override Me!");
    }

    public void InitializeEffect(int height, int width)
    {
        System.out.println("Please Override Me!");
    }

    public void LoadBmp(String uri)
    {
        System.out.println("Please Override Me!" + uri);
    }
}
