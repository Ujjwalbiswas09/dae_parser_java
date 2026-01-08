package com.daeparser;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an animation sampler in a DAE file.
 * Samplers define how to interpolate animation data.
 */
public class DAESampler {
    private String id;
    private Map<String, String> inputs;

    public DAESampler() {
        this.inputs = new HashMap<>();
    }

    public DAESampler(String id) {
        this();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, String> inputs) {
        this.inputs = inputs;
    }

    public void addInput(String semantic, String source) {
        this.inputs.put(semantic, source);
    }

    public String getInput(String semantic) {
        return inputs.get(semantic);
    }

    @Override
    public String toString() {
        return "DAESampler{" +
                "id='" + id + '\'' +
                ", inputs=" + inputs +
                '}';
    }
}
