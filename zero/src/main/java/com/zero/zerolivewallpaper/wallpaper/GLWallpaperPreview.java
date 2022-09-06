package com.zero.zerolivewallpaper.wallpaper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class GLWallpaperPreview extends GLSurfaceView {

    private MyRenderer renderer;
    private WallpaperRenderer wprenderer;
    private final Context context;

    public GLWallpaperPreview(Context context) {
        super(context);
        this.context = context;

    }

    public GLWallpaperPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void init(String uri, int effectId) {
//        String uri = "------------------------------------I Am NULL!----------------------------";
//
//        Bundle extras = ((Activity)context).getIntent().getExtras();//getIntent().getExtras();
//        if (extras == null)
//        {
//            System.out.println(uri);
//        }
//        else
//        {
//            uri = extras.getString("uri");
//            System.out.println(uri);
//        }

        // Set version
        setEGLContextClientVersion(2);
        // Set renderer
        //renderer = new MyRenderer(context, id);
        wprenderer = new WallpaperRenderer(context);

        //setRenderer(renderer);
        setRenderer(wprenderer);
    }

    public void start()
    {
        //renderer.start();
        wprenderer.start();
    }

    public void stop() {
        //renderer.stop();
        wprenderer.stop();
    }
}