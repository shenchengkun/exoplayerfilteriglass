package com.iglassus.exoplayerfilter.filtersample;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.iglassus.epf.filter.GlOverlayFilter;

/**
 * Created by sudamasayuki on 2017/05/19.
 */

public class GlBitmapOverlaySample extends GlOverlayFilter {

    private Bitmap bitmap;

    public GlBitmapOverlaySample(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    protected void drawCanvas(Canvas canvas) {

        canvas.drawBitmap(bitmap, 0, 0, null);

    }
}