package com.daeparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a skin element containing skinning/rigging information.
 * The skin binds a mesh to a skeleton for character animation.
 */
public class DAESkin {
    private String source; // Reference to the geometry being skinned
    private float[] bindShapeMatrix; // Transformation applied to the mesh before skinning (16 elements)
    private List<DAESource> sources; // Sources for joint names, bind matrices, weights, etc.
    private List<String> jointNames; // Names of the joints/bones
    private float[] inverseBindMatrices; // Inverse bind matrices for each joint (16 floats per joint)
    private List<int[]> vertexWeights; // Joint indices and weight indices per vertex
    private float[] weights; // Array of all weight values
    private int maxJointInfluences; // Maximum number of joints influencing a single vertex

    public DAESkin() {
        this.sources = new ArrayList<>();
        this.jointNames = new ArrayList<>();
        this.vertexWeights = new ArrayList<>();
        this.bindShapeMatrix = new float[16];
        // Initialize as identity matrix
        this.bindShapeMatrix[0] = 1.0f;
        this.bindShapeMatrix[5] = 1.0f;
        this.bindShapeMatrix[10] = 1.0f;
        this.bindShapeMatrix[15] = 1.0f;
        this.maxJointInfluences = 0;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public float[] getBindShapeMatrix() {
        return bindShapeMatrix;
    }

    public void setBindShapeMatrix(float[] bindShapeMatrix) {
        if (bindShapeMatrix != null && bindShapeMatrix.length == 16) {
            this.bindShapeMatrix = bindShapeMatrix;
        }
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

    public List<String> getJointNames() {
        return jointNames;
    }

    public void setJointNames(List<String> jointNames) {
        this.jointNames = jointNames;
    }

    public void addJointName(String jointName) {
        this.jointNames.add(jointName);
    }

    public float[] getInverseBindMatrices() {
        return inverseBindMatrices;
    }

    public void setInverseBindMatrices(float[] inverseBindMatrices) {
        this.inverseBindMatrices = inverseBindMatrices;
    }

    public List<int[]> getVertexWeights() {
        return vertexWeights;
    }

    public void setVertexWeights(List<int[]> vertexWeights) {
        this.vertexWeights = vertexWeights;
    }

    public void addVertexWeight(int[] vertexWeight) {
        this.vertexWeights.add(vertexWeight);
    }

    public float[] getWeights() {
        return weights;
    }

    public void setWeights(float[] weights) {
        this.weights = weights;
    }

    public int getMaxJointInfluences() {
        return maxJointInfluences;
    }

    public void setMaxJointInfluences(int maxJointInfluences) {
        this.maxJointInfluences = maxJointInfluences;
    }

    /**
     * Gets the number of joints in the skeleton.
     * 
     * @return Number of joints
     */
    public int getJointCount() {
        return jointNames.size();
    }

    /**
     * Gets the inverse bind matrix for a specific joint.
     * 
     * @param jointIndex Index of the joint
     * @return 16-element float array representing the 4x4 matrix, or null if invalid index
     */
    public float[] getJointInverseBindMatrix(int jointIndex) {
        if (inverseBindMatrices == null || jointIndex < 0 || jointIndex >= getJointCount()) {
            return null;
        }
        
        float[] matrix = new float[16];
        System.arraycopy(inverseBindMatrices, jointIndex * 16, matrix, 0, 16);
        return matrix;
    }

    /**
     * Gets the joint influences for a specific vertex.
     * Returns an array where even indices are joint indices and odd indices are weight values.
     * For example: [jointIdx0, weight0, jointIdx1, weight1, ...]
     * 
     * @param vertexIndex Index of the vertex
     * @return Array of joint index and weight pairs, or null if invalid index
     */
    public float[] getVertexJointInfluences(int vertexIndex) {
        if (vertexWeights == null || vertexIndex < 0 || vertexIndex >= vertexWeights.size() || weights == null) {
            return null;
        }
        
        int[] vertexWeightIndices = vertexWeights.get(vertexIndex);
        if (vertexWeightIndices == null) {
            return null;
        }
        
        // Convert joint indices and weight indices to actual joint index and weight value pairs
        float[] influences = new float[vertexWeightIndices.length];
        for (int i = 0; i < vertexWeightIndices.length; i += 2) {
            int jointIdx = vertexWeightIndices[i];
            int weightIdx = vertexWeightIndices[i + 1];
            
            influences[i] = jointIdx;
            influences[i + 1] = (weightIdx >= 0 && weightIdx < weights.length) ? weights[weightIdx] : 0.0f;
        }
        
        return influences;
    }

    @Override
    public String toString() {
        return "DAESkin{" +
                "source='" + source + '\'' +
                ", jointCount=" + getJointCount() +
                ", vertexCount=" + vertexWeights.size() +
                ", maxJointInfluences=" + maxJointInfluences +
                '}';
    }
}
