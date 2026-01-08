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
        System.out.println("  - DAESource: Data sources (positions, normals, UVs)");
        System.out.println("  - DAEMaterial: Material properties");
        System.out.println("  - DAEScene: Scene hierarchy");
        System.out.println("  - DAENode: Scene node with transformation");
        
        System.out.println("\nTypical usage:");
        System.out.println("  DAEDocument doc = DAEParser.parse(\"model.dae\");");
        System.out.println("  for (DAEGeometry geometry : doc.getGeometries()) {");
        System.out.println("    DAEMesh mesh = geometry.getMesh();");
        System.out.println("    // Access vertex data, triangles, etc.");
        System.out.println("  }");
    }
}
