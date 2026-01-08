package com.daeparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a scene in a DAE file containing visual scene nodes.
 */
public class DAEScene {
    private String id;
    private String name;
    private List<DAENode> nodes;

    public DAEScene() {
        this.nodes = new ArrayList<>();
    }

    public DAEScene(String id, String name) {
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

    public List<DAENode> getNodes() {
        return nodes;
    }

    public void setNodes(List<DAENode> nodes) {
        this.nodes = nodes;
    }

    public void addNode(DAENode node) {
        this.nodes.add(node);
    }

    @Override
    public String toString() {
        return "DAEScene{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", nodes=" + nodes.size() +
                '}';
    }
}
