package com.daeparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an animation in a DAE file.
 * Animations define how objects change over time.
 */
public class DAEAnimation {
    private String id;
    private String name;
    private List<DAEChannel> channels;
    private List<DAESampler> samplers;
    private List<DAESource> sources;

    public DAEAnimation() {
        this.channels = new ArrayList<>();
        this.samplers = new ArrayList<>();
        this.sources = new ArrayList<>();
    }

    public DAEAnimation(String id, String name) {
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

    public List<DAEChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<DAEChannel> channels) {
        this.channels = channels;
    }

    public void addChannel(DAEChannel channel) {
        this.channels.add(channel);
    }

    public List<DAESampler> getSamplers() {
        return samplers;
    }

    public void setSamplers(List<DAESampler> samplers) {
        this.samplers = samplers;
    }

    public void addSampler(DAESampler sampler) {
        this.samplers.add(sampler);
    }

    public List<DAESource> getSources() {
        return sources;
    }

    public void setSources(List<DAESource> sources) {
        this.sources = sources;
    }

    public void addSource(DAESource source) {
        this.sources.add(source);
    }

    @Override
    public String toString() {
        return "DAEAnimation{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", channels=" + channels.size() +
                ", samplers=" + samplers.size() +
                ", sources=" + sources.size() +
                '}';
    }
}
