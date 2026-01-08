package com.daeparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a data source in a DAE mesh (positions, normals, texture coordinates, etc.).
 */
public class DAESource {
    private String id;
    private String name;
    private List<Float> data;
    private int stride;
    private int count;

    public DAESource() {
        this.data = new ArrayList<>();
    }

    public DAESource(String id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Float> getData() {
        return data;
    }

    public void setData(List<Float> data) {
        this.data = data;
    }

    public void addData(float value) {
        this.data.add(value);
    }

    public int getStride() {
        return stride;
    }

    public void setStride(int stride) {
        this.stride = stride;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float[] getDataAsArray() {
        float[] array = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            array[i] = data.get(i);
        }
        return array;
    }

    @Override
    public String toString() {
        return "DAESource{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dataSize=" + data.size() +
                ", stride=" + stride +
                ", count=" + count +
                '}';
    }
}
