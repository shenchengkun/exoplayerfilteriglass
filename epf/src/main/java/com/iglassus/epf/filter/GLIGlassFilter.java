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
import java.nio.ByteBuffer;

/**
 * Created by AsusUser on 2/4/2018.
 */

public class GLIGlassFilter extends GlFilter {
    private float upperPadding_percentage;
    private float bottomPadding_percentage;
    private float leftHalfImgLeftPadding_percentage;
    private float leftHalfImgRightPadding_percentage;

    private int textureHandle1 = EglUtil.NO_TEXTURE;
    private int textureHandle2 = EglUtil.NO_TEXTURE;
    private ByteBuffer buffer1;
    private ByteBuffer buffer2;
    private int buffer1_width;
    private int buffer1_height;
    private int buffer2_width;
    private int buffer2_height;

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


//        if (videoViewFilterParams.threeD_TF) {
//            filePath = "opengl/flip_3d_padding.frag";
//        } else {
//            filePath = "opengl/2d_toflip3d_padding.frag";
//        }

        if (videoViewFilterParams.frameImgFormat == VideoViewFilterParams.FrameImgFormatEnum.Format3D) {
            filePath = "opengl/flip_3d_padding.frag";
        } else if (videoViewFilterParams.frameImgFormat == VideoViewFilterParams.FrameImgFormatEnum.Format2D){
            filePath = "opengl/2d_toflip3d_padding.frag";
        } else if (videoViewFilterParams.frameImgFormat == VideoViewFilterParams.FrameImgFormatEnum.Format1D) {
            filePath = "opengl/1d_flip_padding.frag";
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

        // begin: test data: set array and buffer for lookup table
        int rows = 10;
        int cols = 10;
        byte[] lookupTableIntPartData = new byte[rows*cols*3];
        byte[] lookupTableDecimal255PartData = new byte[rows*cols*3];
        int pixelIndex = 0;
        for (int row = 0; row < rows; row++) {
            double x = row / (rows - 1.0);
            for (int col = 0; col < cols; col++) {
                double y = col / (cols - 1.0f);

                double r = Math.sqrt( x*x + y*y );

                double newx = x;
                double tmp = Math.abs(x - 0.5);
                double newy = tmp * tmp * tmp + y;

                // put the (newx, newy) in the texture
                // textureIntPart, textureDecimalPart
                int newxInt = (int) (newx);
                int newyInt = (int) (newy);

                double newx_decimal = newx - newxInt;
                // The value should be one within [0, 255]
                int newx_decimal255 = (int) (newx_decimal / 255.0);
                double newy_decimal = newy - newyInt;
                int newy_decimal255 = (int) (newy_decimal / 255.0);

                lookupTableIntPartData[pixelIndex*3 + 0] = (byte) newxInt;
                lookupTableIntPartData[pixelIndex*3 + 1] = (byte) newyInt;

                lookupTableDecimal255PartData[pixelIndex*3 + 0] = (byte) newx_decimal255;
                lookupTableDecimal255PartData[pixelIndex*3 + 1] = (byte) newy_decimal255;

                pixelIndex ++;

            }
        }
        ByteBuffer lookupTableIntPartData_buffer = ByteBuffer.allocate(rows*cols*3);
        lookupTableIntPartData_buffer.put(lookupTableIntPartData);
        lookupTableIntPartData_buffer.position(0);

        ByteBuffer lookupTableDecimal255PartData_buffer = ByteBuffer.allocate(rows*cols*3);
        lookupTableDecimal255PartData_buffer.put(lookupTableDecimal255PartData);
        lookupTableDecimal255PartData_buffer.position(0);

        buffer1 = lookupTableIntPartData_buffer;
        buffer2 = lookupTableDecimal255PartData_buffer;
        buffer1_width = cols;
        buffer1_height = rows;
        buffer2_width = cols;
        buffer2_height = rows;
        // end: test data: set array and buffer for lookup table

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

        int dintPartLUTTexture_uniform = getHandle("intPartLUTTexture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureHandle1);
        GLES20.glUniform1i(dintPartLUTTexture_uniform, 2);

        int decimal255PartLUTTexture_uniform = getHandle("decimal255PartLUTTexture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureHandle2);
        GLES20.glUniform1i(decimal255PartLUTTexture_uniform, 3);



    }

    @Override
    public void setup() {
        super.setup();
        loadTexture();
    }

    private void loadTexture() {
        if (textureHandle1 == EglUtil.NO_TEXTURE) {
            textureHandle1 = EglUtil.loadTextureFromByteBufferRGB(buffer1, buffer1_width, buffer1_height, EglUtil.NO_TEXTURE);
        }
        if (textureHandle2 == EglUtil.NO_TEXTURE) {
            textureHandle2 = EglUtil.loadTextureFromByteBufferRGB(buffer2, buffer2_width, buffer2_height, EglUtil.NO_TEXTURE);
        }

    }

}
