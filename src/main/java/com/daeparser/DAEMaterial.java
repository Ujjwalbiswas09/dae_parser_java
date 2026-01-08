package com.daeparser;

/**
 * Represents a material in a DAE file.
 */
public class DAEMaterial {
    private String id;
    private String name;
    private float[] diffuseColor;
    private float[] specularColor;
    private float[] ambientColor;
    private float shininess;
    private String textureId;

    public DAEMaterial() {
        this.diffuseColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        this.specularColor = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        this.ambientColor = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        this.shininess = 0.0f;
    }

    public DAEMaterial(String id, String name) {
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

    public float[] getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(float[] diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public float[] getSpecularColor() {
        return specularColor;
    }

    public void setSpecularColor(float[] specularColor) {
        this.specularColor = specularColor;
    }

    public float[] getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(float[] ambientColor) {
        this.ambientColor = ambientColor;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public String getTextureId() {
        return textureId;
    }

    public void setTextureId(String textureId) {
        this.textureId = textureId;
    }

    @Override
    public String toString() {
        return "DAEMaterial{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", textureId='" + textureId + '\'' +
                '}';
    }
}
