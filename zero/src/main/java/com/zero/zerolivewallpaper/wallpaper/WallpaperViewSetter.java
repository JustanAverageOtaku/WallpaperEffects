package com.zero.zerolivewallpaper.wallpaper;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.zero.zerolivewallpaper.R;

public class WallpaperViewSetter extends FrameLayout
{
    private static final String TAG = "WallpaperViewSetter";
    private int idEffect = 0;
    private GLWallpaperPreview glPreview;
    private VideoView liveWallpaperPreview;
    Context context;

    public WallpaperViewSetter(Context context)
    {
        super(context);
        this.context = context;
    }
    public WallpaperViewSetter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void SetView(FrameLayout frameLayout)
    {
        //LinearLayout LL = new LinearLayout(context);
        //LL.setOrientation(LinearLayout.VERTICAL);
        //LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        VideoView vv = new VideoView(context);
        //vv.setLayoutParams(LLParams);
        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(vv);
        String path = "android.resource://" + context.getPackageName() + "/" + R.raw.abstract_video;
        vv.setMediaController(mediaController);
        vv.setVideoURI(Uri.parse(path));
        vv.requestFocus();
        vv.start();
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                vv.start();
            }
        });

        //LL.addView(vv);
        try {
            frameLayout.addView(vv);
            //addView(vv);
        } catch (Exception e) {
            Log.e(TAG, "OnAddView", e);
        }
    }

    public void TestSetView()
    {
        VideoView vv = new VideoView(context);
        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(vv);
        String path = "android.resource://" + context.getPackageName() + "/" + R.raw.abstract_video;
        vv.setMediaController(mediaController);
        vv.setVideoURI(Uri.parse(path));
        vv.requestFocus();
        vv.start();
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                vv.start();
            }
        });

        addView(vv);
    }

    public void GetWallpaperPreview(String uri, int effectId)
    {
        idEffect = effectId;

        if (effectId == 2)
        {
            liveWallpaperPreview = new VideoView(context);
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(liveWallpaperPreview);
            String path = "android.resource://" + context.getPackageName() + "/" + R.raw.abstract_video;
            liveWallpaperPreview.setMediaController(mediaController);
            liveWallpaperPreview.setVideoURI(Uri.parse(path));
            liveWallpaperPreview.requestFocus();
            liveWallpaperPreview.start();
            liveWallpaperPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    liveWallpaperPreview.start();
                }
            });

            addView(liveWallpaperPreview);
        }
        else
        {
            glPreview = new GLWallpaperPreview(context);

            glPreview.init(uri, effectId);
            addView(glPreview);
        }
    }

    public void OnResume()
    {
        if (glPreview != null)
        {
            glPreview.start();
        }
        else
        {
            liveWallpaperPreview.resume();
        }
    }

    public void OnPause()
    {
        if (glPreview != null)
        {
            glPreview.stop();
        }
        else
        {
            liveWallpaperPreview.pause();
        }
    }
}
