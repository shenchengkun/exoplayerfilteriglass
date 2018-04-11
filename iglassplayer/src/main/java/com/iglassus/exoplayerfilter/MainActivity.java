package com.iglassus.exoplayerfilter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.Player;
import com.google.android.gms.plus.PlusShare;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
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
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.iglassus.exoplayerfilter.youtubeData.DeveloperKey;
import com.iglassus.exoplayerfilter.youtubeData.ListAdapter;
import com.iglassus.exoplayerfilter.youtubeData.data;
import com.xw.repo.BubbleSeekBar;

import org.apache.commons.lang3.CharEncoding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

public class MainActivity extends Activity{
    public final static int REQUEST_CODE = -1010101;
    public static int itag=22;
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
    private float bsk_upperpadding_percentage = 0.0f;
    private float bsk_bottompadding_percentage = 0.0f;
    private float bsk_leftrightpadding_percentage = 0.0f;
    private float bsk_middlepadding_percentage = 0.0f;
    private boolean flip=true;
    private boolean distortion=false;
    private VideoViewFilterParams videoViewFilterParams;
    final static FilterType filterType = FilterType.IGLASS;
    private Bitmap bitmap;
    public DisplayManager mDisplayManager;
    public Display[] displays;
    private String testURL="";
    private View youtubeSearchView;
    private Button youtubeGone;
    private ScrollView scrollview_controller;
    private OkHttpClient client;
    private ArrayList<String> pages=new ArrayList<>();
    static String MyAcessTokenData = "access_token=";
    private SharedPreferences pref;
    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;
    private DefaultBandwidthMeter defaultBandwidthMeter;
    private DataSource.Factory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private String[] SUGGESTION = new String[]{"Belgium", "France", "Italy", "Germany", "Spain"};
    private AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UsbChangeNotification.appIsRunning=true;

        //palyYoutubeWithID("rUOgEfE9Cf4",false);


        defaultBandwidthMeter = new DefaultBandwidthMeter();
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"), defaultBandwidthMeter);
        extractorsFactory = new DefaultExtractorsFactory();
        this.mRecyclerView = (RecyclerView) findViewById(R.id.mlist);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pref= this.pref = PreferenceManager.getDefaultSharedPreferences(this);
        client=new OkHttpClient();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
        videoViewFilterParams = new VideoViewFilterParams(flip,distortion,frameImgFormatEnum,bsk_upperpadding_percentage,bsk_bottompadding_percentage,bsk_leftrightpadding_percentage,bsk_middlepadding_percentage,bitmap);
        movieWrapperView = (MovieWrapperView) findViewById(R.id.layout_movie_wrapper);

        // https://developer.android.com/training/system-ui/immersive.html
        // Hide the status bar on Android 4.1 (API level 16) and higher:
        // https://bradmartin.net/2016/03/10/fullscreen-and-navigation-bar-color-in-a-nativescript-android-app/

