package com.zero.zerolivewallpaper.wallpaper;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.zero.zerolivewallpaper.R;

import java.io.File;
import java.io.IOException;


@SuppressLint("Registered")
public class LiveWallpaperService extends WallpaperService
{
    public static String uri;
    public static int rawuri;

    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    class VideoEngine extends Engine {
        private final String TAG = getClass().getSimpleName();
        private MediaPlayer mediaPlayer;

        public VideoEngine()
        {
            mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.abstract_video);
            mediaPlayer.setLooping(true);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);


            //mediaPlayer.setSurface(holder.getSurface());
            //mediaPlayer.start();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setSurface(holder.getSurface());
            try {
                AssetFileDescriptor descriptor;
                descriptor = getAssets().openFd("Fantasy_00.mp4");
                mediaPlayer.reset();
                //mediaPlayer.setDataSource(getFilesDir() + "/file.mp4");
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                mediaPlayer.setLooping(true);
                mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                descriptor.close();
                mediaPlayer.prepare();
                mediaPlayer.start();
                //    File file = new File(getFilesDir() + "/unmute");
                //    if (file.exists()) mediaPlayer.setVolume(1.0f, 1.0f);
                //    else mediaPlayer.setVolume(0, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                mediaPlayer.start();
            } else {
                mediaPlayer.pause();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mediaPlayer != null) mediaPlayer.release();
            //unregisterReceiver(broadcastReceiver);
        }
    }
}
