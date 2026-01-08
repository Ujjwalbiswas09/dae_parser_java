package com.daeparser;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.List;

/**
 * Unit tests for the DAE parser.
 */
public class DAEParserTest {

    @Test
    public void testParseSimpleTriangle() throws Exception {
        InputStream is = getClass().getResourceAsStream("/triangle.dae");
        assertNotNull("Test file not found", is);

        DAEDocument doc = DAEParser.parse(is);
        assertNotNull("Document should not be null", doc);
        assertEquals("Version should be 1.4.1", "1.4.1", doc.getVersion());

        // Check geometries
        List<DAEGeometry> geometries = doc.getGeometries();
        assertNotNull("Geometries should not be null", geometries);
        assertEquals("Should have 1 geometry", 1, geometries.size());

        DAEGeometry geometry = geometries.get(0);
        assertEquals("Geometry ID should be Triangle-mesh", "Triangle-mesh", geometry.getId());
        assertEquals("Geometry name should be Triangle", "Triangle", geometry.getName());

        // Check mesh
        DAEMesh mesh = geometry.getMesh();
        assertNotNull("Mesh should not be null", mesh);

        // Check sources
        List<DAESource> sources = mesh.getSources();
        assertNotNull("Sources should not be null", sources);
        assertTrue("Should have at least 1 source", sources.size() >= 1);

        DAESource positionSource = sources.get(0);
        assertEquals("Position source should have 9 values", 9, positionSource.getData().size());
        assertEquals("Stride should be 3", 3, positionSource.getStride());

        // Check triangles
        assertEquals("Should have 1 triangle", 1, mesh.getTriangleCount());
    }

    @Test
    public void testParseCube() throws Exception {
        InputStream is = getClass().getResourceAsStream("/cube.dae");
        assertNotNull("Test file not found", is);

        DAEDocument doc = DAEParser.parse(is);
        assertNotNull("Document should not be null", doc);
        assertEquals("Version should be 1.4.1", "1.4.1", doc.getVersion());

        // Check geometries
        List<DAEGeometry> geometries = doc.getGeometries();
        assertNotNull("Geometries should not be null", geometries);
        assertEquals("Should have 1 geometry", 1, geometries.size());

        DAEGeometry geometry = geometries.get(0);
        assertEquals("Geometry ID should be Cube-mesh", "Cube-mesh", geometry.getId());
        assertEquals("Geometry name should be Cube", "Cube", geometry.getName());

        // Check mesh
        DAEMesh mesh = geometry.getMesh();
        assertNotNull("Mesh should not be null", mesh);

        // Check sources
        List<DAESource> sources = mesh.getSources();
        assertNotNull("Sources should not be null", sources);
        assertTrue("Should have at least 2 sources", sources.size() >= 2);

        // Check position source
        DAESource positionSource = sources.get(0);
        assertEquals("Position source should have 24 values", 24, positionSource.getData().size());
        assertEquals("Stride should be 3", 3, positionSource.getStride());

        // Check normal source
        DAESource normalSource = sources.get(1);
        assertEquals("Normal source should have 18 values", 18, normalSource.getData().size());

        // Check triangles
        assertEquals("Should have 12 triangles", 12, mesh.getTriangleCount());

        // Check materials
        List<DAEMaterial> materials = doc.getMaterials();
        assertNotNull("Materials should not be null", materials);
        assertEquals("Should have 1 material", 1, materials.size());

        DAEMaterial material = materials.get(0);
        assertEquals("Material ID should be Material-material", "Material-material", material.getId());
        assertEquals("Material name should be Material", "Material", material.getName());

        // Check scene
        DAEScene scene = doc.getScene();
        assertNotNull("Scene should not be null", scene);
        assertEquals("Scene ID should be Scene", "Scene", scene.getId());
        assertEquals("Scene name should be Scene", "Scene", scene.getName());

        List<DAENode> nodes = scene.getNodes();
        assertNotNull("Nodes should not be null", nodes);
        assertEquals("Should have 1 node", 1, nodes.size());

        DAENode node = nodes.get(0);
        assertEquals("Node ID should be Cube", "Cube", node.getId());
        assertEquals("Node name should be Cube", "Cube", node.getName());
        assertEquals("Node should reference Cube-mesh geometry", "Cube-mesh", node.getGeometryRef());
    }

    @Test
    public void testSourceDataAccess() throws Exception {
        InputStream is = getClass().getResourceAsStream("/triangle.dae");
        DAEDocument doc = DAEParser.parse(is);

        DAEGeometry geometry = doc.getGeometries().get(0);
        DAEMesh mesh = geometry.getMesh();
        DAESource source = mesh.getSources().get(0);

        // Test getDataAsArray method
        float[] dataArray = source.getDataAsArray();
        assertNotNull("Data array should not be null", dataArray);
        assertEquals("Data array should have 9 elements", 9, dataArray.length);

        // Check first vertex (0.0, 1.0, 0.0)
        assertEquals("First vertex X should be 0.0", 0.0f, dataArray[0], 0.001f);
        assertEquals("First vertex Y should be 1.0", 1.0f, dataArray[1], 0.001f);
        assertEquals("First vertex Z should be 0.0", 0.0f, dataArray[2], 0.001f);
    }

    @Test
    public void testMaterialDefaults() {
        DAEMaterial material = new DAEMaterial("test-id", "test-material");
        
        assertNotNull("Diffuse color should not be null", material.getDiffuseColor());
        assertEquals("Diffuse color should have 4 components", 4, material.getDiffuseColor().length);
        
        assertNotNull("Specular color should not be null", material.getSpecularColor());
        assertEquals("Specular color should have 4 components", 4, material.getSpecularColor().length);
        
        assertNotNull("Ambient color should not be null", material.getAmbientColor());
        assertEquals("Ambient color should have 4 components", 4, material.getAmbientColor().length);
        
        assertEquals("Shininess should be 0.0", 0.0f, material.getShininess(), 0.001f);
    }

    @Test
    public void testNodeTransformation() {
        DAENode node = new DAENode("test-node", "Test Node");
        
        float[] transformation = node.getTransformation();
        assertNotNull("Transformation should not be null", transformation);
        assertEquals("Transformation should have 16 elements", 16, transformation.length);
        
        // Check for identity matrix
        assertEquals("Element [0,0] should be 1", 1.0f, transformation[0], 0.001f);
        assertEquals("Element [1,1] should be 1", 1.0f, transformation[5], 0.001f);
        assertEquals("Element [2,2] should be 1", 1.0f, transformation[10], 0.001f);
        assertEquals("Element [3,3] should be 1", 1.0f, transformation[15], 0.001f);
    }

    @Test
    public void testSceneHierarchy() throws Exception {
        InputStream is = getClass().getResourceAsStream("/cube.dae");
        DAEDocument doc = DAEParser.parse(is);

        DAEScene scene = doc.getScene();
        assertNotNull("Scene should not be null", scene);

        List<DAENode> nodes = scene.getNodes();
        assertEquals("Scene should have 1 root node", 1, nodes.size());

        DAENode rootNode = nodes.get(0);
        assertNotNull("Root node should not be null", rootNode);
        assertNotNull("Root node children list should not be null", rootNode.getChildren());
    }
}
