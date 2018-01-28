package com.daasuu.exoplayerfilter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.daasuu.epf.EPlayerView;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private EPlayerView ePlayerView;
    private SimpleExoPlayer player;
    private Button button;
    private SeekBar seekBar;
    private PlayerTimer playerTimer;

    // for file chooser
    DialogProperties properties = new DialogProperties();
    FilePickerDialog dialog;

    // for videoFileReformatter
    String inputVideoFilePath;
    String shaderFileNameStr = "flip_3d.frag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews();

        // https://developer.android.com/training/system-ui/immersive.html
        // Hide the status bar on Android 4.1 (API level 16) and higher:
        // https://bradmartin.net/2016/03/10/fullscreen-and-navigation-bar-color-in-a-nativescript-android-app/
        View decorView = getWindow().getDecorView();
        int uiOptions =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                 | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                 | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
//        // Remember that you should never show the action bar if the
//        // status bar is hidden, so hide that too if necessary.
//        ActionBar actionBar = getActionBar();
//        actionBar.hide();


        // spinner to choose the frame size
        Spinner image2dor3dformat_spinner = (Spinner) findViewById(R.id.image2dor3dformat_spinner);
        List<String> image2dor3dformaList = new ArrayList<String>();
        image2dor3dformaList.add("3D format");
        image2dor3dformaList.add("2D format");
        final List<String> shaderFilePathStrList = new ArrayList<String>();
        shaderFilePathStrList.add("flip_3d.frag");
        shaderFilePathStrList.add("flip_2d.frag");

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
                FilterType filterType;
                if (pos == 0) {
                    // 3D
                    filterType = FilterType.FLIP3D;
                } else {
                    filterType = FilterType.FLIP2D;
                }
//                ePlayerView.setGlFilter(FilterType.createGlFilter(filterType, getApplicationContext()));
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
        dialog = new FilePickerDialog(this,properties);
        dialog.setTitle("Select a Video File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
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

                // Prepare the player with the source.
                player.prepare(videoSource);
                player.setPlayWhenReady(true);

            }
        });
    }

    public void chooseVideoFileToProcess(@SuppressWarnings("unused") View unused) {
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setUpSimpleExoPlayer();
//        setUoGlPlayerView();
//        setUpTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
        if (playerTimer != null) {
            playerTimer.stop();
            playerTimer.removeMessages(0);
        }
    }

    private void setUpViews() {
        // play pause
        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player == null) return;

                if (button.getText().toString().equals(MainActivity.this.getString(R.string.pause))) {
                    player.setPlayWhenReady(false);
                    button.setText(R.string.play);
                } else {
                    player.setPlayWhenReady(true);
                    button.setText(R.string.pause);
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

    }


    private void setUoGlPlayerView() {
        // In xml, MovieWrapperView wrap_content height, let the LinearLayout below not visible
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
//        MovieWrapperView movieWrapperView = (MovieWrapperView) findViewById(R.id.layout_movie_wrapper);
//        movieWrapperView.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenHeight - 100));
//
//        ePlayerView = new EPlayerView(this);
//        ePlayerView.setSimpleExoPlayer(player);
//        ePlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).addView(ePlayerView);
//        ePlayerView.onResume();
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
//        ePlayerView.onPause();
//        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).removeAllViews();
//        ePlayerView = null;
//        player.stop();
//        player.release();
//        player = null;
    }


}