package com.iglassus.exoplayerfilter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.Player;
import com.iglassus.epf.EPlayerView;
import com.iglassus.epf.filter.VideoViewFilterParams;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.xw.repo.BubbleSeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends Activity{
    public final static int REQUEST_CODE = -1010101;
    public Intent glassService;

    public static EPlayerView ePlayerView;
    public static MovieWrapperView movieWrapperView;
    private SimpleExoPlayer player;
    private Button playPause, unLock;
    private SeekBar seekBar;
    private PlayerTimer playerTimer;

    // for file chooser
    DialogProperties properties = new DialogProperties();
    FilePickerDialog filePickerDialog;
    VideoViewFilterParams.FrameImgFormatEnum frameImgFormatEnum= VideoViewFilterParams.FrameImgFormatEnum.Format2D;

    // for videoFileReformatter
    String inputVideoFilePath;

    // padding of the frame image relative to the video player view window
    private float bsk_upperpadding_percentage = 0.07f;
    private float bsk_bottompadding_percentage = 0.20f;
    private float bsk_leftrightpadding_percentage = 0.0f;
    private float bsk_middlepadding_percentage = 0.0f;
    private boolean flip=true;
    private boolean distortion=false;
    private VideoViewFilterParams videoViewFilterParams;
    final static FilterType filterType = FilterType.IGLASS;
    private Bitmap bitmap;
    public DisplayManager mDisplayManager;
    public Display[] displays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UsbChangeNotification.appIsRunning=true;

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
        videoViewFilterParams = new VideoViewFilterParams(flip,distortion,frameImgFormatEnum,bsk_upperpadding_percentage,bsk_bottompadding_percentage,bsk_leftrightpadding_percentage,bsk_middlepadding_percentage,bitmap);
        movieWrapperView = (MovieWrapperView) findViewById(R.id.layout_movie_wrapper);

        // https://developer.android.com/training/system-ui/immersive.html
        // Hide the status bar on Android 4.1 (API level 16) and higher:
        // https://bradmartin.net/2016/03/10/fullscreen-and-navigation-bar-color-in-a-nativescript-android-app/
        unLock =findViewById(R.id.unLock);
        unLock.setVisibility(View.GONE);
        View decorView = getWindow().getDecorView();
        int uiOptions =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                 | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                 | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        // spinner to choose the frame size
        Spinner image2dor3dformat_spinner = (Spinner) findViewById(R.id.image2dor3dformat_spinner);
        List<String> image2dor3dformaList = new ArrayList<String>();
        //image2dor3dformaList.add("1D format");
        image2dor3dformaList.add("2D format");
        image2dor3dformaList.add("3D format");
        {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, image2dor3dformaList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            image2dor3dformat_spinner.setAdapter(dataAdapter);
        }
        image2dor3dformat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // On selecting a spinner item
                if (pos == 0) {
                    frameImgFormatEnum = VideoViewFilterParams.FrameImgFormatEnum.Format2D;
                } else {
                    frameImgFormatEnum = VideoViewFilterParams.FrameImgFormatEnum.Format3D;
                }
                videoViewFilterParams.setFrameImgFormat(frameImgFormatEnum);
                ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // file chooser
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        filePickerDialog = new FilePickerDialog(this,properties);
        filePickerDialog.setTitle("Select a Video File");
        filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                if (files == null || files.length < 1) {
                    return;
                }

                String selectedFilePath = files[0];
                inputVideoFilePath = selectedFilePath;

                // set the file chooser's current default dir
                String selectedDirPath = (new File(selectedFilePath)).getParent().toString();
                // properties.root = new File(selectedDirPath);
                properties.offset = new File(selectedDirPath);

                TextView textViewChosenFileName = (TextView) findViewById(R.id.choosenfilename_textview);
                textViewChosenFileName.setText("Chosen File: " + inputVideoFilePath);

                // Measures bandwidth during playback. Can be null if not required.
                DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
                // Produces DataSource instances through which media data is loaded.
                Context context = getApplicationContext();
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "yourApplicationName"), defaultBandwidthMeter);
                // Produces Extractor instances for parsing the media data.
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                // This is the MediaSource representing the media to be played.
                // My Option to use File Chooser: inputVideoFilePath
                // https://github.com/google/ExoPlayer/issues/3410: Uri localUri=Uri.fromFile(file);
                MediaSource videoSource = new ExtractorMediaSource(Uri.fromFile(new File(inputVideoFilePath)), dataSourceFactory, extractorsFactory, null, null);
                //videoSource = new ExtractorMediaSource(Uri.parse("rtsp://v8.cache1.c.youtube.com/CiILENy73wIaGQnxa4t5p6BVTxMYESARFEgGUgZ2aWRlb3MM/0/0/0/video.3gp"), dataSourceFactory, extractorsFactory, null, null);
                // Prepare the player with the source.
                player.prepare(videoSource);
                player.setPlayWhenReady(true);
                ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));
                playPause.setText(R.string.pause);
            }
        });

        // set the padding bubble seek bars listener
        final BubbleSeekBar bsk_upperpadding = findViewById(R.id.upperpadding_bsk);
        final BubbleSeekBar bsk_bottompadding = findViewById(R.id.bottompadding_bsk);
        final BubbleSeekBar bsk_leftrightpadding = findViewById(R.id.leftrightpadding_bsk);
        final BubbleSeekBar bsk_middlepadding = findViewById(R.id.middlepadding_bsk);
        bsk_upperpadding.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                bsk_upperpadding_percentage = progressFloat;
                videoViewFilterParams.setUpperPadding_percentage(progressFloat/100.0f);
                ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));
            }
        });
        bsk_bottompadding.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                bsk_bottompadding_percentage = progressFloat;
                videoViewFilterParams.setBottomPadding_percentage(progressFloat/100.0f);
                ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));
            }
        });
        bsk_leftrightpadding.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                bsk_leftrightpadding_percentage = progressFloat;
                videoViewFilterParams.setLeftHalfImgLeftPadding_percentage(progressFloat/100.0f);
                ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));
            }
        });
        bsk_middlepadding.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                bsk_middlepadding_percentage = progressFloat;
                // Note: halfImgRightPadding = middlePadding/2.0
                videoViewFilterParams.leftHalfImgRightPadding_percentage = progressFloat/100.0f;
                FilterType filterType = FilterType.IGLASS;
                ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));
            }
        });
        setUpViews();
        setUpSimpleExoPlayer();
        setUoGlPlayerView();
        setUpTimer();
        castMovieToGlass();
        Log.i("开始","activity开始了");
    }

    // END: protected void onCreate(Bundle savedInstanceState)

    public void chooseVideoFileToProcess(@SuppressWarnings("unused") View unused) {
        filePickerDialog.show();
    }
    @Override
    protected void onNewIntent(Intent intent)
    {
        mDisplayManager= (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
        displays=mDisplayManager.getDisplays();
        if(displays.length<=1) {
            Toast.makeText(this,"眼镜拔出",Toast.LENGTH_LONG).show();
            finishAndRemoveTask ();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        UsbChangeNotification.appIsRunning=false;
        releasePlayer();
        if (playerTimer != null) {
            playerTimer.stop();
            playerTimer.removeMessages(0);
        }
        if(glassService !=null) stopService(glassService);
        Log.i("破坏","activity被破坏了");
    }

    private void setUpViews() {
        // control visibility
        // temporary use this way. Should have a more elegant way to do it, triggered by let be idle for a while
        final Button btn_controlvisibility = (Button) findViewById(R.id.btn_controlvisibility);
        btn_controlvisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScrollView scrollview_controller = (ScrollView) findViewById(R.id.scrollview_controller);
                scrollview_controller.setVisibility(View.GONE);
            }
        });

        // play pause
        playPause = (Button) findViewById(R.id.btn_pause);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (!isPlaying) return;

                if (playPause.getText().toString().equals(MainActivity.this.getString(R.string.pause))) {
                    player.setPlayWhenReady(false);
                    playPause.setText(R.string.play);
                } else {
                    player.setPlayWhenReady(true);
                    playPause.setText(R.string.pause);
                }
            }
        });

        // seek
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player == null) return;

                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                player.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

        });
    }

    private void setUpSimpleExoPlayer() {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // SimpleExoPlayer
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
    }

    private void setUoGlPlayerView() {
        // In xml, MovieWrapperView wrap_content height, let the LinearLayout below not visible
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        float density = this.getResources()
                .getDisplayMetrics()
                .density;
        int topBarHeight = Math.round(density * 36);

        ePlayerView = new EPlayerView(this);
        ePlayerView.setSimpleExoPlayer(player);
        ePlayerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ePlayerView.onResume();
        //movieWrapperView.addView(ePlayerView);

        // ScrollView is accessed from the inner class. need to be delcared final
        // can move scrollview_controller inside the onClick
        movieWrapperView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //scrollview_controller.setVisibility(View.VISIBLE);
                if(unLock.getVisibility()==View.VISIBLE){
                    unLock.setVisibility(View.GONE);
                    return;
                }
                unLock.setVisibility(View.VISIBLE);
                unLock.bringToFront();
            }
        });
    }

    private void setUpTimer() {
        playerTimer = new PlayerTimer();
        playerTimer.setCallback(new PlayerTimer.Callback() {
            @Override
            public void onTick(long timeMillis) {
                long position = player.getCurrentPosition();
                long duration = player.getDuration();
                if (duration <= 0) return;

                seekBar.setMax((int) duration / 1000);
                seekBar.setProgress((int) position / 1000);
            }
        });
        playerTimer.start();
    }

    private void releasePlayer() {
        ePlayerView.onPause();
        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).removeAllViews();
        ePlayerView = null;
        player.stop();
        player.release();
        player = null;
    }

    // https://android.googlesource.com/platform/development/+/e7a6ab4/samples/devbytes/ui/ImmersiveMode/src/main/java/com/example/android/immersive/ImmersiveActivity.java
    // https://stackoverflow.com/questions/24187728/sticky-immersive-mode-disabled-after-soft-keyboard-shown
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // When the window loses focus (e.g. the action overflow is shown),
        // cancel any pending hide action. When the window gains focus,
        // hide the system UI.
        if (hasFocus) {
            delayedHide(300);
        } else {
            mHideHandler.removeMessages(0);
        }
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private final Handler mHideHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            hideSystemUI();
        }
    };
    private void delayedHide(int delayMillis) {
        mHideHandler.removeMessages(0);
        mHideHandler.sendEmptyMessageDelayed(0, delayMillis);
    }

    public void distortFrame(View view) {
        distortion=distortion?false:true;
        videoViewFilterParams.setDistortion(distortion);
        ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));
    }

    public void flipFrame(View view) {
        flip=flip?false:true;
        videoViewFilterParams.setFlip(flip);
        ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));
    }

    public void openYoutube(View view) {

        //Intent intent=new Intent(this,YoutubeActivity.class);
        //startActivity(intent);
    }

    public void unLock(View view) {
        final ScrollView scrollview_controller = (ScrollView) findViewById(R.id.scrollview_controller);
        scrollview_controller.setVisibility(View.VISIBLE);
        unLock.setVisibility(View.GONE);
    }

    //@RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NewApi")
    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (!Settings.canDrawOverlays(this)) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            /** request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE);
        }else{
            startGlass();
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
       /* * if so check once again if we have permission */
            if (Settings.canDrawOverlays(this)) {
                // continue here - permission was granted
                startGlass();
            }else{
                Toast.makeText(this,"没有权限！",Toast.LENGTH_LONG);
                finishAndRemoveTask();
            }
        }
    }
    private void startGlass() {
        glassService = new Intent(this, IGlassService.class);
        startService(glassService);
    }

    private void castMovieToGlass(){
        //ViewGroup viewGroup= (ViewGroup) ePlayerView.getParent();
        //if(viewGroup!=null) viewGroup.removeAllViews();
        //ePlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mDisplayManager= (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
        displays=mDisplayManager.getDisplays();
        if(displays.length<=1) {
            Toast.makeText(this,"未检测到眼镜",Toast.LENGTH_LONG).show();
            Log.i("未检测到","未检测到眼镜");
            finishAndRemoveTask ();
        } else {
            Toast.makeText(MainActivity.this, "检测到眼镜", Toast.LENGTH_SHORT).show();
            Log.i("检测到","检测到眼镜");
            checkDrawOverlayPermission();
        }
    }

    @SuppressLint("NewApi")
    public void testDisplays(View view) {
        // The file '/sys/devices/virtual/switch/hdmi/state' holds an int -- if it's 1 then an HDMI device is connected.
        // An alternative file to check is '/sys/class/switch/hdmi/state' which exists instead on certain devices.
        /*
        File switchFile = new File("/sys/devices/virtual/switch/hdmi/state");
        if (!switchFile.exists()) {
            switchFile = new File("/sys/class/switch/hdmi/state");
        }
        try {
            Scanner switchFileScanner = new Scanner(switchFile);
            int switchValue = switchFileScanner.nextInt();
            switchFileScanner.close();
            Toast.makeText(MainActivity.this, (switchValue>0?String.valueOf(2):String.valueOf(1))+"个屏幕", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }*/
        mDisplayManager= (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
        displays=mDisplayManager.getDisplays();
        DisplayMetrics metrics =new DisplayMetrics();
        displays[1].getRealMetrics(metrics);
        float rate=displays[1].getRefreshRate();
        Toast.makeText(getApplicationContext(),String.valueOf(metrics)+"\n"+"Frequency{"+String.valueOf(rate)+"}",Toast.LENGTH_LONG).show();
    }

}