package com.zero.zerolivewallpaper.wallpaper;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.SurfaceHolder;

import java.util.concurrent.Executor;

public class MyWallpaperService extends GLWallpaperService
{
    public static String uri;
    public static int effectId;

    @Override
    public Engine onCreateEngine()
    {
        return new OpenGLES2Engine();
    }

    //@Override
    //public boolean bindService(Intent service, int flags, Executor executor, ServiceConnection conn) {
    //    super.bindService(service, flags, executor, conn);
//
    //    Bundle extras = service.getExtras();
    //    if (extras == null)
    //    {
    //        System.out.println("~~~~~~~~~~~~~~~~~Empty Extras");
    //    }
    //    else
    //    {
    //        String uri = extras.getString("uri");
    //        System.out.println(uri);
    //    }
//
    //    return true;
    //}

    private class OpenGLES2Engine extends GLWallpaperService.GLEngine {

        MyRenderer renderer;
        WallpaperRenderer wpRenderer;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            System.out.println("@@@@@@@@@@@@@@@@@@@@ Renderer Set and initiated");
            System.out.println("@@@@@@@@@@@@@@@@@@@@ " + uri);
            System.out.println("@@@@@@@@@@@@@@@@@@@@ " + effectId);
            // Set version
            setEGLContextClientVersion(2);
            setPreserveEGLContextOnPause(true);

            // Set renderer
            //renderer = new MyRenderer(getApplicationContext());
            wpRenderer = new WallpaperRenderer(getApplicationContext());
            //setRenderer(renderer);
            setRenderer(wpRenderer);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                //wpRenderer.start();
            } else {
                //wpRenderer.stop();
            }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            //wpRenderer.setOffset(xOffset);
        }
    }
}