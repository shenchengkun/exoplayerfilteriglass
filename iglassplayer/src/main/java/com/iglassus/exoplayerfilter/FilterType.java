package com.iglassus.exoplayerfilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.iglassus.epf.filter.GLIGlassFilter;
import com.iglassus.epf.filter.GlBilateralFilter;
import com.iglassus.epf.filter.GlBoxBlurFilter;
import com.iglassus.epf.filter.GlBulgeDistortionFilter;
import com.iglassus.epf.filter.GlCGAColorspaceFilter;
import com.iglassus.epf.filter.GlFilter;
import com.iglassus.epf.filter.GlFilterGroup;
import com.iglassus.epf.filter.GlFlipFilter;
import com.iglassus.epf.filter.GlGaussianBlurFilter;
import com.iglassus.epf.filter.GlGrayScaleFilter;
import com.iglassus.epf.filter.GlHazeFilter;
import com.iglassus.epf.filter.GlInvertFilter;
import com.iglassus.epf.filter.GlLookUpTableFilter;
import com.iglassus.epf.filter.GlMonochromeFilter;
import com.iglassus.epf.filter.GlSepiaFilter;
import com.iglassus.epf.filter.GlSharpenFilter;
import com.iglassus.epf.filter.GlSphereRefractionFilter;
import com.iglassus.epf.filter.GlToneCurveFilter;
import com.iglassus.epf.filter.GlVignetteFilter;
import com.iglassus.epf.filter.VideoViewFilterParams;
import com.iglassus.exoplayerfilter.filtersample.GlBitmapOverlaySample;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudamasayuki on 2017/05/18.
 */

public enum FilterType {
    DEFAULT,
    IGLASS,
    FLIP3D,
    FLIP2D,
    BILATERAL_BLUR,
    BOX_BLUR,
    TONE_CURVE_SAMPLE,
    LOOK_UP_TABLE_SAMPLE,
    BULGE_DISTORTION,
    CGA_COLORSPACE,
    GAUSSIAN_FILTER,
    GRAY_SCALE,
    HAZE,
    INVERT,
    MONOCHROME,
    SEPIA,
    SHARP,
    VIGNETTE,
    FILTER_GROUP_SAMPLE,
    SPHERE_REFRACTION,
    BITMAP_OVERLAY_SAMPLE;


    public static List<FilterType> createFilterList() {
        List<FilterType> filters = new ArrayList<>();

        filters.add(DEFAULT);
        filters.add(IGLASS);
        filters.add(FLIP3D);
        filters.add(SEPIA);
//        filters.add(MONOCHROME);
        filters.add(TONE_CURVE_SAMPLE);
//        filters.add(LOOK_UP_TABLE_SAMPLE);
//        filters.add(VIGNETTE);
//        filters.add(INVERT);
//        filters.add(HAZE);
//        filters.add(BOX_BLUR);
//        filters.add(BILATERAL_BLUR);
//        filters.add(GRAY_SCALE);
//        filters.add(SPHERE_REFRACTION);
//        filters.add(FILTER_GROUP_SAMPLE);
//        filters.add(GAUSSIAN_FILTER);
//        filters.add(BULGE_DISTORTION);
//        filters.add(CGA_COLORSPACE);
//        filters.add(SHARP);
//        filters.add(BITMAP_OVERLAY_SAMPLE);

        return filters;
    }

    public static GlFilter createGlFilter(FilterType filterType, VideoViewFilterParams videoViewFilterParams, Context context) {
        switch (filterType) {
            case DEFAULT:
                return new GlFilter();
            case IGLASS:
                return new GLIGlassFilter(context, videoViewFilterParams);
            case FLIP3D:
                return new GlFlipFilter(context, true);
            case FLIP2D:
                return new GlFlipFilter(context, false);
            case SEPIA:
                return new GlSepiaFilter();
            case GRAY_SCALE:
                return new GlGrayScaleFilter();
            case INVERT:
                return new GlInvertFilter();
            case HAZE:
                return new GlHazeFilter();
            case MONOCHROME:
                return new GlMonochromeFilter();
            case BILATERAL_BLUR:
                return new GlBilateralFilter();
            case BOX_BLUR:
                return new GlBoxBlurFilter();
            case LOOK_UP_TABLE_SAMPLE:
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lookup_sample);

                return new GlLookUpTableFilter(bitmap);
            case TONE_CURVE_SAMPLE:
                try {
                    InputStream is = context.getAssets().open("acv/tone_cuver_sample.acv");
                    return new GlToneCurveFilter(is);
                } catch (IOException e) {
                    Log.e("FilterType", "Error");
                }
                return new GlFilter();

            case SPHERE_REFRACTION:
                return new GlSphereRefractionFilter();
            case VIGNETTE:
                return new GlVignetteFilter();
            case FILTER_GROUP_SAMPLE:
                return new GlFilterGroup(new GlSepiaFilter(), new GlVignetteFilter());
            case GAUSSIAN_FILTER:
                return new GlGaussianBlurFilter();
            case BULGE_DISTORTION:
                return new GlBulgeDistortionFilter();
            case CGA_COLORSPACE:
                return new GlCGAColorspaceFilter();
            case SHARP:
                GlSharpenFilter glSharpenFilter = new GlSharpenFilter();
                glSharpenFilter.setSharpness(4f);
                return glSharpenFilter;
            case BITMAP_OVERLAY_SAMPLE:
                return new GlBitmapOverlaySample(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round));
            default:
                return new GlFilter();
        }
    }


}
