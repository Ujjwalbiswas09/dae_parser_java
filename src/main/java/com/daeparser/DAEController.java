package com.daeparser;

/**
 * Represents a controller element in COLLADA.
 * A controller can contain skin data for skeletal animation or morph data for blend shapes.
 * Currently supports skin controllers for skeletal animation.
 */
public class DAEController {
    private String id;
    private String name;
    private DAESkin skin; // Skin data for skeletal animation
    
    public DAEController() {
    }
    
    public DAEController(String id, String name) {
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
    
    public DAESkin getSkin() {
        return skin;
    }
    
    public void setSkin(DAESkin skin) {
        this.skin = skin;
    }
    
    @Override
    public String toString() {
        return "DAEController{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", skin=" + skin +
                '}';
    }
}
