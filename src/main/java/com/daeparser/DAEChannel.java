package com.daeparser;

/**
 * Represents an animation channel in a DAE file.
 * Channels connect samplers to target parameters.
 */
public class DAEChannel {
    private String source;
    private String target;

    public DAEChannel() {
    }

    public DAEChannel(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "DAEChannel{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}
