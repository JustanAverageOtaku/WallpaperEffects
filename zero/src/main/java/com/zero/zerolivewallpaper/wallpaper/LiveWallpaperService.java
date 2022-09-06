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

import java.io.File;
import java.io.IOException;


@SuppressLint("Registered")
public class LiveWallpaperService extends WallpaperService
{
    public static final String VIDEO_PARAMS_CONTROL_ACTION = "com.zero.zerolivewallpaper";
    public static final String KEY_ACTION = "music";
    public static final boolean ACTION_MUSIC_UNMUTE = false;
    public static final boolean ACTION_MUSIC_MUTE = true;

    //public static void muteMusic(Context context) {
    //    Intent intent = new Intent(LiveWallpaperService.VIDEO_PARAMS_CONTROL_ACTION);
    //    intent.putExtra(LiveWallpaperService.KEY_ACTION, LiveWallpaperService.ACTION_MUSIC_MUTE);
    //    context.sendBroadcast(intent);
    //}
//
    //public static void unmuteMusic(Context context) {
    //    Intent intent = new Intent(LiveWallpaperService.VIDEO_PARAMS_CONTROL_ACTION);
    //    intent.putExtra(LiveWallpaperService.KEY_ACTION, LiveWallpaperService.ACTION_MUSIC_UNMUTE);
    //    context.sendBroadcast(intent);
    //}

    class VideoEngine extends Engine {
        private MediaPlayer mediaPlayer;
        private BroadcastReceiver broadcastReceiver;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            IntentFilter intentFilter = new IntentFilter(LiveWallpaperService.VIDEO_PARAMS_CONTROL_ACTION);
            registerReceiver(broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean action = intent.getBooleanExtra(KEY_ACTION, false);
                    if (action) {
                        mediaPlayer.setVolume(0, 0);
                    } else {
                        mediaPlayer.setVolume(1.0f, 1.0f);
                    }
                }
            }, intentFilter);
        }

        @SuppressLint("SdCardPath")
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setSurface(holder.getSurface());
            try {
                AssetFileDescriptor descriptor;
                descriptor = getAssets().openFd( "Abstract_00.mp3" );
                mediaPlayer.reset();
                //mediaPlayer.setDataSource(getFilesDir() + "/file.mp4");
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                mediaPlayer.setLooping(true);
                mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                descriptor.close();
                mediaPlayer.prepare();
                mediaPlayer.start();
                File file = new File(getFilesDir() + "/unmute");
                if (file.exists()) mediaPlayer.setVolume(1.0f, 1.0f);
                else mediaPlayer.setVolume(0, 0);
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
            unregisterReceiver(broadcastReceiver);
        }
    }

    public Engine onCreateEngine() {
        return new VideoEngine();
    }

}
