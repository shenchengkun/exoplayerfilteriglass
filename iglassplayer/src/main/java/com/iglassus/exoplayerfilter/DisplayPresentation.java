package com.iglassus.exoplayerfilter;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by AdminUser on 3/20/2018.
 */

public class DisplayPresentation extends Presentation {
    public static ViewGroup loadingView=null;

    public DisplayPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iglass_screen);
        loadingView=findViewById(R.id.bufferingView);
        loadingView.setVisibility(View.GONE);

        MovieWrapperView movieWrapperView=findViewById(R.id.layout_movie_wrapper_iGlass);
        movieWrapperView.addView(IGLassMainActivity.ePlayerView);
    }

    public static void hideLoadingView(){loadingView.setVisibility(View.GONE);}
    public static void showLoadingView(){loadingView.setVisibility(View.VISIBLE);}
}