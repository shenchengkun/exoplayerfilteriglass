package com.iglassus.epf.filter;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

import com.iglassus.epf.EglUtil;

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

    private int hTex = EglUtil.NO_TEXTURE;

    public GLIGlassFilter(Context context, VideoViewFilterParams videoViewFilterParams) {
        AssetManager assetManager = context.getAssets();
        String fragStr = null;
        String filePath = "";

        upperPadding_percentage = videoViewFilterParams.upperPadding_percentage;
        bottomPadding_percentage = videoViewFilterParams.bottomPadding_percentage;
        leftHalfImgLeftPadding_percentage = videoViewFilterParams.leftHalfImgLeftPadding_percentage;
        leftHalfImgRightPadding_percentage = videoViewFilterParams.leftHalfImgRightPadding_percentage;

        Log.d("GLIGlassFilter", "upperPadding_percentage: " + upperPadding_percentage);
        Log.d("GLIGlassFilter", "bottomPadding_percentage: " + bottomPadding_percentage);
        Log.d("GLIGlassFilter", "leftHalfImgLeftPadding_percentage: " + leftHalfImgLeftPadding_percentage);
        Log.d("GLIGlassFilter", "leftHalfImgRightPadding_percentage: " + leftHalfImgRightPadding_percentage);


        if (videoViewFilterParams.threeD_TF) {
            filePath = "opengl/flip_3d_padding.frag";
        } else {
            filePath = "opengl/2d_toflip3d_padding.frag";
        }
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

        int upperPadding_percentage_uniform = getHandle("upperPadding_percentage");
        int bottomPadding_percentage_uniform = getHandle("bottomPadding_percentage");
        int leftHalfImgLeftPadding_percentage_uniform = getHandle("leftHalfImgLeftPadding_percentage");
        int leftHalfImgRightPadding_percentage_uniform = getHandle("leftHalfImgRightPadding_percentage");

        GLES20.glUniform1f(upperPadding_percentage_uniform, upperPadding_percentage);
        GLES20.glUniform1f(bottomPadding_percentage_uniform, bottomPadding_percentage);
        GLES20.glUniform1f(leftHalfImgLeftPadding_percentage_uniform, leftHalfImgLeftPadding_percentage);
        GLES20.glUniform1f(leftHalfImgRightPadding_percentage_uniform, leftHalfImgRightPadding_percentage);


//        // // Option 1
        int lutTexture_uniform = getHandle("lutTexture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, hTex);
        GLES20.glUniform1i(lutTexture_uniform, 3);
//        // // Option 2
        // int lutTexture_uniform = getHandle("lutTexture");
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, hTex);
//        GLES20.glUniform1i(lutTexture_uniform, 1);
    }

    @Override
    public void setup() {
        super.setup();
        loadTexture();
    }

    private void loadTexture() {
        if (hTex == EglUtil.NO_TEXTURE) {
            // lutDataForDistortion
            int textureWidth = 100;
            int textureHeight = 100;
//            float[] lutDataForDistortion = new float[textureHeight * textureWidth * 3];
            float[] lutDataForDistortion = new float[textureHeight * textureWidth * 2];

            // for debug test:
            // create a fake one:
            // texture: column-major order
            // (x,y) [0.0, 1.0] ==>
            int index = 0;
            for (int row = 0; row < textureHeight; row++) {
                for (int col = 0; col < textureWidth; col++) {
                    float x = col / (textureWidth - 1.0f);
                    float y = row / (textureHeight - 1.0f);

                    // newx, newy
                    float x2 = x - 0.5f;
                    float y2 = y - 0.5f;

                    float r_sqr = x2 * x2 + y2 * y2;
                    float K1 = 0.0f; // 0.04f;
                    float xu = x + (x - 0.5f) * (K1 * r_sqr);
                    float yu = y + (y - 0.5f) * (K1 * r_sqr);

//                    lutDataForDistortion[index] = xu;
//                    index++;
//                    lutDataForDistortion[index] = yu;
//                    index++;
//                    lutDataForDistortion[index] = 0.0f;
//                    index++;


                    lutDataForDistortion[index] = xu;
                    index++;
                    lutDataForDistortion[index] = yu;
                    index++;


                }
            }

            hTex = EglUtil.loadTexture(lutDataForDistortion, textureWidth, textureHeight, hTex);
        }
    }
}
