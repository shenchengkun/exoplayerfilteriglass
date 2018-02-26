package com.iglassus.epf.filter;

/**
 * Created by AsusUser on 2/4/2018.
 */

public class VideoViewFilterParams {
    public enum FrameImgFormatEnum {
        Format3D,
        Format2D,
        Format1D
    }
    public float upperPadding_percentage = 0.0f;
    public float bottomPadding_percentage = 0.0f;
    public float leftHalfImgLeftPadding_percentage = 0.0f;
    public float leftHalfImgRightPadding_percentage = 0.0f;

    public boolean threeD_TF = true;


    public FrameImgFormatEnum frameImgFormat = FrameImgFormatEnum.Format3D;
}
