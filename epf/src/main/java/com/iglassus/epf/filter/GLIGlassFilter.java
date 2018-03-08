package com.iglassus.epf.filter;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by AsusUser on 2/4/2018.
 */

public class GLIGlassFilter extends GlFilter {
    private float upperPadding_percentage;
    private float bottomPadding_percentage;
    private float leftHalfImgLeftPadding_percentage;
    private float leftHalfImgRightPadding_percentage;
    private float centerX;
    private float centerY;
    private float radius;
    private float scale;
    private float flip;
    private float distortion;
    private float dup;


    public GLIGlassFilter(Context context, VideoViewFilterParams videoViewFilterParams) {
        AssetManager assetManager = context.getAssets();
        String fragStr = null;
        String filePath = "";

        centerX = 0.5f;
        centerY = 0.5f;
        radius = 0.5f;
        scale = 0.5f;

        distortion=videoViewFilterParams.distortion?1f:0f;
        flip=videoViewFilterParams.flip?1f:0f;
        upperPadding_percentage = videoViewFilterParams.upperPadding_percentage;
        bottomPadding_percentage = videoViewFilterParams.bottomPadding_percentage;
        leftHalfImgLeftPadding_percentage = videoViewFilterParams.leftHalfImgLeftPadding_percentage;
        leftHalfImgRightPadding_percentage = videoViewFilterParams.leftHalfImgRightPadding_percentage;

        Log.d("GLIGlassFilter", "upperPadding_percentage: " + upperPadding_percentage);
        Log.d("GLIGlassFilter", "bottomPadding_percentage: " + bottomPadding_percentage);
        Log.d("GLIGlassFilter", "leftHalfImgLeftPadding_percentage: " + leftHalfImgLeftPadding_percentage);
        Log.d("GLIGlassFilter", "leftHalfImgRightPadding_percentage: " + leftHalfImgRightPadding_percentage);

        if (videoViewFilterParams.frameImgFormat == VideoViewFilterParams.FrameImgFormatEnum.Format3D) {
            filePath = "opengl/flip_3d_padding.frag";
            dup=0f;
        } else if (videoViewFilterParams.frameImgFormat == VideoViewFilterParams.FrameImgFormatEnum.Format2D){
            filePath = "opengl/2d_toflip3d_padding.frag";
            dup=1f;
        } else if (videoViewFilterParams.frameImgFormat == VideoViewFilterParams.FrameImgFormatEnum.Format1D) {
            filePath = "opengl/1d_flip_padding.frag";
            dup=0f;
        }

        filePath="opengl/myFrag.frag";
        try {
            InputStream fileInputStream = assetManager.open(filePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            fileInputStream.close();
            fragStr = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setVertexShaderSource(DEFAULT_VERTEX_SHADER);
        setFragmentShaderSource(fragStr);
    }

    @Override
    protected void onDraw() {
        super.onDraw();

        GLES20.glUniform1f(getHandle("upperPadding_percentage"), upperPadding_percentage);
        GLES20.glUniform1f(getHandle("bottomPadding_percentage"), bottomPadding_percentage);
        GLES20.glUniform1f(getHandle("leftHalfImgLeftPadding_percentage"), leftHalfImgLeftPadding_percentage);
        GLES20.glUniform1f(getHandle("leftHalfImgRightPadding_percentage"), leftHalfImgRightPadding_percentage);
        GLES20.glUniform1f(getHandle("flip"), flip);
        GLES20.glUniform1f(getHandle("distortion"), distortion);
        GLES20.glUniform1f(getHandle("dup"), dup);


        GLES20.glUniform2f(getHandle("center"), centerX, centerY);
        GLES20.glUniform1f(getHandle("radius"), radius);
        GLES20.glUniform1f(getHandle("scale"), scale);

    }
}
