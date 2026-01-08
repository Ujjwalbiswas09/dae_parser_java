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

    @Test
    public void testParseAnimation() throws Exception {
        InputStream is = getClass().getResourceAsStream("/animated_cube.dae");
        assertNotNull("Test file not found", is);

        DAEDocument doc = DAEParser.parse(is);
        assertNotNull("Document should not be null", doc);

        // Check animations
        List<DAEAnimation> animations = doc.getAnimations();
        assertNotNull("Animations should not be null", animations);
        assertEquals("Should have 1 animation", 1, animations.size());

        DAEAnimation animation = animations.get(0);
        assertEquals("Animation ID should be Cube_location_X", "Cube_location_X", animation.getId());
        assertEquals("Animation name should be Cube_location_X", "Cube_location_X", animation.getName());

        // Check sources
        List<DAESource> sources = animation.getSources();
        assertNotNull("Animation sources should not be null", sources);
        assertTrue("Should have at least 2 sources (input/output)", sources.size() >= 2);

        // Check samplers
        List<DAESampler> samplers = animation.getSamplers();
        assertNotNull("Samplers should not be null", samplers);
        assertEquals("Should have 1 sampler", 1, samplers.size());

        DAESampler sampler = samplers.get(0);
        assertEquals("Sampler ID should be Cube_location_X-sampler", "Cube_location_X-sampler", sampler.getId());
        assertNotNull("Sampler inputs should not be null", sampler.getInputs());
        assertTrue("Sampler should have INPUT semantic", sampler.getInputs().containsKey("INPUT"));
        assertTrue("Sampler should have OUTPUT semantic", sampler.getInputs().containsKey("OUTPUT"));

        // Check channels
        List<DAEChannel> channels = animation.getChannels();
        assertNotNull("Channels should not be null", channels);
        assertEquals("Should have 1 channel", 1, channels.size());

        DAEChannel channel = channels.get(0);
        assertEquals("Channel source should be Cube_location_X-sampler", "Cube_location_X-sampler", channel.getSource());
        assertEquals("Channel target should be Cube/location.X", "Cube/location.X", channel.getTarget());
    }

    @Test
    public void testAnimationSourceData() throws Exception {
        InputStream is = getClass().getResourceAsStream("/animated_cube.dae");
        DAEDocument doc = DAEParser.parse(is);

        DAEAnimation animation = doc.getAnimations().get(0);
        List<DAESource> sources = animation.getSources();

        // Find the input source (time values)
        DAESource inputSource = null;
        for (DAESource source : sources) {
            if (source.getId().contains("input")) {
                inputSource = source;
                break;
            }
        }

        assertNotNull("Input source should be found", inputSource);
        assertEquals("Input source should have 3 values", 3, inputSource.getData().size());

        float[] timeData = inputSource.getDataAsArray();
        assertEquals("First time should be 0.0", 0.0f, timeData[0], 0.001f);
        assertEquals("Second time should be 1.0", 1.0f, timeData[1], 0.001f);
        assertEquals("Third time should be 2.0", 2.0f, timeData[2], 0.001f);
    }

    @Test
    public void testTriangulatedPositions() throws Exception {
        InputStream is = getClass().getResourceAsStream("/triangle.dae");
        DAEDocument doc = DAEParser.parse(is);

        DAEGeometry geometry = doc.getGeometries().get(0);
        DAEMesh mesh = geometry.getMesh();

        float[] triangulatedPositions = mesh.getTriangulatedPositions();
        assertNotNull("Triangulated positions should not be null", triangulatedPositions);
        
        // Triangle has 3 vertices, each with 3 components (x, y, z)
        assertEquals("Should have 9 values (3 vertices * 3 components)", 9, triangulatedPositions.length);
        
        // First vertex (0.0, 1.0, 0.0)
        assertEquals("First vertex X", 0.0f, triangulatedPositions[0], 0.001f);
        assertEquals("First vertex Y", 1.0f, triangulatedPositions[1], 0.001f);
        assertEquals("First vertex Z", 0.0f, triangulatedPositions[2], 0.001f);
        
        // Second vertex (-1.0, -1.0, 0.0)
        assertEquals("Second vertex X", -1.0f, triangulatedPositions[3], 0.001f);
        assertEquals("Second vertex Y", -1.0f, triangulatedPositions[4], 0.001f);
        assertEquals("Second vertex Z", 0.0f, triangulatedPositions[5], 0.001f);
        
        // Third vertex (1.0, -1.0, 0.0)
        assertEquals("Third vertex X", 1.0f, triangulatedPositions[6], 0.001f);
        assertEquals("Third vertex Y", -1.0f, triangulatedPositions[7], 0.001f);
        assertEquals("Third vertex Z", 0.0f, triangulatedPositions[8], 0.001f);
    }

    @Test
    public void testTriangulatedVertexDataWithNormals() throws Exception {
        InputStream is = getClass().getResourceAsStream("/cube.dae");
        DAEDocument doc = DAEParser.parse(is);

        DAEGeometry geometry = doc.getGeometries().get(0);
        DAEMesh mesh = geometry.getMesh();

        float[] triangulatedData = mesh.getTriangulatedVertexData();
        assertNotNull("Triangulated data should not be null", triangulatedData);
        
        // Cube has 12 triangles, each with 3 vertices, each vertex has position (3) + normal (3) = 6 components
        assertEquals("Should have 216 values (12 triangles * 3 vertices * 6 components)", 
                     216, triangulatedData.length);
        
        // Verify first vertex has both position and normal data
        // Position should be (-1.0, -1.0, -1.0) based on cube.dae
        assertEquals("First vertex position X", -1.0f, triangulatedData[0], 0.001f);
        assertEquals("First vertex position Y", -1.0f, triangulatedData[1], 0.001f);
        assertEquals("First vertex position Z", -1.0f, triangulatedData[2], 0.001f);
        
        // Normal should follow position (0.0, 0.0, -1.0) based on cube.dae first triangle
        assertEquals("First vertex normal X", 0.0f, triangulatedData[3], 0.001f);
        assertEquals("First vertex normal Y", 0.0f, triangulatedData[4], 0.001f);
        assertEquals("First vertex normal Z", -1.0f, triangulatedData[5], 0.001f);
    }

    @Test
    public void testTriangulatedNormals() throws Exception {
        InputStream is = getClass().getResourceAsStream("/cube.dae");
        DAEDocument doc = DAEParser.parse(is);

        DAEGeometry geometry = doc.getGeometries().get(0);
        DAEMesh mesh = geometry.getMesh();

        float[] triangulatedNormals = mesh.getTriangulatedNormals();
        assertNotNull("Triangulated normals should not be null", triangulatedNormals);
        
        // Cube has 12 triangles, each with 3 vertices, each normal has 3 components
        assertEquals("Should have 108 values (12 triangles * 3 vertices * 3 components)", 
                     108, triangulatedNormals.length);
        
        // First triangle should have normal (0.0, 0.0, -1.0)
        for (int i = 0; i < 3; i++) {  // 3 vertices of first triangle
            assertEquals("Normal X for vertex " + i, 0.0f, triangulatedNormals[i * 3], 0.001f);
            assertEquals("Normal Y for vertex " + i, 0.0f, triangulatedNormals[i * 3 + 1], 0.001f);
            assertEquals("Normal Z for vertex " + i, -1.0f, triangulatedNormals[i * 3 + 2], 0.001f);
        }
    }

    @Test
    public void testParseSkinnedMesh() throws Exception {
        InputStream is = getClass().getResourceAsStream("/skinned_cylinder.dae");
        assertNotNull("Test file not found", is);

        DAEDocument doc = DAEParser.parse(is);
        assertNotNull("Document should not be null", doc);

        // Check controllers
        List<DAEController> controllers = doc.getControllers();
        assertNotNull("Controllers should not be null", controllers);
        assertEquals("Should have 1 controller", 1, controllers.size());

        DAEController controller = controllers.get(0);
        assertEquals("Controller ID should be Cylinder-skin", "Cylinder-skin", controller.getId());
        assertEquals("Controller name should be Cylinder", "Cylinder", controller.getName());

        // Check skin
        DAESkin skin = controller.getSkin();
        assertNotNull("Skin should not be null", skin);
        assertEquals("Skin source should be Cylinder-mesh", "Cylinder-mesh", skin.getSource());

        // Check bind shape matrix
        float[] bindShapeMatrix = skin.getBindShapeMatrix();
        assertNotNull("Bind shape matrix should not be null", bindShapeMatrix);
        assertEquals("Bind shape matrix should have 16 elements", 16, bindShapeMatrix.length);
        assertEquals("Bind shape matrix [0,0] should be 1.0", 1.0f, bindShapeMatrix[0], 0.001f);
        assertEquals("Bind shape matrix [3,3] should be 1.0", 1.0f, bindShapeMatrix[15], 0.001f);

        // Check joint names
        List<String> jointNames = skin.getJointNames();
        assertNotNull("Joint names should not be null", jointNames);
        assertEquals("Should have 3 joints", 3, jointNames.size());
        assertEquals("First joint should be Bone1", "Bone1", jointNames.get(0));
        assertEquals("Second joint should be Bone2", "Bone2", jointNames.get(1));
        assertEquals("Third joint should be Bone3", "Bone3", jointNames.get(2));

        // Check inverse bind matrices
        float[] inverseBindMatrices = skin.getInverseBindMatrices();
        assertNotNull("Inverse bind matrices should not be null", inverseBindMatrices);
        assertEquals("Should have 48 values (3 joints * 16 floats)", 48, inverseBindMatrices.length);

        // Check joint count
        assertEquals("Joint count should be 3", 3, skin.getJointCount());

        // Check vertex weights
        List<int[]> vertexWeights = skin.getVertexWeights();
        assertNotNull("Vertex weights should not be null", vertexWeights);
        assertEquals("Should have 4 vertex weights", 4, vertexWeights.size());

        // Check weights array
        float[] weights = skin.getWeights();
        assertNotNull("Weights array should not be null", weights);
        assertEquals("Should have 8 weights", 8, weights.length);

        // Check max joint influences
        assertEquals("Max joint influences should be 2", 2, skin.getMaxJointInfluences());
    }

    @Test
    public void testSkinJointInfluences() throws Exception {
        InputStream is = getClass().getResourceAsStream("/skinned_cylinder.dae");
        DAEDocument doc = DAEParser.parse(is);

        DAEController controller = doc.getControllers().get(0);
        DAESkin skin = controller.getSkin();

        // Test first vertex (should have 1 influence)
        float[] influences0 = skin.getVertexJointInfluences(0);
        assertNotNull("Vertex 0 influences should not be null", influences0);
        assertEquals("Vertex 0 should have 2 values (1 joint influence)", 2, influences0.length);
        assertEquals("Vertex 0 joint index should be 0", 0.0f, influences0[0], 0.001f);
        assertEquals("Vertex 0 weight should be 1.0", 1.0f, influences0[1], 0.001f);

        // Test second vertex (should have 2 influences)
        float[] influences1 = skin.getVertexJointInfluences(1);
        assertNotNull("Vertex 1 influences should not be null", influences1);
        assertEquals("Vertex 1 should have 4 values (2 joint influences)", 4, influences1.length);
        assertEquals("Vertex 1 first joint index should be 0", 0.0f, influences1[0], 0.001f);
        assertEquals("Vertex 1 first weight should be 0.5", 0.5f, influences1[1], 0.001f);
        assertEquals("Vertex 1 second joint index should be 1", 1.0f, influences1[2], 0.001f);
        assertEquals("Vertex 1 second weight should be 0.5", 0.5f, influences1[3], 0.001f);
    }

    @Test
    public void testSkinJointInverseBindMatrix() throws Exception {
        InputStream is = getClass().getResourceAsStream("/skinned_cylinder.dae");
        DAEDocument doc = DAEParser.parse(is);

        DAEController controller = doc.getControllers().get(0);
        DAESkin skin = controller.getSkin();

        // Test first joint's inverse bind matrix
        float[] matrix0 = skin.getJointInverseBindMatrix(0);
        assertNotNull("Joint 0 matrix should not be null", matrix0);
        assertEquals("Joint 0 matrix should have 16 elements", 16, matrix0.length);
        assertEquals("Joint 0 matrix [0,0] should be 1.0", 1.0f, matrix0[0], 0.001f);
        assertEquals("Joint 0 matrix [3,3] should be 1.0", 1.0f, matrix0[15], 0.001f);

        // Test second joint's inverse bind matrix
        float[] matrix1 = skin.getJointInverseBindMatrix(1);
        assertNotNull("Joint 1 matrix should not be null", matrix1);
        assertEquals("Joint 1 matrix should have 16 elements", 16, matrix1.length);
        assertEquals("Joint 1 matrix [1,3] should be -1.0", -1.0f, matrix1[7], 0.001f);

        // Test invalid index
        float[] matrixInvalid = skin.getJointInverseBindMatrix(99);
        assertNull("Invalid joint index should return null", matrixInvalid);
    }

    @Test
    public void testNodeWithController() throws Exception {
        InputStream is = getClass().getResourceAsStream("/skinned_cylinder.dae");
        DAEDocument doc = DAEParser.parse(is);

        DAEScene scene = doc.getScene();
        assertNotNull("Scene should not be null", scene);

        List<DAENode> nodes = scene.getNodes();
        assertNotNull("Nodes should not be null", nodes);
        assertTrue("Should have at least 2 nodes", nodes.size() >= 2);

        // Find the node with instance_controller
        DAENode controllerNode = null;
        for (DAENode node : nodes) {
            if (node.getControllerRef() != null) {
                controllerNode = node;
                break;
            }
        }

        assertNotNull("Should find node with controller", controllerNode);
        assertEquals("Controller ref should be Cylinder-skin", "Cylinder-skin", controllerNode.getControllerRef());

        // Check skeleton references
        List<String> skeletonRefs = controllerNode.getSkeletonRefs();
        assertNotNull("Skeleton refs should not be null", skeletonRefs);
        assertEquals("Should have 1 skeleton reference", 1, skeletonRefs.size());
        assertEquals("Skeleton ref should be Bone1", "Bone1", skeletonRefs.get(0));
    }

    @Test
    public void testJointNodes() throws Exception {
        InputStream is = getClass().getResourceAsStream("/skinned_cylinder.dae");
        DAEDocument doc = DAEParser.parse(is);

        DAEScene scene = doc.getScene();
        List<DAENode> nodes = scene.getNodes();

        // Find the armature node
        DAENode armatureNode = null;
        for (DAENode node : nodes) {
            if ("Armature".equals(node.getName())) {
                armatureNode = node;
                break;
            }
        }

        assertNotNull("Should find armature node", armatureNode);
        assertTrue("Armature should have children", armatureNode.getChildren().size() > 0);

        // Check first bone (joint)
        DAENode bone1 = armatureNode.getChildren().get(0);
        assertEquals("First bone should be Bone1", "Bone1", bone1.getName());
        assertEquals("First bone type should be JOINT", "JOINT", bone1.getType());
        assertTrue("Bone1 should be a joint", bone1.isJoint());

        // Check bone hierarchy
        assertTrue("Bone1 should have children", bone1.getChildren().size() > 0);
        DAENode bone2 = bone1.getChildren().get(0);
        assertEquals("Second bone should be Bone2", "Bone2", bone2.getName());
        assertTrue("Bone2 should be a joint", bone2.isJoint());

        assertTrue("Bone2 should have children", bone2.getChildren().size() > 0);
        DAENode bone3 = bone2.getChildren().get(0);
        assertEquals("Third bone should be Bone3", "Bone3", bone3.getName());
        assertTrue("Bone3 should be a joint", bone3.isJoint());
    }
}
