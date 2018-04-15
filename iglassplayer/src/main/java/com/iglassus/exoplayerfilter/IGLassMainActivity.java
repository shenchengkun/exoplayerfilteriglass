package com.iglassus.exoplayerfilter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.GridView;

public class IGLassMainActivity  extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iglass_layout_main);
        setUpViews();
    }

    private void setUpViews() {
        GridView gridView=findViewById(R.id.homeGV);
        gridView.setAdapter(new HomeItemAdapter(this,new String[]{"VIDEO","PHONE","YOUTUBE"},new String[]{"Play local videos","Photo Slide Show","Watch web videos"},
                new int[]{R.drawable.mb_video,R.drawable.mb_photo,R.drawable.mb_web}));
    }
}
