package com.iglassus.exoplayerfilter;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;

/**
 * Created by AdminUser on 3/20/2018.
 */

public class DifferentDisplay extends Presentation {

    public DifferentDisplay(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iglass_screen);

        MovieWrapperView movieWrapperView=findViewById(R.id.layout_movie_wrapper_iGlass);
        movieWrapperView.addView(MainActivity.ePlayerView);

    }
}