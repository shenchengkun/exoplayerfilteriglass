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
        // 0.0 -> 1.0
        // 0 -> 2^8 -1, or 2^16 - 1, or ..., depending how many bytes you want to use
        int rows = 10;
        int cols = 10;
        byte[] lookupTableData_byte1_FromRight = new byte[rows*cols*3];
        byte[] lookupTableData_byte2_FromRight = new byte[rows*cols*3];
        int pixelIndex = 0;
        for (int row = 0; row < rows; row++) {
            double x = row / (rows - 1.0);
            for (int col = 0; col < cols; col++) {
                double y = col / (cols - 1.0f);

                double r = Math.sqrt( x*x + y*y );

                double newx = x;
                double tmp = Math.abs(x - 0.5);
                double newy = tmp * tmp * tmp + y;
                newy = Math.min(newy, 1.0);

                // Option 1:
                int newx_2bytes = (int) Math.round(newx * (65536 - 1.0));
                int newy_2bytes = (int) Math.round(newy * (65536 - 1.0));

                // Checked the values below, using: http://www.exploringbinary.com/binary-converter/
                int newx_No1byte = (byte) newx_2bytes;
                int newx_No2byte = (byte) (newx_2bytes >>> 8);
                int newy_No1byte = (byte) newy_2bytes;
                int newy_No2byte = (byte) (newy_2bytes >>> 8);

                lookupTableData_byte1_FromRight[pixelIndex*3 + 0] = (byte) newx_No1byte;
                lookupTableData_byte1_FromRight[pixelIndex*3 + 1] = (byte) newy_No1byte;

                lookupTableData_byte2_FromRight[pixelIndex*3 + 0] = (byte) newx_No2byte;
                lookupTableData_byte2_FromRight[pixelIndex*3 + 1] = (byte) newy_No2byte;


                // Option 2:

                pixelIndex ++;

            }
        }
        ByteBuffer byte1_FromRight_buffer = ByteBuffer.allocate(rows*cols*3);
        byte1_FromRight_buffer.put(lookupTableData_byte1_FromRight);
        byte1_FromRight_buffer.position(0);

        ByteBuffer byte2_FromRight_buffer = ByteBuffer.allocate(rows*cols*3);
        byte2_FromRight_buffer.put(lookupTableData_byte2_FromRight);
        byte2_FromRight_buffer.position(0);

        buffer1 = byte1_FromRight_buffer;
        buffer2 = byte2_FromRight_buffer;
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

        // Note: GL_TEXTURE2, should match the number "2"
        int NO1_byte_fromRight_Texture_uniform = getHandle("NO1_byte_fromRight_Texture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureHandle1);
        GLES20.glUniform1i(NO1_byte_fromRight_Texture_uniform, 2);

        int NO2_byte_fromRight_Texture_uniform = getHandle("NO2_byte_fromRight_Texture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureHandle2);
        GLES20.glUniform1i(NO2_byte_fromRight_Texture_uniform, 3);

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
