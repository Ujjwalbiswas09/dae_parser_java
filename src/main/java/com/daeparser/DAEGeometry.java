package com.daeparser;

/**
 * Represents a geometry element in a DAE file.
 */
public class DAEGeometry {
    private String id;
    private String name;
    private DAEMesh mesh;

    public DAEGeometry() {
    }

    public DAEGeometry(String id, String name) {
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

    public DAEMesh getMesh() {
        return mesh;
    }

    public void setMesh(DAEMesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public String toString() {
        return "DAEGeometry{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mesh=" + mesh +
                '}';
    }
}
