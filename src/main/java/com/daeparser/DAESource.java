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
    private List<String> names; // For Name_array (e.g., joint names)
    private int stride;
    private int count;

    public DAESource() {
        this.data = new ArrayList<>();
        this.names = new ArrayList<>();
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

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public void addName(String name) {
        this.names.add(name);
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
        int i = 0;
        for (Float value : data) {
            array[i++] = value;
        }
        return array;
    }

    public String[] getNamesAsArray() {
        return names.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return "DAESource{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dataSize=" + data.size() +
                ", namesSize=" + names.size() +
                ", stride=" + stride +
                ", count=" + count +
                '}';
    }
}
