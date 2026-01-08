package com.daeparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a COLLADA DAE document containing 3D assets.
 */
public class DAEDocument {
    private String version;
    private List<DAEGeometry> geometries;
    private List<DAEMaterial> materials;
    private DAEScene scene;

    public DAEDocument() {
        this.geometries = new ArrayList<>();
        this.materials = new ArrayList<>();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<DAEGeometry> getGeometries() {
        return geometries;
    }

    public void setGeometries(List<DAEGeometry> geometries) {
        this.geometries = geometries;
    }

    public void addGeometry(DAEGeometry geometry) {
        this.geometries.add(geometry);
    }

    public List<DAEMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<DAEMaterial> materials) {
        this.materials = materials;
    }

    public void addMaterial(DAEMaterial material) {
        this.materials.add(material);
    }

    public DAEScene getScene() {
        return scene;
    }

    public void setScene(DAEScene scene) {
        this.scene = scene;
    }

    @Override
    public String toString() {
        return "DAEDocument{" +
                "version='" + version + '\'' +
                ", geometries=" + geometries.size() +
                ", materials=" + materials.size() +
                ", scene=" + scene +
                '}';
    }
}
