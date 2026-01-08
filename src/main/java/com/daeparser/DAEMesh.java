package com.daeparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a mesh element containing triangles, polygons, or other primitives.
 */
public class DAEMesh {
    private List<DAESource> sources;
    private List<float[]> vertices;
    private List<int[]> triangles;
    private int vertexCount;
    private int triangleCount;
    
    // New fields for triangulated data generation
    private List<int[]> triangleIndices; // Full indices for all attributes (position, normal, texcoord, etc.)
    private Map<String, String> inputSemantics; // Maps semantic (VERTEX, NORMAL, TEXCOORD) to source ID
    private Map<String, Integer> inputOffsets; // Maps semantic to offset in index array
    private String verticesId; // ID of the vertices element

    public DAEMesh() {
        this.sources = new ArrayList<>();
        this.vertices = new ArrayList<>();
        this.triangles = new ArrayList<>();
        this.triangleIndices = new ArrayList<>();
        this.inputSemantics = new HashMap<>();
        this.inputOffsets = new HashMap<>();
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
    
    public List<int[]> getTriangleIndices() {
        return triangleIndices;
    }
    
    public void setTriangleIndices(List<int[]> triangleIndices) {
        this.triangleIndices = triangleIndices;
    }
    
    public Map<String, String> getInputSemantics() {
        return inputSemantics;
    }
    
    public void setInputSemantics(Map<String, String> inputSemantics) {
        this.inputSemantics = inputSemantics;
    }
    
    public void addInputSemantic(String semantic, String sourceId) {
        this.inputSemantics.put(semantic, sourceId);
    }
    
    public Map<String, Integer> getInputOffsets() {
        return inputOffsets;
    }
    
    public void setInputOffsets(Map<String, Integer> inputOffsets) {
        this.inputOffsets = inputOffsets;
    }
    
    public void addInputOffset(String semantic, int offset) {
        this.inputOffsets.put(semantic, offset);
    }
    
    public String getVerticesId() {
        return verticesId;
    }
    
    public void setVerticesId(String verticesId) {
        this.verticesId = verticesId;
    }

    /**
     * Gets triangulated vertex data suitable for VBO creation.
     * Returns an array where each triangle's vertices are expanded with all attributes
     * in the format: [x, y, z, nx, ny, nz, u, v, ...] for each vertex.
     * 
     * @return Triangulated vertex data as a float array, or null if data is incomplete
     */
    public float[] getTriangulatedVertexData() {
        if (triangleIndices.isEmpty() || inputSemantics.isEmpty()) {
            return null;
        }
        
        // Find sources for each semantic
        DAESource positionSource = null;
        DAESource normalSource = null;
        DAESource texcoordSource = null;
        
        for (Map.Entry<String, String> entry : inputSemantics.entrySet()) {
            String semantic = entry.getKey();
            String sourceId = entry.getValue();
            
            // Handle VERTEX semantic by looking up the vertices element
            if (semantic.equals("VERTEX")) {
                // For VERTEX, we need to find the POSITION source via verticesId
                for (DAESource source : sources) {
                    if (source.getId() != null && sourceId != null && 
                        (source.getId().equals(sourceId) || sourceId.endsWith(source.getId()))) {
                        positionSource = source;
                        break;
                    }
                }
            } else if (semantic.equals("NORMAL")) {
                for (DAESource source : sources) {
                    if (source.getId() != null && sourceId != null && 
                        (source.getId().equals(sourceId) || sourceId.endsWith(source.getId()))) {
                        normalSource = source;
                        break;
                    }
                }
            } else if (semantic.equals("TEXCOORD")) {
                for (DAESource source : sources) {
                    if (source.getId() != null && sourceId != null && 
                        (source.getId().equals(sourceId) || sourceId.endsWith(source.getId()))) {
                        texcoordSource = source;
                        break;
                    }
                }
            }
        }
        
        if (positionSource == null) {
            return null;
        }
        
        // Calculate stride per vertex (position + normal + texcoord)
        int posStride = positionSource.getStride() > 0 ? positionSource.getStride() : 3;
        int normStride = normalSource != null && normalSource.getStride() > 0 ? normalSource.getStride() : 3;
        int texStride = texcoordSource != null && texcoordSource.getStride() > 0 ? texcoordSource.getStride() : 2;
        
        int vertexStride = posStride;
        if (normalSource != null) vertexStride += normStride;
        if (texcoordSource != null) vertexStride += texStride;
        
        // Each triangle has 3 vertices
        float[] triangulatedData = new float[triangleIndices.size() * 3 * vertexStride];
        int dataIndex = 0;
        
        float[] posData = positionSource.getDataAsArray();
        float[] normData = normalSource != null ? normalSource.getDataAsArray() : null;
        float[] texData = texcoordSource != null ? texcoordSource.getDataAsArray() : null;
        
        for (int[] triIndices : triangleIndices) {
            // Each triangle has 3 vertices
            for (int v = 0; v < 3; v++) {
                int indexOffset = v * inputOffsets.size();
                
                // Position data
                Integer posOffset = inputOffsets.get("VERTEX");
                if (posOffset != null && indexOffset + posOffset < triIndices.length) {
                    int posIndex = triIndices[indexOffset + posOffset];
                    for (int i = 0; i < posStride && posIndex * posStride + i < posData.length; i++) {
                        triangulatedData[dataIndex++] = posData[posIndex * posStride + i];
                    }
                }
                
                // Normal data
                if (normalSource != null) {
                    Integer normOffset = inputOffsets.get("NORMAL");
                    if (normOffset != null && indexOffset + normOffset < triIndices.length) {
                        int normIndex = triIndices[indexOffset + normOffset];
                        for (int i = 0; i < normStride && normIndex * normStride + i < normData.length; i++) {
                            triangulatedData[dataIndex++] = normData[normIndex * normStride + i];
                        }
                    }
                }
                
                // Texcoord data
                if (texcoordSource != null) {
                    Integer texOffset = inputOffsets.get("TEXCOORD");
                    if (texOffset != null && indexOffset + texOffset < triIndices.length) {
                        int texIndex = triIndices[indexOffset + texOffset];
                        for (int i = 0; i < texStride && texIndex * texStride + i < texData.length; i++) {
                            triangulatedData[dataIndex++] = texData[texIndex * texStride + i];
                        }
                    }
                }
            }
        }
        
        // Return only the filled portion if dataIndex < triangulatedData.length
        if (dataIndex < triangulatedData.length) {
            float[] result = new float[dataIndex];
            System.arraycopy(triangulatedData, 0, result, 0, dataIndex);
            return result;
        }
        
        return triangulatedData;
    }
    
    /**
     * Gets triangulated positions only.
     * Returns positions for each vertex of each triangle: [x, y, z, x, y, z, ...]
     * 
     * @return Triangulated position data, or null if not available
     */
    public float[] getTriangulatedPositions() {
        if (triangleIndices.isEmpty() || inputSemantics.isEmpty()) {
            return null;
        }
        
        // Find position source
        DAESource positionSource = null;
        for (DAESource source : sources) {
            String sourceId = inputSemantics.get("VERTEX");
            if (source.getId() != null && sourceId != null && 
                (source.getId().equals(sourceId) || sourceId.endsWith(source.getId()))) {
                positionSource = source;
                break;
            }
        }
        
        if (positionSource == null) {
            return null;
        }
        
        int posStride = positionSource.getStride() > 0 ? positionSource.getStride() : 3;
        float[] posData = positionSource.getDataAsArray();
        float[] result = new float[triangleIndices.size() * 3 * posStride];
        int dataIndex = 0;
        
        Integer posOffset = inputOffsets.get("VERTEX");
        if (posOffset == null) {
            return null;
        }
        
        for (int[] triIndices : triangleIndices) {
            for (int v = 0; v < 3; v++) {
                int indexOffset = v * inputOffsets.size();
                if (indexOffset + posOffset < triIndices.length) {
                    int posIndex = triIndices[indexOffset + posOffset];
                    for (int i = 0; i < posStride && posIndex * posStride + i < posData.length; i++) {
                        result[dataIndex++] = posData[posIndex * posStride + i];
                    }
                }
            }
        }
        
        if (dataIndex < result.length) {
            float[] trimmed = new float[dataIndex];
            System.arraycopy(result, 0, trimmed, 0, dataIndex);
            return trimmed;
        }
        
        return result;
    }
    
    /**
     * Gets triangulated normals only.
     * Returns normals for each vertex of each triangle: [nx, ny, nz, nx, ny, nz, ...]
     * 
     * @return Triangulated normal data, or null if not available
     */
    public float[] getTriangulatedNormals() {
        if (triangleIndices.isEmpty() || inputSemantics.isEmpty()) {
            return null;
        }
        
        DAESource normalSource = null;
        String sourceId = inputSemantics.get("NORMAL");
        if (sourceId != null) {
            for (DAESource source : sources) {
                if (source.getId() != null && 
                    (source.getId().equals(sourceId) || sourceId.endsWith(source.getId()))) {
                    normalSource = source;
                    break;
                }
            }
        }
        
        if (normalSource == null) {
            return null;
        }
        
        int normStride = normalSource.getStride() > 0 ? normalSource.getStride() : 3;
        float[] normData = normalSource.getDataAsArray();
        float[] result = new float[triangleIndices.size() * 3 * normStride];
        int dataIndex = 0;
        
        Integer normOffset = inputOffsets.get("NORMAL");
        if (normOffset == null) {
            return null;
        }
        
        for (int[] triIndices : triangleIndices) {
            for (int v = 0; v < 3; v++) {
                int indexOffset = v * inputOffsets.size();
                if (indexOffset + normOffset < triIndices.length) {
                    int normIndex = triIndices[indexOffset + normOffset];
                    for (int i = 0; i < normStride && normIndex * normStride + i < normData.length; i++) {
                        result[dataIndex++] = normData[normIndex * normStride + i];
                    }
                }
            }
        }
        
        if (dataIndex < result.length) {
            float[] trimmed = new float[dataIndex];
            System.arraycopy(result, 0, trimmed, 0, dataIndex);
            return trimmed;
        }
        
        return result;
    }
    
    /**
     * Gets triangulated texture coordinates only.
     * Returns texcoords for each vertex of each triangle: [u, v, u, v, ...]
     * 
     * @return Triangulated texcoord data, or null if not available
     */
    public float[] getTriangulatedTexCoords() {
        if (triangleIndices.isEmpty() || inputSemantics.isEmpty()) {
            return null;
        }
        
        DAESource texcoordSource = null;
        String sourceId = inputSemantics.get("TEXCOORD");
        if (sourceId != null) {
            for (DAESource source : sources) {
                if (source.getId() != null && 
                    (source.getId().equals(sourceId) || sourceId.endsWith(source.getId()))) {
                    texcoordSource = source;
                    break;
                }
            }
        }
        
        if (texcoordSource == null) {
            return null;
        }
        
        int texStride = texcoordSource.getStride() > 0 ? texcoordSource.getStride() : 2;
        float[] texData = texcoordSource.getDataAsArray();
        float[] result = new float[triangleIndices.size() * 3 * texStride];
        int dataIndex = 0;
        
        Integer texOffset = inputOffsets.get("TEXCOORD");
        if (texOffset == null) {
            return null;
        }
        
        for (int[] triIndices : triangleIndices) {
            for (int v = 0; v < 3; v++) {
                int indexOffset = v * inputOffsets.size();
                if (indexOffset + texOffset < triIndices.length) {
                    int texIndex = triIndices[indexOffset + texOffset];
                    for (int i = 0; i < texStride && texIndex * texStride + i < texData.length; i++) {
                        result[dataIndex++] = texData[texIndex * texStride + i];
                    }
                }
            }
        }
        
        if (dataIndex < result.length) {
            float[] trimmed = new float[dataIndex];
            System.arraycopy(result, 0, trimmed, 0, dataIndex);
            return trimmed;
        }
        
        return result;
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
