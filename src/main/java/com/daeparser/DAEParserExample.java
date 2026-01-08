package com.daeparser;

import java.io.File;

/**
 * Example usage of the DAE parser.
 */
public class DAEParserExample {

    public static void main(String[] args) {
        try {
            // Example 1: Parse from file path
            System.out.println("=== Example 1: Parse DAE file ===");
            
            // In a real scenario, you would provide the path to your DAE file
            // DAEDocument doc = DAEParser.parse("path/to/your/model.dae");
            
            // For demonstration, we'll show how to use it
            String filePath = "model.dae";
            File file = new File(filePath);
            
            if (file.exists()) {
                DAEDocument doc = DAEParser.parse(filePath);
                
                System.out.println("COLLADA Version: " + doc.getVersion());
                System.out.println("Number of Geometries: " + doc.getGeometries().size());
                System.out.println("Number of Materials: " + doc.getMaterials().size());
                System.out.println("Number of Animations: " + doc.getAnimations().size());
                
                // Iterate through geometries
                for (DAEGeometry geometry : doc.getGeometries()) {
                    System.out.println("\nGeometry: " + geometry.getName());
                    System.out.println("  ID: " + geometry.getId());
                    
                    DAEMesh mesh = geometry.getMesh();
                    if (mesh != null) {
                        System.out.println("  Sources: " + mesh.getSources().size());
                        System.out.println("  Triangle Count: " + mesh.getTriangleCount());
                        
                        // Access vertex data
                        for (DAESource source : mesh.getSources()) {
                            System.out.println("  Source: " + source.getId());
                            System.out.println("    Data points: " + source.getData().size());
                            System.out.println("    Stride: " + source.getStride());
                        }
                    }
                }
                
                // Access scene information
                if (doc.getScene() != null) {
                    System.out.println("\nScene: " + doc.getScene().getName());
                    System.out.println("Root Nodes: " + doc.getScene().getNodes().size());
                    
                    for (DAENode node : doc.getScene().getNodes()) {
                        System.out.println("  Node: " + node.getName());
                        if (node.getGeometryRef() != null) {
                            System.out.println("    References Geometry: " + node.getGeometryRef());
                        }
                    }
                }
                
                // Access animations
                for (DAEAnimation animation : doc.getAnimations()) {
                    System.out.println("\nAnimation: " + animation.getName());
                    System.out.println("  ID: " + animation.getId());
                    System.out.println("  Channels: " + animation.getChannels().size());
                    System.out.println("  Samplers: " + animation.getSamplers().size());
                    System.out.println("  Sources: " + animation.getSources().size());
                    
                    // Show channel targets
                    for (DAEChannel channel : animation.getChannels()) {
                        System.out.println("  Channel Target: " + channel.getTarget());
                        System.out.println("    Sampler: " + channel.getSource());
                    }
                    
                    // Show sampler inputs
                    for (DAESampler sampler : animation.getSamplers()) {
                        System.out.println("  Sampler: " + sampler.getId());
                        System.out.println("    Inputs: " + sampler.getInputs().keySet());
                    }
                }
                
                // Access controllers (skeleton/skinning)
                for (DAEController controller : doc.getControllers()) {
                    System.out.println("\nController: " + controller.getName());
                    System.out.println("  ID: " + controller.getId());
                    
                    DAESkin skin = controller.getSkin();
                    if (skin != null) {
                        System.out.println("  Skin Source: " + skin.getSource());
                        System.out.println("  Joint Count: " + skin.getJointCount());
                        System.out.println("  Vertex Count: " + skin.getVertexWeights().size());
                        System.out.println("  Max Joint Influences: " + skin.getMaxJointInfluences());
                        
                        // Show joint names
                        System.out.println("  Joints: " + skin.getJointNames());
                        
                        // Example: Show first vertex influences
                        if (skin.getVertexWeights().size() > 0) {
                            float[] influences = skin.getVertexJointInfluences(0);
                            System.out.print("  First vertex influences: ");
                            for (int i = 0; i < influences.length; i += 2) {
                                System.out.print("Joint " + (int)influences[i] + 
                                               " (weight " + influences[i+1] + ") ");
                            }
                            System.out.println();
                        }
                    }
                }
            } else {
                System.out.println("File not found. Please provide a valid DAE file path.");
                demonstrateAPI();
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing DAE file: " + e.getMessage());
            e.printStackTrace();
            demonstrateAPI();
        }
    }
    
    private static void demonstrateAPI() {
        System.out.println("\n=== DAE Parser API Demo ===");
        System.out.println("Available parsing methods:");
        System.out.println("  1. DAEParser.parse(String filePath)");
        System.out.println("  2. DAEParser.parse(File file)");
        System.out.println("  3. DAEParser.parse(InputStream inputStream)");
        
        System.out.println("\nAvailable classes:");
        System.out.println("  - DAEDocument: Root document containing all elements");
        System.out.println("  - DAEGeometry: 3D geometry definition");
        System.out.println("  - DAEMesh: Mesh data with vertices and triangles");
        System.out.println("  - DAESource: Data sources (positions, normals, UVs, keyframes, joint names)");
        System.out.println("  - DAEMaterial: Material properties");
        System.out.println("  - DAEController: Controller for skin/morph data");
        System.out.println("  - DAESkin: Skin data for skeletal animation");
        System.out.println("  - DAEAnimation: Animation with channels and samplers");
        System.out.println("  - DAEChannel: Animation channel targeting node properties");
        System.out.println("  - DAESampler: Animation sampler with interpolation data");
        System.out.println("  - DAEScene: Scene hierarchy");
        System.out.println("  - DAENode: Scene node with transformation and skeleton links");
        
        System.out.println("\nTypical usage:");
        System.out.println("  DAEDocument doc = DAEParser.parse(\"model.dae\");");
        System.out.println("  for (DAEGeometry geometry : doc.getGeometries()) {");
        System.out.println("    DAEMesh mesh = geometry.getMesh();");
        System.out.println("    // Access vertex data, triangles, etc.");
        System.out.println("  }");
        
        System.out.println("\nTriangulated data for VBO creation:");
        System.out.println("  DAEMesh mesh = geometry.getMesh();");
        System.out.println("  ");
        System.out.println("  // Get interleaved vertex data (pos + normal + texcoord)");
        System.out.println("  float[] vboData = mesh.getTriangulatedVertexData();");
        System.out.println("  ");
        System.out.println("  // Or get individual attributes:");
        System.out.println("  float[] positions = mesh.getTriangulatedPositions();");
        System.out.println("  float[] normals = mesh.getTriangulatedNormals();");
        System.out.println("  float[] texCoords = mesh.getTriangulatedTexCoords();");
        System.out.println("  ");
        System.out.println("  // Use these arrays directly with OpenGL/Vulkan/DirectX:");
        System.out.println("  // glBufferData(GL_ARRAY_BUFFER, vboData, GL_STATIC_DRAW);");
        
        System.out.println("\nSkeleton and Skinning:");
        System.out.println("  DAEController controller = doc.getControllers().get(0);");
        System.out.println("  DAESkin skin = controller.getSkin();");
        System.out.println("  ");
        System.out.println("  // Get joint names and matrices");
        System.out.println("  List<String> jointNames = skin.getJointNames();");
        System.out.println("  float[] inverseBindMatrix = skin.getJointInverseBindMatrix(0);");
        System.out.println("  ");
        System.out.println("  // Get vertex skinning weights");
        System.out.println("  float[] influences = skin.getVertexJointInfluences(vertexIndex);");
        System.out.println("  // influences = [jointIdx0, weight0, jointIdx1, weight1, ...]");
        System.out.println("  ");
        System.out.println("  // Traverse skeleton hierarchy");
        System.out.println("  for (DAENode node : scene.getNodes()) {");
        System.out.println("    if (node.isJoint()) {");
        System.out.println("      // Process joint transformation");
        System.out.println("    }");
        System.out.println("  }");
    }
}
