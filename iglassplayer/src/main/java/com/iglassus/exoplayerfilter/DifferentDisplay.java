package com.iglassus.exoplayerfilter;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.VideoView;

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
        //videoView=findViewById(R.id.iGlassVideoView);
        imageView=findViewById(R.id.catImage);
        imageView.setImageResource(R.drawable.cat);
    }
}