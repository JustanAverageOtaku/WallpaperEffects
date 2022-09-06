package com.zero.zerolivewallpaper;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.transition.TransitionManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.zero.zerolivewallpaper.data.CatalogItem;
import com.zero.zerolivewallpaper.databinding.ActivitySetBinding;
import com.zero.zerolivewallpaper.utils.StorageHelper;
import com.zero.zerolivewallpaper.wallpaper.GLWallpaperPreview;
import com.zero.zerolivewallpaper.wallpaper.Utils;
import com.zero.zerolivewallpaper.wallpaper.WallpaperViewSetter;

import java.io.File;

import static com.zero.zerolivewallpaper.Constants.LIVE_SERVICE_NAME;
import static com.zero.zerolivewallpaper.Constants.PREF_BACKGROUND;
import static com.zero.zerolivewallpaper.wallpaper.Utils.openLWSetter;

public class SetActivity extends AppCompatActivity
{
    private static final String TAG = "ActivitySetter";
    // Constants
    public static final String EXTRA_CATALOG_ITEM = "item";

    private Context context;

    // Layout
    LinearLayout linearLayout;
    private TextView titleText;
    private TextView authorText;
    private ConstraintLayout textBox;
    //private GLWallpaperPreview wpPreview;

    private SharedPreferences sharedPreferences;
    private CatalogItem catalogItem;

    private boolean textBoxVisible = true;
    private ActivitySetBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySetBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

       // setContentView(R.layout.some_activity);
        //binding.setTextAuthor.sette
        context = this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // First execution, grab intent extra
        Intent intent = getIntent();
        if (intent != null) {
            catalogItem = intent.getParcelableExtra(EXTRA_CATALOG_ITEM);
        } else {
            finish();
            return;
        }


        //WallpaperViewSetter wps = new WallpaperViewSetter(SetActivity.this);
        //wps.SetView(binding.viewSet);
        //binding.wpggg.TestSetView();
        binding.wpg.GetWallpaperPreview("uri", 1);
        //try {
        //    wps = findViewById(R.id.viewSet);
        //} catch (Exception e) {
        //    Log.e(TAG, "OnFindByID: ", e);
        //}
        //try {
        //    //wps.SetView();
        //} catch (Exception e) {
        //    Log.e(TAG, "OnSetView: ", e);
        //}
        ////linearLayout = new LinearLayout(this);
        ////linearLayout.setOrientation(LinearLayout .VERTICAL);
//
        //VideoView vv = new VideoView(context);
        //MediaController mediaController = new MediaController(context);
        //mediaController.setAnchorView(vv);
        //String path = "android.resource://" + context.getPackageName() + "/" + R.raw.abstract_video;
        //vv.setMediaController(mediaController);
        //vv.setVideoURI(Uri.parse(path));
        //vv.requestFocus();
        //vv.start();
        //vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        //    @Override
        //    public void onCompletion(MediaPlayer mediaPlayer) {
        //        vv.start();
        //    }
        //});

        //linearLayout.addView(vv);

       // setContentView(linearLayout);
        //textBox = findViewById(R.id.set_text_box);


        //String uri = "------------------------------------I Am NULL!----------------------------";
//
        //Bundle extras = intent.getExtras();//getIntent().getExtras();
        //if (extras == null)
        //{
        //    System.out.println(uri);
        //}
        //else
        //{
        //    uri = extras.getString("uri");
        //    System.out.println(uri);
        //}

        //wpPreview = findViewById(R.id.set_wppreview);

        //WallpaperViewSetter wps = new WallpaperViewSetter(context);
        //wps.SetView();
        //VideoView videoView = (VideoView)findViewById(R.id.videoView1);
        //MediaController mediaController = new MediaController(this);
        //mediaController.setAnchorView(videoView);
        //String path = "android.resource://" + getPackageName() + "/" + R.raw.abstract_video;
        //videoView.setMediaController(mediaController);
        //videoView.setVideoURI(Uri.parse(path));
        //videoView.requestFocus();
        //videoView.start();
        //videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        //    @Override
        //    public void onCompletion(MediaPlayer mediaPlayer) {
        //        videoView.start();
        //    }
        //});

        // Show textbox
        //textBox.setVisibility(View.VISIBLE);

        // Set wallpaper preview
        //wpPreview.init(catalogItem.getId());

        // Set title text
        //titleText = findViewById(R.id.set_text_title);
        //titleText.setText(catalogItem.getTitle());

        // Set author text
        //authorText = findViewById(R.id.set_text_author);
        //authorText.setText(catalogItem.getAuthor());

        // Add magic button
        //wpPreview.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        //TransitionManager.beginDelayedTransition(textBox);
        //        //textBoxVisible = !textBoxVisible;
        //        //textBox.setVisibility(textBoxVisible ? View.VISIBLE : View.GONE);
        //    }
        //});
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.wpg.OnResume();
        //wpPreview.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.wpg.OnResume();
        //wpPreview.stop();
    }


    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.set_menu, menu);

        // Hide delete for custom wallpapers
        if (catalogItem.getSite() == null || catalogItem.getSite().equals("")) {
            MenuItem menuItem = menu.findItem(R.id.set_menu_link);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.set_menu_set:
                // Set wallpaper
                setWallpaper();
                //setToWallPaper(this);
                return true;

            case R.id.set_menu_settings:
                // Start settings
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.set_menu_delete:
                // Delete local content
                deleteWallpaper();
                return true;

            case R.id.set_menu_link:
                // Open browser to author's website
                Utils.openBrowser(context, catalogItem.getSite());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Start wallpaper setting routine + interface
    private void setWallpaper() {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();

        sharedPreferences.edit().putString(PREF_BACKGROUND, catalogItem.getId()).apply();

        boolean zeroAsLive = true;

        // Check if Zero is the current live wallpaper
        if (wallpaperInfo == null) {
            zeroAsLive = false;
        } else {
//            if (!wallpaperInfo.getServiceName().equals(SERVICE_NAME)) {
//                zeroAsLive = false;
//            }
            if (!wallpaperInfo.getServiceName().equals(LIVE_SERVICE_NAME)) {
                zeroAsLive = false;
            }
        }
        //setToWallPaper(this);
        if (!zeroAsLive) {
            // Show dialog
            new AlertDialog.Builder(this)
                    .setTitle(R.string.set_dialog_notzero_title)
                    .setMessage(R.string.set_dialog_notzero_message)
                    .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Show live-wallpaper preview
                            openLWSetter(context, "uri", 2);
                        }
                    })
                    .setNegativeButton(R.string.common_cancel, null)
                    .show();
        } else {
            Toast.makeText(context, R.string.set_alert_changed, Toast.LENGTH_SHORT).show();
        }
    }

    // Start wallpaper delete routine + interface
    private void deleteWallpaper() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.set_dialog_delete_title)
                .setMessage(R.string.set_dialog_delete_message)
                .setPositiveButton(R.string.common_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Show live-wallpaper preview
                        File file = StorageHelper.getBackgroundFolder(catalogItem.getId(), context);
                        StorageHelper.deleteFolder(file);
                        finish();
                    }
                })
                .setNegativeButton(R.string.common_cancel, null)
                .show();
    }
}
