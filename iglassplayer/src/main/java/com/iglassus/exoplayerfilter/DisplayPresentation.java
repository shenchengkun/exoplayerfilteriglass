package com.iglassus.exoplayerfilter;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by AdminUser on 3/20/2018.
 */

public class DisplayPresentation extends Presentation {
    ImageView imageView;

    public DisplayPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iglass_screen);

        MovieWrapperView movieWrapperView=findViewById(R.id.layout_movie_wrapper_iGlass);
        movieWrapperView.addView(MainActivity.ePlayerView);
        imageView=new ImageView(getContext());
        imageView.setImageResource(R.drawable.cat);
        //movieWrapperView.addView(imageView);

    }
}