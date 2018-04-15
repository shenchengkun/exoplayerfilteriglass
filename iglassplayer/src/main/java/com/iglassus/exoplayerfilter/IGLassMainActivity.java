package com.iglassus.exoplayerfilter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
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

public class IGLassMainActivity  extends Activity{
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

    private SimpleExoPlayer player;
    public static EPlayerView ePlayerView;

    private DefaultDataSourceFactory dataSourceFactory;
    private DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
    private DialogProperties properties = new DialogProperties();
    private FilePickerDialog filePickerDialog;

    private ImageView playPause,stretch,mode;
    private boolean isPlaying=false,is169=false;

    public Intent glassService;
    public final static int REQUEST_CODE = -1010101;

    private ArrayList<String> pages;
    private String[] SUGGESTION = new String[]{"Belgium", "France", "Italy", "Germany", "Spain"};
    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean scroll;
    private ArrayList myDataAll;
    private String curString="victoria's secret"; private OkHttpClient client;
    static String MyAcessTokenData = "access_token=";
    private SharedPreferences pref;
    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;
    private SeekBar seekBar;
    private PlayerTimer playerTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iglass_layout_main);

        setUpSimpleExoPlayer();
        setUoGlPlayerView();
        setUpControlPanel();
        setUpHomeItermViews();
        castMovieToGlass();
        setUpYoutubeView();
        newSearch();
    }

    private void setUpYoutubeView() {
        this.mRecyclerView = findViewById(R.id.mlist);
        mLinearLayoutManager = new LinearLayoutManager(this);
        this.mRecyclerView.setLayoutManager(mLinearLayoutManager);
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
                autoCompleteTextView.setBackgroundResource(R.drawable.picb46);
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
                if (event.getAction() != 1 || event.getRawX() < ((float) (IGLassMainActivity.this.autoCompleteTextView.getRight() - IGLassMainActivity.this.autoCompleteTextView.getCompoundDrawables()[2].getBounds().width()))) {
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

        findViewById(R.id.backToControl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(R.id.home_view).setVisibility(View.VISIBLE);
                findViewById(R.id.youtube_search_view).setVisibility(View.GONE);
            }
        });
        resetPages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if(glassService !=null) stopService(glassService);
    }

    private void releasePlayer() {
        ePlayerView.onPause();
        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).removeAllViews();
        ePlayerView = null;
        player.stop();
        player.release();
        player = null;
    }

    private void setUpControlPanel() {
        playPause=findViewById(R.id.play_pause);
        stretch=findViewById(R.id.stretch);
        mode=findViewById(R.id.mode);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlaying = isPlaying ? false : true;
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
                if (duration <= 0) return;

                seekBar.setMax((int) duration / 1000);
                seekBar.setProgress((int) position / 1000);
            }
        });
        playerTimer.start();
    }

    private void setUpHomeItermViews() {
        GridView gridView=findViewById(R.id.homeGV);
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
    }

    private void openYoutube() {
        findViewById(R.id.home_view).setVisibility(View.GONE);
        findViewById(R.id.youtube_search_view).setVisibility(View.VISIBLE);
    }

    private void openPhoto() {

    }

    private void openVideo() {
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
                if (files == null || files.length < 1) {
                    return;
                }
                String selectedFilePath = files[0];
                String selectedDirPath = (new File(selectedFilePath)).getParent().toString();
                properties.offset = new File(selectedDirPath);
                player.prepare(new ExtractorMediaSource(Uri.fromFile(new File(selectedFilePath)), dataSourceFactory, extractorsFactory, null, null));
                player.setPlayWhenReady(true);
            }
        });
        filePickerDialog.show();
    }

    private void setUpSimpleExoPlayer() {

        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"), new DefaultBandwidthMeter());
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
    }
    private void setUoGlPlayerView() {
        ePlayerView = new EPlayerView(this.getApplicationContext());
        ePlayerView.setSimpleExoPlayer(player);
        ePlayerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ePlayerView.onResume();
        ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, videoViewFilterParams, getApplicationContext()));
    }

    private void castMovieToGlass() {
        DisplayManager mDisplayManager = (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        if (displays.length <= 1) {
            Toast.makeText(this, "未检测到眼镜", Toast.LENGTH_LONG).show();
            Log.i("未检测到", "未检测到眼镜");
            //finishAndRemoveTask();
        } else {
            Toast.makeText(this, "检测到眼镜", Toast.LENGTH_SHORT).show();
            Log.i("检测到", "检测到眼镜");
            checkDrawOverlayPermission();
        }
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
                JSONArray items = new JSONObject(IGLassMainActivity.this.getJson("https://www.googleapis.com/youtube/v3/videos?" + MainActivity.MyAcessTokenData + IGLassMainActivity.this.pref.getString(DeveloperKey.AcessToken, "none") + "&" + "part=contentDetails,statistics" + "&" + "id=" + idDuration + "&" + "key=AIzaSyA6Sp0Jo0PdZmY0VYXwDSGsTk16yHcjEYA")).getJSONArray("items");
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
                    palyYoutubeWithID(IGLassMainActivity.this.mAdapter.myData.get(position).getId(),false);
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
