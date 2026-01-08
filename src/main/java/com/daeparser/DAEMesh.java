package com.daeparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a mesh element containing triangles, polygons, or other primitives.
 */
public class DAEMesh {
    private List<DAESource> sources;
    private List<float[]> vertices;
    private List<int[]> triangles;
    private int vertexCount;
    private int triangleCount;

    public DAEMesh() {
        this.sources = new ArrayList<>();
        this.vertices = new ArrayList<>();
        this.triangles = new ArrayList<>();
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

    public List<float[]> getVertices() {
        return vertices;
    }

    public void setVertices(List<float[]> vertices) {
        this.vertices = vertices;
    }

    public void addVertex(float[] vertex) {
        this.vertices.add(vertex);
    }

    public List<int[]> getTriangles() {
        return triangles;
    }

    public void setTriangles(List<int[]> triangles) {
        this.triangles = triangles;
    }

    public void addTriangle(int[] triangle) {
        this.triangles.add(triangle);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public int getTriangleCount() {
        return triangleCount;
    }

    public void setTriangleCount(int triangleCount) {
        this.triangleCount = triangleCount;
    }

    @Override
    public String toString() {
        return "DAEMesh{" +
                "sources=" + sources.size() +
                ", vertices=" + vertices.size() +
                ", triangles=" + triangles.size() +
                ", vertexCount=" + vertexCount +
                ", triangleCount=" + triangleCount +
                '}';
    }
}