        autoCompleteTextView=findViewById(R.id.playtxt);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.dropdown, this.SUGGESTION);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.setEnabled(true);
                autoCompleteTextView.setCursorVisible(true);
                autoCompleteTextView.setBackgroundResource(R.drawable.picb46);
            }
        });
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_SEARCH) {
                    return false;
                }
                newSearch();
                autoCompleteTextView.dismissDropDown();
                autoCompleteTextView.setCursorVisible(false);
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                hideSystemUI();
                return true;
            }
        });
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != 1 || event.getRawX() < ((float) (MainActivity.this.autoCompleteTextView.getRight() - MainActivity.this.autoCompleteTextView.getCompoundDrawables()[2].getBounds().width()))) {
                    return false;
                }
                newSearch();
                return true;
            }
        });
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newSearch();
            }
        });

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
                player.prepare(new ExtractorMediaSource(Uri.fromFile(new File(inputVideoFilePath)), dataSourceFactory, extractorsFactory, null, null));
                player.setPlayWhenReady(true);
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

        scrollview_controller = (ScrollView) findViewById(R.id.scrollview_controller);
        youtubeSearchView=findViewById(R.id.youtubeSearchView);
        youtubeGone=findViewById(R.id.youtubeGone);
        youtubeGone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youtubeSearchView.setVisibility(View.GONE);
                scrollview_controller.setVisibility(View.VISIBLE);
                movieWrapperView.setVisibility(View.VISIBLE);
            }
        });

        resetPages();
        newSearch();
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

        ePlayerView = new EPlayerView(this.getApplicationContext());
        ePlayerView.setSimpleExoPlayer(player);
        ePlayerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ePlayerView.onResume();
        ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));
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
        youtubeSearchView.setVisibility(View.VISIBLE);
        youtubeSearchView.bringToFront();
        scrollview_controller.setVisibility(View.GONE);
        movieWrapperView.setVisibility(View.GONE);
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


    private class RunTask extends AsyncTask<String,String,List<data>>{
        List<data> myData;
        boolean prescroll;
        String testJson="";

        private RunTask() {
            this.myData = new ArrayList();
            this.prescroll = false;
        }

        @Override
        protected List<data> doInBackground(String... strings) {
            int i;
            if (true||strings[1].equals("0")) {
                MainActivity.this.resetPages();
            }
            String getData = "";
            String idDuration = "";
            try {
                getData = MainActivity.this.getJson(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            testJson=getData;
            try {
                JSONObject jSONObject = new JSONObject(getData);
                if (jSONObject.isNull("nextPageToken")) {
                    //SearchActivity.this.scroll = false;
                } else {
                    MainActivity.this.pages.add(jSONObject.getString("nextPageToken"));
                    this.prescroll = true;
                }
                JSONArray mainArray = jSONObject.getJSONArray("items");
                int maxResult = Math.min(jSONObject.getJSONObject("pageInfo").getInt("totalResults"), mainArray.length());
                for (i = 0; i < maxResult; i++) {
                    JSONObject mainObject0 = mainArray.getJSONObject(i);
                    JSONObject sousObject1 = mainObject0.getJSONObject("snippet");
                    String title = sousObject1.getString("title");
                    String channeltitle = sousObject1.getString("channelTitle");
                    String imageUrl = sousObject1.getJSONObject("thumbnails").getJSONObject("medium").getString(PlusShare.KEY_CALL_TO_ACTION_URL);
                    String dates = sousObject1.getString("publishedAt");
                    JSONObject idOBject1 = mainObject0.getJSONObject("id");
                    String kind = idOBject1.getString("kind");
                    if (kind.equals("youtube#video")) {
                        String id = idOBject1.getString("videoId");
                        this.myData.add(new data(imageUrl, title, channeltitle, dates, 0, id));
                        idDuration = idDuration + id + ",";
                    } else {
                        if (kind.equals("youtube#channel")) {
                            this.myData.add(new data(imageUrl, title, channeltitle, dates, 1, idOBject1.getString("channelId")));
                        } else {
                            if (kind.equals("youtube#playlist")) {
                                this.myData.add(new data(imageUrl, title, channeltitle, dates, 2, idOBject1.getString("playlistId")));
                            }
                        }
                    }
                }
            } catch (JSONException e3) {
            } catch (NullPointerException e4) {
            }
            try {
                JSONArray items = new JSONObject(MainActivity.this.getJson("https://www.googleapis.com/youtube/v3/videos?" + MainActivity.MyAcessTokenData + MainActivity.this.pref.getString(DeveloperKey.AcessToken, "none") + "&" + "part=contentDetails,statistics" + "&" + "id=" + idDuration + "&" + "key=AIzaSyA6Sp0Jo0PdZmY0VYXwDSGsTk16yHcjEYA")).getJSONArray("items");
                int kk = 0;
                for (i = 0; i < this.myData.size(); i++) {
                    if (((data) this.myData.get(i)).getType() == 0) {
                        JSONObject child = items.getJSONObject(kk);
                        ((data) this.myData.get(i)).setDuration(child.getJSONObject("contentDetails").getString("duration"));
                        ((data) this.myData.get(i)).setCount(Long.parseLong(child.getJSONObject("statistics").getString("viewCount")));
                        kk++;
                    }
                }
            } catch (IOException e5) {
                e5.printStackTrace();
            } catch (NullPointerException e6) {
            } catch (JSONException e7) {
                e7.printStackTrace();
            } catch (NumberFormatException e8) {
            }
            return myData;
        }

        @Override
        protected void onPostExecute(List<data> data) {
            //Toast.makeText(getApplicationContext(),testJson,Toast.LENGTH_LONG).show();
            Log.i("json",testJson);
            mAdapter = new ListAdapter(data);
            mAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    Toast.makeText(getApplicationContext(),"播放第"+(position+1)+"个视频",Toast.LENGTH_SHORT).show();
                    palyYoutubeWithID(MainActivity.this.mAdapter.myData.get(position).getId(),false);
                }
                @Override
                public void onLongClick(int position) {
                    //Toast.makeText(getApplicationContext(),"您长按点击了"+position+"行",Toast.LENGTH_SHORT).show();
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void newSearch(){
        new RunTask().execute(url(0),"0");
    }

    private void palyYoutubeWithID(String id, boolean is360) {
        if (!false) {
            String youtubeLink = "http://youtube.com/watch?v=" + id;
            final String str = id;
            final boolean z = is360;
            new YouTubeUriExtractor(this) {
                public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                    String downloadUrl = "";
                    if (ytFiles != null) {
                        ArrayList<Integer> key = new ArrayList();
                        for (int i = 0; i < ytFiles.size(); i++) {
                            key.add(Integer.valueOf(ytFiles.keyAt(i)));
                            System.out.println(key.get(i));
                        }
                        if (key.contains(Integer.valueOf(MainActivity.itag))) {
                            downloadUrl = ((YtFile) ytFiles.get(MainActivity.itag)).getUrl();
                        } else if (key.contains(Integer.valueOf(18))) {
                            downloadUrl = ((YtFile) ytFiles.get(18)).getUrl();
                        } else if (key.contains(Integer.valueOf(22))) {
                            downloadUrl = ((YtFile) ytFiles.get(22)).getUrl();
                        } else if (key.contains(Integer.valueOf(36))) {
                            downloadUrl = ((YtFile) ytFiles.get(36)).getUrl();
                        } else if (key.contains(Integer.valueOf(17))) {
                            downloadUrl = ((YtFile) ytFiles.get(17)).getUrl();
                        } else {
                        }
                        System.out.println("Url------------>>> :" + downloadUrl);
                        if (downloadUrl == null || downloadUrl.isEmpty()) {
                            System.out.println("Download urL is null");
                            return;
                        } else if (z) {
                            return;
                        } else {
                            player.prepare(new ExtractorMediaSource(Uri.parse(downloadUrl), dataSourceFactory, extractorsFactory, null, null));
                            player.setPlayWhenReady(true);
                            playPause.setText(R.string.pause);
                            return;
                        }
                    }
                    System.out.println("YTFILES is null");
                }
            }.execute(new String[]{youtubeLink});
        }
    }

    private String getJson(String url) throws IOException {

        Response response = this.client.newCall(new Request.Builder().url(url).build()).execute();
        if (response.code() == 401) {
            getJson(url(0));
        }
        return response.body().string();
    }

    private String url(int page) {
        String url = "";
        int pg = page;
        try {
            if (this.pages.size() >= page) {
                pg = this.pages.size() - 1;
            }
            url = "https://www.googleapis.com/youtube/v3/search?" + MyAcessTokenData + this.pref.getString(DeveloperKey.AcessToken, "none") + "&" + "part=snippet"
                    //+ "&" + "pageToken=" + ((String) this.pages.get(pg)) + "&" + "maxResults=30" + "&" + "q=" + URLEncoder.encode(this.f33q.getText().toString(), CharEncoding.UTF_8) + "&" + "key=AIzaSyA6Sp0Jo0PdZmY0VYXwDSGsTk16yHcjEYA";
                    + "&" + "pageToken=" + this.pages.get(pg) + "&" + "maxResults=30" + "&" + "q=" + URLEncoder.encode(autoCompleteTextView.getText().toString(), CharEncoding.UTF_8) + "&" + "key=AIzaSyA6Sp0Jo0PdZmY0VYXwDSGsTk16yHcjEYA";
            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return url;
        }
    }

    public void resetPages() {
        //this.myDataAll = new ArrayList();
        //this.scroll = false;
        this.pages = new ArrayList();
        this.pages.add("");
    }
}