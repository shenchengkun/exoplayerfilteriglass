package com.iglassus.exoplayerfilter;

import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by AdminUser on 3/14/2018.
 */

/**
 * Created by AdminUser on 3/14/2018.
 */

public class Grid {
    //private final Context context;
    private int height,width;
    float[] vertices, texels,verticesRight;
    int[] indices;
    Workbook book;

    public Grid(int height, int width, Workbook workbook) {
        this.height = height;
        this.width = width;
        this.book=workbook;
        initVertices();
        initTexels();
        initIndices();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getVerticesRight() {
        return verticesRight;
    }

    public float[] getTexels() {
        return texels;
    }

    public int[] getIndices() {
        return indices;
    }

    private void initVertices() {

        vertices = new float[height*width*3];
        verticesRight = new float[height*width*3];
        int i = 0;
/*
        //////////////////////////////normal//////////////////////////////
        float h = (float)height-1;
        float w = (float) width - 1;
        for(int row = 0; row < height; row++) {
            float roww=2*row/h-1;
            for(int col = 0; col < width; col++) {
                float coll=2*col/w-1;
                //vertices[i++] = coll/(2-row/(height-1));
                vertices[i++] = coll;
                vertices[i++] = roww;
                vertices[i++] = 0.0f;
            }
        }
 */
        //////////////////////////////transform//////////////////////////////
        //double r     = Math.sqrt(0.5*0.5 + 1.75*1.75);
        //double theta = Math.atan2(1.75, -0.5);
        //double r1,theta1;
        //for(int row = 0; row < height; row++) {
        //    r1 = r + row * r/ (height - 1) ;   //这个地方，r必须放在除号前面，否则算之前并不会自动转double导致真个结果一直为零~！！！！！！！
        //    for (int col = 0; col < width; col++) {
        //        theta1=(Math.PI-2*theta)/(width-1)*col+theta;
        //        vertices[i] = ((float) (r1*Math.cos(theta1))-1)/2;verticesRight[i]=vertices[i]+1;i++;
        //        vertices[i] = (float) (r1*Math.sin(theta1)-2.75);verticesRight[i]=vertices[i];i++;
        //        vertices[i] = 0.0f;verticesRight[i]=vertices[i];i++;
        //    }
        //}
        Sheet sheet = book.getSheet(0);
        for (int j=1;j<=169;j++){
            Float x=Float.valueOf(sheet.getCell(16,j).getContents()),y=Float.valueOf(sheet.getCell(17,j).getContents());
            vertices[i] = (x-1f)/2;verticesRight[i]=vertices[i]+1;i++;
            vertices[i] = y;verticesRight[i]=vertices[i];i++;
            vertices[i] = 0.0f;verticesRight[i]=vertices[i];i++;
            //Log.i("提取",String.valueOf(y));
        }
    }


    private void initTexels() {
        texels = new float[height*width*2];
        int i = 0;
        float h = (float)height-1;
        float w = (float) width - 1;
        for(int row = 0; row < height; row++) {
            float roww=row/h;
            for(int col = 0; col < width; col++) {
                float coll=col/w;
                texels[i++] = coll;
                texels[i++] = roww;
            }
        }
    }

    private void initIndices() {
        // http://stackoverflow.com/questions/5915753/generate-a-plane-with-triangle-strips
        indices = new int[getIndicesCount()];
        int i = 0;

        // Indices for drawing cube faces using triangle strips. Triangle
        // strips can be connected by duplicating indices between the
        // strips. If connecting strips have opposite vertex order, then
        // the last index of the first strip and the first index of the
        // second strip need to be duplicated. If connecting strips have
        // same vertex order, then only the last index of the first strip
        // needs to be duplicated.
        for(int row = 0; row < height - 1; row++) {
            if(row % 2 == 0) { // even rows
                for(int col = 0; col < width; col++) {
                    indices[i++] = col + row * width;
                    indices[i++] = col + (row + 1) * width;
                }
            } else {           // odd rows
                for(int col = width - 1; col > 0; col--) {
                    indices[i++] = col + (row + 1) * width;
                    indices[i++] = (col - 1) + row * width;
                }
            }
        }
        if ( (height%2!=0) && height>2) {
            indices[i++] = (height-1) * width;
        }


    }

    public int getIndicesCount() {
        return (height * width) + (width - 1) * (height - 2);
    }

}