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
    private String controllerRef; // Reference to a controller (for skinned meshes)
    private List<String> skeletonRefs; // References to skeleton root nodes
    private String materialRef;
    private String type; // Node type (NODE, JOINT, etc.)
    private float[] transformation;
    private List<DAENode> children;

    public DAENode() {
        this.children = new ArrayList<>();
        this.skeletonRefs = new ArrayList<>();
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

    public String getControllerRef() {
        return controllerRef;
    }

    public void setControllerRef(String controllerRef) {
        this.controllerRef = controllerRef;
    }

    public List<String> getSkeletonRefs() {
        return skeletonRefs;
    }

    public void setSkeletonRefs(List<String> skeletonRefs) {
        this.skeletonRefs = skeletonRefs;
    }

    public void addSkeletonRef(String skeletonRef) {
        this.skeletonRefs.add(skeletonRef);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    /**
     * Checks if this node is a joint node.
     * 
     * @return true if this node represents a skeleton joint
     */
    public boolean isJoint() {
        return "JOINT".equals(type);
    }

    @Override
    public String toString() {
        return "DAENode{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", geometryRef='" + geometryRef + '\'' +
                ", controllerRef='" + controllerRef + '\'' +
                ", skeletonRefs=" + skeletonRefs.size() +
                ", materialRef='" + materialRef + '\'' +
                ", children=" + children.size() +
                '}';
    }
}
