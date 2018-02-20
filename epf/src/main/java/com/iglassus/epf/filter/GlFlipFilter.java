package com.iglassus.epf.filter;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by AsusUser on 1/20/2018.
 */

public class GlFlipFilter extends GlFilter {

    public GlFlipFilter(Context context, boolean threeD_TF) {
        AssetManager assetManager = context.getAssets();
        String fragStr = null;
        String filePath = "";
        if (threeD_TF) {
            filePath = "opengl/flip_3d.frag";
        } else {
            filePath = "opengl/2d_toflip3d.frag";
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
}
