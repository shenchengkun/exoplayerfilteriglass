package com.iglassus.exoplayerfilter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.plus.PlusShare;
import com.iglassus.epf.EPlayerView;
import com.iglassus.epf.filter.VideoViewFilterParams;
import com.iglassus.exoplayerfilter.youtubeData.DeveloperKey;
import com.iglassus.exoplayerfilter.youtubeData.EndlessRecyclerViewScrollListener;
import com.iglassus.exoplayerfilter.youtubeData.ListAdapter;
import com.iglassus.exoplayerfilter.youtubeData.data;

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

public class IGLassMainActivity extends Activity{
    private float bsk_upperpadding_percentage = 0.0f;
    private float bsk_bottompadding_percentage = 0.0f;
    private float bsk_leftrightpadding_percentage = 0.0f;
    private float bsk_middlepadding_percentage = 0.0f;
    private boolean flip=true;
    private boolean distortion=false;
    final static FilterType filterType = FilterType.IGLASS;
    private VideoViewFilterParams.FrameImgFormatEnum frameImgFormatEnum= VideoViewFilterParams.FrameImgFormatEnum.Format2D;
    private VideoViewFilterParams videoViewFilterParams=new VideoViewFilterParams(flip,distortion,frameImgFormatEnum,bsk_upperpadding_percentage,
            bsk_bottompadding_percentage,bsk_leftrightpadding_percentage,bsk_middlepadding_percentage,null);

    private GridView gridView;
    private SimpleExoPlayer player;
    public static EPlayerView ePlayerView;

    private DefaultDataSourceFactory dataSourceFactory;
    private DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
    private DialogProperties properties = new DialogProperties();
    private FilePickerDialog filePickerDialog;

    private ImageView playPause,stretch,mode,lock;
    private boolean isPlaying=false,is169=false;
    private TextView movieDuration;
    private SeekBar seekBar;
    private PlayerTimer playerTimer;

    private ViewGroup touchEventView;
    private Button unlock;
    private GestureDetector gestureDetector;
    private boolean firstScroll = false;// 每次触摸屏幕后，第一次scroll的标志
    private int GESTURE_FLAG = 0;// 1,调节进度，2，调节音量,3.调节亮度
    private static final int GESTURE_MODIFY_PROGRESS = 1;
    private static final int GESTURE_MODIFY_VOLUME = 2;
    private static final int GESTURE_MODIFY_BRIGHT = 3;
    private int playerWidth;
    private int playerHeight;
    private AudioManager audiomanager;
    private int maxVolume;
    private int currentVolume;
    private float mBrightness = -1f; // 亮度
    private static final float STEP_PROGRESS = 2f;// 设定进度滑动时的步长，避免每次滑动都改变，导致改变过快
    private static final float STEP_VOLUME = 2f;// 协调音量滑动时的步长，避免每次滑动都改变，导致改变过快
    private int playingTime=0;

    public Intent glassService;
    public final static int REQUEST_CODE = -1010101;
    private boolean noHDMI=false;
    public static Activity app=null;

