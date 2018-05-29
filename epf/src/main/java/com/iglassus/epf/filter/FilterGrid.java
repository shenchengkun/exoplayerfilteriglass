package com.iglassus.epf.filter;

public class FilterGrid {
    private int height,width;
    float[] vertices, texels,verticesRight;
    int[] indices;

    public FilterGrid(int height, int width, float[] vertices, float[] texels, float[] verticesRight, int[] indices) {
        this.height = height;
        this.width = width;
        this.vertices = vertices;
        this.texels = texels;
        this.verticesRight = verticesRight;
        this.indices = indices;
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

    public float[] getTexels() {
        return texels;
    }

    public float[] getVerticesRight() {
        return verticesRight;
    }

    public int[] getIndices() {
        return indices;
    }

    public int getIndicesCount() {
        return (height * width) + (width - 1) * (height - 2);
    }
}
