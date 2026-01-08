package com.daeparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the scene hierarchy.
 */
public class DAENode {
    private String id;
    private String name;
    private String geometryRef;
    private String materialRef;
    private float[] transformation;
    private List<DAENode> children;

    public DAENode() {
        this.children = new ArrayList<>();
        this.transformation = new float[16];
        // Identity matrix
        transformation[0] = transformation[5] = transformation[10] = transformation[15] = 1.0f;
    }

    public DAENode(String id, String name) {
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

    public String getGeometryRef() {
        return geometryRef;
    }

    public void setGeometryRef(String geometryRef) {
        this.geometryRef = geometryRef;
    }

    public String getMaterialRef() {
        return materialRef;
    }

    public void setMaterialRef(String materialRef) {
        this.materialRef = materialRef;
    }

    public float[] getTransformation() {
        return transformation;
    }

    public void setTransformation(float[] transformation) {
        this.transformation = transformation;
    }

    public List<DAENode> getChildren() {
        return children;
    }

    public void setChildren(List<DAENode> children) {
        this.children = children;
    }

    public void addChild(DAENode child) {
        this.children.add(child);
    }

    @Override
    public String toString() {
        return "DAENode{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", geometryRef='" + geometryRef + '\'' +
                ", materialRef='" + materialRef + '\'' +
                ", children=" + children.size() +
                '}';
    }
}
