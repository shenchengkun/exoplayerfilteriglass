package com.iglassus.exoplayerfilter;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by AdminUser on 3/20/2018.
 */

public class DisplayPresentation extends Presentation {
    public static ViewGroup loadingView,pictureView;
    public static MovieWrapperView movieWrapperView;
    public static ImageView pic1,pic2;
    public static Context context;

    public DisplayPresentation(Context outerContext, Display display) {
        super(outerContext, display);
        context=outerContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iglass_screen);

        loadingView=findViewById(R.id.buffering_view);
        movieWrapperView=findViewById(R.id.layout_movie_wrapper_iGlass);
        movieWrapperView.addView(IGLassMainActivity.ePlayerView);
        pictureView=findViewById(R.id.picture_view);
        pic1=findViewById(R.id.pic1);
        pic2=findViewById(R.id.pic2);
    }

    public static void hideLoadingView(){loadingView.setVisibility(View.GONE);}
    public static void showLoadingView(){loadingView.setVisibility(View.VISIBLE);}

    public static void hideMovieView(){movieWrapperView.setVisibility(View.GONE);}
    public static void showMovieView(){movieWrapperView.setVisibility(View.VISIBLE);}

    public static void hidePicView(){pictureView.setVisibility(View.GONE);}
    public static void showPicView(String path){
        pictureView.setVisibility(View.VISIBLE);
        Picasso.with(context).load(new File(path)).into(pic1);
        Picasso.with(context).load(new File(path)).into(pic2);
    }

}