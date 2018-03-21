package com.iglassus.exoplayerfilter;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.iglassus.epf.EPlayerView;

/**
 * Created by AdminUser on 3/20/2018.
 */

public class DifferentDisplay extends Presentation {
    //public VideoView videoView;
    public ImageView imageView;

    public DifferentDisplay(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iglass_screen);
        MovieWrapperView movieWrapperView=findViewById(R.id.layout_movie_wrapper1);
        imageView=new ImageView(getContext());
        imageView.setImageResource(R.drawable.cat);
        movieWrapperView.addView(imageView);
        //videoView=findViewById(R.id.iGlassVideoView);
        //imageView=findViewById(R.id.catImage);
        //imageView.setImageResource(R.drawable.cat);
        //this.addContentView( MainActivity.ePlayerView,null);
    }
}