    private ArrayList<String> pages;
    private String[] SUGGESTION = new String[]{"Belgium", "France", "Italy", "Germany", "Spain"};
    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean scroll;
    private ArrayList myDataAll;
    private String curString="victoria's secret";
    private OkHttpClient client;
    static String MyAcessTokenData = "access_token=";
    public static int itag=22;
    private SharedPreferences pref;
    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iglass_layout_main);
        if(noHDMI()) {
            finishAndRemoveTask();
            noHDMI=true;
            return;
        }

        setUpSimpleExoPlayer();
        setUoGlPlayerView();
        setUpControlPanel();
        setUpLockScreen();
        setUpHomeItermViews();
        castMovieToGlass();
        setUpYoutubeView();
        newSearch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app = null;
        if (noHDMI) return;
        releasePlayer();
        if (glassService != null) stopService(glassService);

    }

    @Override
    public void onBackPressed() {

    }

    private boolean noHDMI() {
        DisplayManager mDisplayManager = (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        if (displays.length <= 1) {
            Toast.makeText(this, "No Glass Found", Toast.LENGTH_SHORT).show();
            Log.i("未检测到", "未检测到眼镜");
            return true;
        } else {
            Toast.makeText(this, "Glass Found", Toast.LENGTH_SHORT).show();
            Log.i("检测到", "检测到眼镜");
            return false;
        }
    }

    private void releasePlayer() {
        ePlayerView.onPause();
        ePlayerView = null;
        player.stop();
        player.release();
        player = null;
        playerTimer.stop();
    }

    private void setUpYoutubeView() {
        mRecyclerView = findViewById(R.id.mlist);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        client=new OkHttpClient();

        autoCompleteTextView=findViewById(R.id.playtxt);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.dropdown, this.SUGGESTION);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.setEnabled(true);
                autoCompleteTextView.setCursorVisible(true);
                //autoCompleteTextView.setBackgroundResource(R.drawable.picb46);
            }
        });
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_SEARCH) {
                    return false;
                }
                curString=autoCompleteTextView.getText().toString();
                newSearch();
                autoCompleteTextView.dismissDropDown();
                autoCompleteTextView.setCursorVisible(false);
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
        });
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != 1 || event.getRawX() < ((float) (IGLassMainActivity.this.autoCompleteTextView.getRight() -
                        IGLassMainActivity.this.autoCompleteTextView.getCompoundDrawables()[2].getBounds().width()))) {
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
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.search72);
        drawable.setBounds(0, 0, 100,100);
        autoCompleteTextView.setCompoundDrawables(null, null, drawable, null);
        autoCompleteTextView.setPadding(10,0,10,0);

        findViewById(R.id.back_to_control).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(R.id.home_view).setVisibility(View.VISIBLE);
                findViewById(R.id.youtube_search_view).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.youtube_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode.performClick();
            }
        });

        resetPages();
    }


    private void setUpControlPanel() {
        playPause=findViewById(R.id.play_pause);
        stretch=findViewById(R.id.stretch);
        mode=findViewById(R.id.mode);
        seekBar = findViewById(R.id.seek_bar);
        movieDuration=findViewById(R.id.movie_duration);

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlaying = isPlaying ? false : true;
                playPause.setBackgroundColor(isPlaying? Color.YELLOW:Color.TRANSPARENT);
                player.setPlayWhenReady(isPlaying);
            }
        });
        stretch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is169){
                    bsk_upperpadding_percentage = 0.0f;
                    bsk_bottompadding_percentage = 0.0f;
                    is169=false;

                }else{
                    bsk_upperpadding_percentage = 0.09f;
                    bsk_bottompadding_percentage = 0.09f;
                    is169=true;
                }

                stretch.setBackgroundColor(is169? Color.TRANSPARENT:Color.YELLOW);
                videoViewFilterParams.setUpperPadding_percentage(bsk_upperpadding_percentage);
                videoViewFilterParams.setBottomPadding_percentage(bsk_bottompadding_percentage);
                ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));
            }
        });
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameImgFormatEnum= (frameImgFormatEnum==VideoViewFilterParams.FrameImgFormatEnum.Format2D)?
                        VideoViewFilterParams.FrameImgFormatEnum.Format3D:VideoViewFilterParams.FrameImgFormatEnum.Format2D;
                mode.setBackgroundColor(frameImgFormatEnum== VideoViewFilterParams.FrameImgFormatEnum.Format3D?Color.TRANSPARENT:Color.YELLOW);
                videoViewFilterParams.setFrameImgFormat(frameImgFormatEnum);
                ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));
            }
        });
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

        playerTimer = new PlayerTimer();
        playerTimer.setCallback(new PlayerTimer.Callback() {
            @Override
            public void onTick(long timeMillis) {
                long position = player.getCurrentPosition();
                long duration = player.getDuration();
                int positionInt=(int)position/1000;
                int durationInt=(int)duration/1000;
                if (duration <= 0) return;

                movieDuration.setText(positionInt/60+":"+positionInt%60+"("+durationInt/60+":"+durationInt%60+")");
                seekBar.setMax(durationInt);
                seekBar.setProgress(positionInt);
            }
        });
        playerTimer.start();
    }


    private void setUpLockScreen() {
        lock=findViewById(R.id.lock);
        touchEventView =findViewById(R.id.touch_event_view);
        unlock=findViewById(R.id.unlock);

        touchEventView.setLongClickable(true);
        audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
        currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
        /** 获取视频播放窗口的尺寸 */
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.home_view).setVisibility(View.GONE);
                unlock.setVisibility(View.GONE);
                touchEventView.setVisibility(View.VISIBLE);
            }
        });
        ViewTreeObserver viewObserver = touchEventView.getViewTreeObserver();
        viewObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                touchEventView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                playerWidth = touchEventView.getWidth();
                playerHeight = touchEventView.getHeight();
            }
        });
        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.home_view).setVisibility(View.VISIBLE);
                touchEventView.setVisibility(View.GONE);
            }
        });
        gestureDetector=new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                firstScroll = true;// 设定是触摸屏幕后第一次scroll的标志
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                playPause.performClick();
                if(unlock.getVisibility()==View.GONE) {
                    unlock.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            unlock.setVisibility(View.GONE);
                        }
                    }, 3000);
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float mOldX = e1.getX(), mOldY = e1.getY();
                int y = (int) e2.getRawY();
                if (firstScroll) {// 以触摸屏幕后第一次滑动为标准，避免在屏幕上操作切换混乱
                    // 横向的距离变化大则调整进度，纵向的变化大则调整音量
                    if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                        GESTURE_FLAG = GESTURE_MODIFY_PROGRESS;
                    } else {
                        if (mOldX > playerWidth * 3.0 / 5) {// 音量
                            GESTURE_FLAG = GESTURE_MODIFY_VOLUME;
                        } else if (mOldX < playerWidth * 2.0 / 5) {// 亮度
                            GESTURE_FLAG = GESTURE_MODIFY_BRIGHT;
                        }
                    }
                }
                // 如果每次触摸屏幕后第一次scroll是调节进度，那之后的scroll事件都处理音量进度，直到离开屏幕执行下一次操作
                if (GESTURE_FLAG == GESTURE_MODIFY_PROGRESS) {
                    // distanceX=lastScrollPositionX-currentScrollPositionX，因此为正时是快进
                    if (Math.abs(distanceX) > 2.0f*Math.abs(distanceY)) {// 横向移动大于纵向移动
                        if (distanceX >= DensityUtil.dip2px(getApplicationContext(), STEP_PROGRESS)) {// 快退，用步长控制改变速度，可微调
                            //if (playingTime > 3) {// 避免为负
                            //    playingTime -= 3;// scroll方法执行一次快退3秒
                            //} else {
                            //    playingTime = 0;
                            //}
                            playingTime=-5000;
                        } else if (distanceX <= -DensityUtil.dip2px(getApplicationContext(), STEP_PROGRESS)) {// 快进
                            //if (playingTime < videoTotalTime - 16) {// 避免超过总时长
                            //    playingTime += 3;// scroll执行一次快进3秒
                            //} else {
                            //    playingTime = videoTotalTime - 10;
                            //}
                            playingTime=5000;
                        }
                    }
                }
                // 如果每次触摸屏幕后第一次scroll是调节音量，那之后的scroll事件都处理音量调节，直到离开屏幕执行下一次操作
                else if (GESTURE_FLAG == GESTURE_MODIFY_VOLUME) {
                    currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
                    if (Math.abs(distanceY) > Math.abs(distanceX)) {// 纵向移动大于横向移动
                        //if (distanceY >0) {// 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
                        if (distanceY >= 5.0*DensityUtil.dip2px(getApplicationContext(), STEP_VOLUME)) {// 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
                            if (currentVolume < ((int)(0.6f*maxVolume))) {// 为避免调节过快，distanceY应大于一个设定值
                                currentVolume++;
                            }
                        } else if (distanceY <= -5.0*DensityUtil.dip2px(getApplicationContext(), STEP_VOLUME)) {// 音量调小
                        //} else {// 音量调小
                            if (currentVolume > ((int)(0.1f*maxVolume))) {
                                currentVolume--;
                                if (currentVolume == 0) {// 静音，设定静音独有的图片
                                }
                            }
                        }
                        audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
                    }
                }
                // 如果每次触摸屏幕后第一次scroll是调节亮度，那之后的scroll事件都处理亮度调节，直到离开屏幕执行下一次操作
                else if (GESTURE_FLAG == GESTURE_MODIFY_BRIGHT) {
                    if (mBrightness < 0) {
                        mBrightness = getWindow().getAttributes().screenBrightness;
                        if (mBrightness <= 0.00f)
                            mBrightness = 0.50f;
                        if (mBrightness < 0.01f)
                            mBrightness = 0.01f;
                    }
                    WindowManager.LayoutParams lpa = getWindow().getAttributes();
                    lpa.screenBrightness = mBrightness + (mOldY - y) / playerHeight;
                    if (lpa.screenBrightness > 1.0f)
                        lpa.screenBrightness = 1.0f;
                    else if (lpa.screenBrightness < 0.01f)
                        lpa.screenBrightness = 0.01f;
                    getWindow().setAttributes(lpa);
                }

                firstScroll = false;// 第一次scroll执行完成，修改标志
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        gestureDetector.setIsLongpressEnabled(true);
        touchEventView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(GESTURE_FLAG==GESTURE_MODIFY_PROGRESS) {
                        player.seekTo(player.getCurrentPosition() + playingTime);
                        playingTime = 0;
                    }
                    GESTURE_FLAG = 0;// 手指离开屏幕后，重置调节音量或进度的标志
                }
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void setUpHomeItermViews() {
        gridView=findViewById(R.id.home_GV);
        gridView.setAdapter(new HomeItemAdapter(this,new String[]{"VIDEO","PHOTO","YOUTUBE"},new String[]{"Play local videos","Photo slide show","Watch youtube videos"},
                new int[]{R.drawable.mb_video,R.drawable.mb_photo,R.drawable.mb_web}));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:openVideo();break;
                    case 1:openPhoto();break;
                    case 2:openYoutube();break;
                }
            }
        });
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR+"/sdcard");
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR+"/sdcard");
        properties.offset = new File(DialogConfigs.DEFAULT_DIR+"/sdcard");
        properties.extensions = null;
        filePickerDialog = new FilePickerDialog(this,properties);
    }

    private void openYoutube() {
        findViewById(R.id.home_view).setVisibility(View.GONE);
        findViewById(R.id.youtube_search_view).setVisibility(View.VISIBLE);
    }

    private void openPhoto() {
        openFile(1);
    }

    private void openVideo() {
        openFile(0);
    }
    private void openFile(final int tag) {
        filePickerDialog.setTitle(tag==0?"Select a Video File":"Select a Picture File");
        filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files == null || files.length < 1) {
                    return;
                }
                String selectedFilePath = files[0];
                String selectedDirPath = (new File(selectedFilePath)).getParent().toString();
                properties.offset = new File(selectedDirPath);
                if(tag==0) {
                    player.prepare(new ExtractorMediaSource(Uri.fromFile(new File(selectedFilePath)), dataSourceFactory, extractorsFactory, null, null));
                    playNewMovie();
                }else {
                    player.prepare(null);
                    DisplayPresentation.hideMovieView();
                    DisplayPresentation.showPicView(selectedFilePath);
                }
            }
        });
        filePickerDialog.show();
    }

    private void playNewMovie() {
        DisplayPresentation.hidePicView();
        DisplayPresentation.showMovieView();

        if(is169){
            stretch.performClick();
        }
        if(frameImgFormatEnum==VideoViewFilterParams.FrameImgFormatEnum.Format3D){
            mode.performClick();
        }

        player.setPlayWhenReady(true);
        isPlaying=true;
        playPause.setBackgroundColor(Color.YELLOW);
    }

    private void setUpSimpleExoPlayer() {
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"), new DefaultBandwidthMeter());
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState==Player.STATE_BUFFERING) DisplayPresentation.showLoadingView();
                else DisplayPresentation.hideLoadingView();
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    private void setUoGlPlayerView() {
        ePlayerView = new EPlayerView(this.getApplicationContext());
        ePlayerView.setSimpleExoPlayer(player);
        ePlayerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ePlayerView.onResume();
        ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));
    }

    private void castMovieToGlass() {
        app=this;
        checkDrawOverlayPermission();
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
    private void startGlass() {
        glassService = new Intent(this, IGlassService.class);
        startService(glassService);
    }

    ////////////////All below are for youtube playing////////////////////////
    private class RunTask extends AsyncTask<String,String,List<data>> {
        List<data> myData;
        boolean prescroll;

        private RunTask() {
            this.myData = new ArrayList();
            this.prescroll = false;
        }
        @Override
        protected List<data> doInBackground(String... strings) {
            int i;
            if (strings[1].equals("0")) {
                IGLassMainActivity.this.resetPages();
            }
            String getData = "";
            String idDuration = "";
            try {
                getData = IGLassMainActivity.this.getJson(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jSONObject = new JSONObject(getData);
                if (jSONObject.isNull("nextPageToken")) {
                    //SearchActivity.this.scroll = false;
                } else {
                    IGLassMainActivity.this.pages.add(jSONObject.getString("nextPageToken"));
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
                JSONArray items = new JSONObject(IGLassMainActivity.this.getJson("https://www.googleapis.com/youtube/v3/videos?" + IGLassMainActivity.MyAcessTokenData +
                        IGLassMainActivity.this.pref.getString(DeveloperKey.AcessToken, "none") + "&" + "part=contentDetails,statistics" + "&" + "id=" + idDuration + "&" +
                        "key=AIzaSyA6Sp0Jo0PdZmY0VYXwDSGsTk16yHcjEYA")).getJSONArray("items");
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
        protected void onPostExecute(List<data> myData) {
            myDataAll.addAll(myData);
            if (this.prescroll && IGLassMainActivity.this.pages.size() > 1) {
                IGLassMainActivity.this.scroll = true;
            }
            if (IGLassMainActivity.this.myDataAll.size() > 20) {
                IGLassMainActivity.this.mAdapter.notifyDataSetChanged();
            } else {
                IGLassMainActivity.this.mAdapter = new ListAdapter(IGLassMainActivity.this.myDataAll);
                IGLassMainActivity.this.mRecyclerView.setAdapter(IGLassMainActivity.this.mAdapter);
                if (!IGLassMainActivity.this.myDataAll.isEmpty()) {
                    IGLassMainActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
            mAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    Toast.makeText(getApplicationContext(),"播放第"+(position+1)+"个视频",Toast.LENGTH_SHORT).show();
                    playPause.setBackgroundColor(Color.YELLOW);
                    isPlaying=true;
                    palyYoutubeWithID(IGLassMainActivity.this.mAdapter.myData.get(position).getId(),false);
                    player.setPlayWhenReady(false);
                    DisplayPresentation.showLoadingView();
                }
                @Override
                public void onLongClick(int position) {
                    //Toast.makeText(getApplicationContext(),"您长按点击了"+position+"行",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void newSearch(){
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            // notify user you are online
        } else {
            // notify user you are not online
            return;
        }
        new IGLassMainActivity.RunTask().execute(url(0),"0");
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView recyclerView) {
                if (IGLassMainActivity.this.scroll) {
                    new IGLassMainActivity.RunTask().execute(new String[]{url(page), (page + 1) + ""});
                }
            }
        });
    }

    private void palyYoutubeWithID(String id, boolean is360) {

        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            // notify user you are online
        } else {
            // notify user you are not online
            return;
        }
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
                        if (key.contains(Integer.valueOf(IGLassMainActivity.itag))) {
                            downloadUrl = ((YtFile) ytFiles.get(IGLassMainActivity.itag)).getUrl();
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
                            playNewMovie();
                            return;
                        }
                    }
                    System.out.println("YTFILES is null");
                }
            }.execute(new String[]{youtubeLink});
            /*
            new YouTubeExtractor(this) {
                @Override
                public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                    if (ytFiles != null) {
                        int itag = 22;
                        String downloadUrl = ytFiles.get(itag).getUrl();
                    }
                }
            }.extract(youtubeLink, true, true);*/
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
                    + "&" + "pageToken=" + this.pages.get(pg) + "&" + "maxResults=20" + "&" + "q=" + URLEncoder.encode(curString, CharEncoding.UTF_8) + "&" + "key=AIzaSyA6Sp0Jo0PdZmY0VYXwDSGsTk16yHcjEYA";
            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return url;
        }
    }

    public void resetPages() {
        this.myDataAll = new ArrayList();
        this.scroll = false;
        this.pages = new ArrayList();
        this.pages.add("");
    }

}
