package com.iglassus.epf.filter;

import android.graphics.Bitmap;

/**
 * Created by AsusUser on 2/4/2018.
 */

public class VideoViewFilterParams {
    public final Bitmap bitmap;

    public enum FrameImgFormatEnum {
        Format3D,
        Format2D,
        Format1D
    }
    public boolean flip;
    public boolean distortion;
    public FrameImgFormatEnum frameImgFormat;
    public float upperPadding_percentage;
    public float bottomPadding_percentage;
    public float leftHalfImgLeftPadding_percentage;
    public float leftHalfImgRightPadding_percentage;

    public VideoViewFilterParams(boolean flip, boolean distortion, FrameImgFormatEnum frameImgFormat, float upperPadding_percentage, float bottomPadding_percentage, float leftHalfImgLeftPadding_percentage, float leftHalfImgRightPadding_percentage, Bitmap bitmap) {
        this.flip = flip;
        this.distortion = distortion;
        this.upperPadding_percentage = upperPadding_percentage;
        this.bottomPadding_percentage = bottomPadding_percentage;
        this.leftHalfImgLeftPadding_percentage = leftHalfImgLeftPadding_percentage;
        this.leftHalfImgRightPadding_percentage = leftHalfImgRightPadding_percentage;
        this.frameImgFormat=frameImgFormat;
        this.bitmap=bitmap;
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean isDistortion() {
        return distortion;
    }

    public void setDistortion(boolean distortion) {
        this.distortion = distortion;
    }

    public FrameImgFormatEnum getFrameImgFormat() {
        return frameImgFormat;
    }

    public void setFrameImgFormat(FrameImgFormatEnum frameImgFormat) {
        this.frameImgFormat = frameImgFormat;
    }

    public float getUpperPadding_percentage() {
        return upperPadding_percentage;
    }

    public void setUpperPadding_percentage(float upperPadding_percentage) {
        this.upperPadding_percentage = upperPadding_percentage;
    }

    public float getBottomPadding_percentage() {
        return bottomPadding_percentage;
    }

    public void setBottomPadding_percentage(float bottomPadding_percentage) {
        this.bottomPadding_percentage = bottomPadding_percentage;
    }

    public float getLeftHalfImgLeftPadding_percentage() {
        return leftHalfImgLeftPadding_percentage;
    }

    public void setLeftHalfImgLeftPadding_percentage(float leftHalfImgLeftPadding_percentage) {
        this.leftHalfImgLeftPadding_percentage = leftHalfImgLeftPadding_percentage;
    }

    public float getLeftHalfImgRightPadding_percentage() {
        return leftHalfImgRightPadding_percentage;
    }

    public void setLeftHalfImgRightPadding_percentage(float leftHalfImgRightPadding_percentage) {
        this.leftHalfImgRightPadding_percentage = leftHalfImgRightPadding_percentage;
    }
}
