package com.daeparser;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Main parser class for COLLADA DAE files.
 * Parses DAE XML files and converts them into Java objects.
 */
public class DAEParser {

    /**
     * Parse a DAE file from a file path.
     *
     * @param filePath Path to the DAE file
     * @return Parsed DAEDocument
     * @throws Exception if parsing fails
     */
    public static DAEDocument parse(String filePath) throws Exception {
        File file = new File(filePath);
        return parse(file);
    }

    /**
     * Parse a DAE file from a File object.
     *
     * @param file DAE file
     * @return Parsed DAEDocument
     * @throws Exception if parsing fails
     */
    public static DAEDocument parse(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        return parseDocument(doc);
    }

    /**
     * Parse a DAE file from an InputStream.
     *
     * @param inputStream InputStream containing DAE data
     * @return Parsed DAEDocument
     * @throws Exception if parsing fails
     */
    public static DAEDocument parse(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);
        return parseDocument(doc);
    }

    private static DAEDocument parseDocument(Document doc) {
        DAEDocument daeDoc = new DAEDocument();

        Element root = doc.getDocumentElement();
        String version = root.getAttribute("version");
        daeDoc.setVersion(version);

        // Parse library_geometries
        NodeList geometryLibraries = root.getElementsByTagName("library_geometries");
        if (geometryLibraries.getLength() > 0) {
            Element geometryLibrary = (Element) geometryLibraries.item(0);
            NodeList geometries = geometryLibrary.getElementsByTagName("geometry");
            for (int i = 0; i < geometries.getLength(); i++) {
                DAEGeometry geometry = parseGeometry((Element) geometries.item(i));
                daeDoc.addGeometry(geometry);
            }
        }

        // Parse library_materials
        NodeList materialLibraries = root.getElementsByTagName("library_materials");
        if (materialLibraries.getLength() > 0) {
            Element materialLibrary = (Element) materialLibraries.item(0);
            NodeList materials = materialLibrary.getElementsByTagName("material");
            for (int i = 0; i < materials.getLength(); i++) {
                DAEMaterial material = parseMaterial((Element) materials.item(i));
                daeDoc.addMaterial(material);
            }
        }

        // Parse library_animations
        NodeList animationLibraries = root.getElementsByTagName("library_animations");
        if (animationLibraries.getLength() > 0) {
            Element animationLibrary = (Element) animationLibraries.item(0);
            NodeList animations = animationLibrary.getElementsByTagName("animation");
            for (int i = 0; i < animations.getLength(); i++) {
                Element animationElement = (Element) animations.item(i);
                // Only parse direct children, not nested animations
                if (animationElement.getParentNode().equals(animationLibrary)) {
                    DAEAnimation animation = parseAnimation(animationElement);
                    daeDoc.addAnimation(animation);
                }
            }
        }

        // Parse library_controllers
        NodeList controllerLibraries = root.getElementsByTagName("library_controllers");
        if (controllerLibraries.getLength() > 0) {
            Element controllerLibrary = (Element) controllerLibraries.item(0);
            NodeList controllers = controllerLibrary.getElementsByTagName("controller");
            for (int i = 0; i < controllers.getLength(); i++) {
                DAEController controller = parseController((Element) controllers.item(i));
                daeDoc.addController(controller);
            }
        }

        // Parse library_visual_scenes
        NodeList sceneLibraries = root.getElementsByTagName("library_visual_scenes");
        if (sceneLibraries.getLength() > 0) {
            Element sceneLibrary = (Element) sceneLibraries.item(0);
            NodeList visualScenes = sceneLibrary.getElementsByTagName("visual_scene");
            if (visualScenes.getLength() > 0) {
                DAEScene scene = parseScene((Element) visualScenes.item(0));
                daeDoc.setScene(scene);
            }
        }

        return daeDoc;
    }

    private static DAEGeometry parseGeometry(Element geometryElement) {
        DAEGeometry geometry = new DAEGeometry();
        geometry.setId(geometryElement.getAttribute("id"));
        geometry.setName(geometryElement.getAttribute("name"));

        NodeList meshes = geometryElement.getElementsByTagName("mesh");
        if (meshes.getLength() > 0) {
            DAEMesh mesh = parseMesh((Element) meshes.item(0));
            geometry.setMesh(mesh);
        }

        return geometry;
    }

    private static DAEMesh parseMesh(Element meshElement) {
        DAEMesh mesh = new DAEMesh();

        // Parse sources
        NodeList sources = meshElement.getElementsByTagName("source");
        for (int i = 0; i < sources.getLength(); i++) {
            DAESource source = parseSource((Element) sources.item(i));
            mesh.addSource(source);
        }

        // Parse vertices
        NodeList verticesElements = meshElement.getElementsByTagName("vertices");
        if (verticesElements.getLength() > 0) {
            Element verticesElement = (Element) verticesElements.item(0);
            String verticesId = verticesElement.getAttribute("id");
            mesh.setVerticesId(verticesId);
            
            // Parse input elements inside vertices to find position source
            NodeList vertexInputs = verticesElement.getElementsByTagName("input");
            for (int i = 0; i < vertexInputs.getLength(); i++) {
                Element input = (Element) vertexInputs.item(i);
                String semantic = input.getAttribute("semantic");
                String source = input.getAttribute("source");
                if (source.startsWith("#")) {
                    source = source.substring(1);
                }
                if (semantic.equals("POSITION")) {
                    // Store the position source for VERTEX semantic
                    mesh.addInputSemantic("VERTEX", source);
                }
            }
        }

        // Parse triangles
        NodeList trianglesElements = meshElement.getElementsByTagName("triangles");
        if (trianglesElements.getLength() > 0) {
            Element trianglesElement = (Element) trianglesElements.item(0);
            String countStr = trianglesElement.getAttribute("count");
            if (!countStr.isEmpty()) {
                mesh.setTriangleCount(Integer.parseInt(countStr));
            }

            // Parse input elements and store semantics and offsets
            NodeList inputElements = trianglesElement.getElementsByTagName("input");
            int maxOffset = 0;
            for (int i = 0; i < inputElements.getLength(); i++) {
                Element input = (Element) inputElements.item(i);
                String semantic = input.getAttribute("semantic");
                String source = input.getAttribute("source");
                String offsetStr = input.getAttribute("offset");
                
                if (source.startsWith("#")) {
                    source = source.substring(1);
                }
                
                int offset = 0;
                if (!offsetStr.isEmpty()) {
                    offset = Integer.parseInt(offsetStr);
                    if (offset > maxOffset) {
                        maxOffset = offset;
                    }
                }
                
                // Store semantic to offset mapping
                mesh.addInputOffset(semantic, offset);
                
                // Store semantic to source mapping (except VERTEX which was already mapped)
                if (!semantic.equals("VERTEX")) {
                    mesh.addInputSemantic(semantic, source);
                }
            }
            int stride = maxOffset + 1;

            NodeList pElements = trianglesElement.getElementsByTagName("p");
            if (pElements.getLength() > 0) {
                Element pElement = (Element) pElements.item(0);
                String[] indices = pElement.getTextContent().trim().split("\\s+");
                
                List<int[]> trianglesList = new ArrayList<>();
                List<int[]> triangleIndicesList = new ArrayList<>();
                
                // Process triangles considering stride (multiple inputs with offsets)
                for (int i = 0; i < indices.length; i += stride * 3) {
                    if (i + stride * 2 < indices.length) {
                        // For backward compatibility, keep simple vertex-only triangles
                        int[] triangle = new int[3];
                        triangle[0] = Integer.parseInt(indices[i]);
                        triangle[1] = Integer.parseInt(indices[i + stride]);
                        triangle[2] = Integer.parseInt(indices[i + stride * 2]);
                        trianglesList.add(triangle);
                        
                        // Store full index data for triangulation
                        int[] fullIndices = new int[stride * 3];
                        for (int j = 0; j < stride * 3; j++) {
                            fullIndices[j] = Integer.parseInt(indices[i + j]);
                        }
                        triangleIndicesList.add(fullIndices);
                    }
                }
                mesh.setTriangles(trianglesList);
                mesh.setTriangleIndices(triangleIndicesList);
            }
        }

        // Parse polylist (alternative to triangles)
        NodeList polylistElements = meshElement.getElementsByTagName("polylist");
        if (polylistElements.getLength() > 0) {
            Element polylistElement = (Element) polylistElements.item(0);
            String countStr = polylistElement.getAttribute("count");
            if (!countStr.isEmpty()) {
                mesh.setTriangleCount(Integer.parseInt(countStr));
            }
        }

        return mesh;
    }

    private static DAESource parseSource(Element sourceElement) {
        DAESource source = new DAESource();
        source.setId(sourceElement.getAttribute("id"));
        source.setName(sourceElement.getAttribute("name"));

        NodeList floatArrays = sourceElement.getElementsByTagName("float_array");
        if (floatArrays.getLength() > 0) {
            Element floatArray = (Element) floatArrays.item(0);
            String countStr = floatArray.getAttribute("count");
            if (!countStr.isEmpty()) {
                source.setCount(Integer.parseInt(countStr));
            }

            String[] values = floatArray.getTextContent().trim().split("\\s+");
            List<Float> data = new ArrayList<>();
            for (String value : values) {
                if (!value.isEmpty()) {
                    data.add(Float.parseFloat(value));
                }
            }
            source.setData(data);
        }

        // Parse accessor for stride information
        NodeList accessors = sourceElement.getElementsByTagName("accessor");
        if (accessors.getLength() > 0) {
            Element accessor = (Element) accessors.item(0);
            String strideStr = accessor.getAttribute("stride");
            if (!strideStr.isEmpty()) {
                source.setStride(Integer.parseInt(strideStr));
            }
        }

        return source;
    }

    private static DAEMaterial parseMaterial(Element materialElement) {
        DAEMaterial material = new DAEMaterial();
        material.setId(materialElement.getAttribute("id"));
        material.setName(materialElement.getAttribute("name"));

        // Parse instance_effect to get material properties
        NodeList instanceEffects = materialElement.getElementsByTagName("instance_effect");
        if (instanceEffects.getLength() > 0) {
            Element instanceEffect = (Element) instanceEffects.item(0);
            String url = instanceEffect.getAttribute("url");
            if (url.startsWith("#")) {
                material.setTextureId(url.substring(1));
            }
        }

        return material;
    }

    private static DAEScene parseScene(Element sceneElement) {
        DAEScene scene = new DAEScene();
        scene.setId(sceneElement.getAttribute("id"));
        scene.setName(sceneElement.getAttribute("name"));

        NodeList nodes = sceneElement.getElementsByTagName("node");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element nodeElement = (Element) nodes.item(i);
            // Only parse direct children, not nested nodes
            if (nodeElement.getParentNode().equals(sceneElement)) {
                DAENode node = parseNode(nodeElement);
                scene.addNode(node);
            }
        }

        return scene;
    }

    private static DAENode parseNode(Element nodeElement) {
        DAENode node = new DAENode();
        node.setId(nodeElement.getAttribute("id"));
        node.setName(nodeElement.getAttribute("name"));
        
        // Parse node type
        String type = nodeElement.getAttribute("type");
        if (type != null && !type.isEmpty()) {
            node.setType(type);
        }

        // Parse transformation matrix
        NodeList matrices = nodeElement.getElementsByTagName("matrix");
        if (matrices.getLength() > 0) {
            Element matrix = (Element) matrices.item(0);
            String[] values = matrix.getTextContent().trim().split("\\s+");
            if (values.length == 16) {
                float[] transformation = new float[16];
                for (int i = 0; i < 16; i++) {
                    transformation[i] = Float.parseFloat(values[i]);
                }
                node.setTransformation(transformation);
            }
        }

        // Parse instance_geometry
        NodeList instanceGeometries = nodeElement.getElementsByTagName("instance_geometry");
        if (instanceGeometries.getLength() > 0) {
            Element instanceGeometry = (Element) instanceGeometries.item(0);
            String url = instanceGeometry.getAttribute("url");
            if (url.startsWith("#")) {
                node.setGeometryRef(url.substring(1));
            }
        }
        
        // Parse instance_controller
        NodeList instanceControllers = nodeElement.getElementsByTagName("instance_controller");
        if (instanceControllers.getLength() > 0) {
            Element instanceController = (Element) instanceControllers.item(0);
            String url = instanceController.getAttribute("url");
            if (url.startsWith("#")) {
                node.setControllerRef(url.substring(1));
            }
            
            // Parse skeleton references
            NodeList skeletons = instanceController.getElementsByTagName("skeleton");
            for (int i = 0; i < skeletons.getLength(); i++) {
                Element skeleton = (Element) skeletons.item(i);
                String skeletonRef = skeleton.getTextContent().trim();
                if (skeletonRef.startsWith("#")) {
                    skeletonRef = skeletonRef.substring(1);
                }
                node.addSkeletonRef(skeletonRef);
            }
        }

        // Parse child nodes
        NodeList childNodes = nodeElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE && 
                childNode.getNodeName().equals("node")) {
                DAENode child = parseNode((Element) childNode);
                node.addChild(child);
            }
        }

        return node;
    }

    private static DAEAnimation parseAnimation(Element animationElement) {
        DAEAnimation animation = new DAEAnimation();
        animation.setId(animationElement.getAttribute("id"));
        animation.setName(animationElement.getAttribute("name"));

        // Parse sources
        NodeList sources = animationElement.getElementsByTagName("source");
        for (int i = 0; i < sources.getLength(); i++) {
            Element sourceElement = (Element) sources.item(i);
            // Only parse direct children
            if (sourceElement.getParentNode().equals(animationElement)) {
                DAESource source = parseSource(sourceElement);
                animation.addSource(source);
            }
        }

        // Parse samplers
        NodeList samplers = animationElement.getElementsByTagName("sampler");
        for (int i = 0; i < samplers.getLength(); i++) {
            Element samplerElement = (Element) samplers.item(i);
            if (samplerElement.getParentNode().equals(animationElement)) {
                DAESampler sampler = parseSampler(samplerElement);
                animation.addSampler(sampler);
            }
        }

        // Parse channels
        NodeList channels = animationElement.getElementsByTagName("channel");
        for (int i = 0; i < channels.getLength(); i++) {
            Element channelElement = (Element) channels.item(i);
            if (channelElement.getParentNode().equals(animationElement)) {
                DAEChannel channel = parseChannel(channelElement);
                animation.addChannel(channel);
            }
        }

        return animation;
    }

    private static DAESampler parseSampler(Element samplerElement) {
        DAESampler sampler = new DAESampler();
        sampler.setId(samplerElement.getAttribute("id"));

        // Parse input elements
        NodeList inputs = samplerElement.getElementsByTagName("input");
        for (int i = 0; i < inputs.getLength(); i++) {
            Element input = (Element) inputs.item(i);
            String semantic = input.getAttribute("semantic");
            String source = input.getAttribute("source");
            if (source.startsWith("#")) {
                source = source.substring(1);
            }
            sampler.addInput(semantic, source);
        }

        return sampler;
    }

    private static DAEChannel parseChannel(Element channelElement) {
        DAEChannel channel = new DAEChannel();
        
        String source = channelElement.getAttribute("source");
        if (source.startsWith("#")) {
            source = source.substring(1);
        }
        channel.setSource(source);
        
        String target = channelElement.getAttribute("target");
        channel.setTarget(target);

        return channel;
    }

    private static DAEController parseController(Element controllerElement) {
        DAEController controller = new DAEController();
        controller.setId(controllerElement.getAttribute("id"));
        controller.setName(controllerElement.getAttribute("name"));

        // Parse skin element
        NodeList skins = controllerElement.getElementsByTagName("skin");
        if (skins.getLength() > 0) {
            DAESkin skin = parseSkin((Element) skins.item(0));
            controller.setSkin(skin);
        }

        return controller;
    }

    private static DAESkin parseSkin(Element skinElement) {
        DAESkin skin = new DAESkin();
        
        // Parse source reference (geometry being skinned)
        String source = skinElement.getAttribute("source");
        if (source.startsWith("#")) {
            source = source.substring(1);
        }
        skin.setSource(source);

        // Parse bind_shape_matrix
        NodeList bindShapeMatrices = skinElement.getElementsByTagName("bind_shape_matrix");
        if (bindShapeMatrices.getLength() > 0) {
            Element bindShapeMatrix = (Element) bindShapeMatrices.item(0);
            String[] values = bindShapeMatrix.getTextContent().trim().split("\\s+");
            if (values.length == 16) {
                float[] matrix = new float[16];
                for (int i = 0; i < 16; i++) {
                    matrix[i] = Float.parseFloat(values[i]);
                }
                skin.setBindShapeMatrix(matrix);
            }
        }

        // Parse sources (joint names, bind matrices, weights)
        NodeList sources = skinElement.getElementsByTagName("source");
        for (int i = 0; i < sources.getLength(); i++) {
            Element sourceElement = (Element) sources.item(i);
            if (sourceElement.getParentNode().equals(skinElement)) {
                DAESource source2 = parseSourceForSkin(sourceElement);
                skin.addSource(source2);
            }
        }

        // Parse joints element to identify sources
        NodeList jointsElements = skinElement.getElementsByTagName("joints");
        if (jointsElements.getLength() > 0) {
            Element jointsElement = (Element) jointsElements.item(0);
            NodeList inputs = jointsElement.getElementsByTagName("input");
            
            String jointNamesSourceId = null;
            String inverseBindMatricesSourceId = null;
            
            for (int i = 0; i < inputs.getLength(); i++) {
                Element input = (Element) inputs.item(i);
                String semantic = input.getAttribute("semantic");
                String sourceRef = input.getAttribute("source");
                if (sourceRef.startsWith("#")) {
                    sourceRef = sourceRef.substring(1);
                }
                
                if (semantic.equals("JOINT")) {
                    jointNamesSourceId = sourceRef;
                } else if (semantic.equals("INV_BIND_MATRIX")) {
                    inverseBindMatricesSourceId = sourceRef;
                }
            }
            
            // Extract joint names and inverse bind matrices
            for (DAESource source2 : skin.getSources()) {
                if (source2.getId().equals(jointNamesSourceId)) {
                    // This is the joint names source (Name_array)
                    List<String> names = source2.getNames();
                    if (names != null) {
                        skin.setJointNames(names);
                    }
                } else if (source2.getId().equals(inverseBindMatricesSourceId)) {
                    // This is the inverse bind matrices source
                    float[] matrices = source2.getDataAsArray();
                    skin.setInverseBindMatrices(matrices);
                }
            }
        }

        // Parse vertex_weights element
        NodeList vertexWeightsElements = skinElement.getElementsByTagName("vertex_weights");
        if (vertexWeightsElements.getLength() > 0) {
            Element vertexWeightsElement = (Element) vertexWeightsElements.item(0);
            String countStr = vertexWeightsElement.getAttribute("count");
            int vertexCount = countStr.isEmpty() ? 0 : Integer.parseInt(countStr);
            
            // Parse inputs to get offsets
            NodeList inputs = vertexWeightsElement.getElementsByTagName("input");
            int jointOffset = -1;
            int weightOffset = -1;
            String weightsSourceId = null;
            int maxOffset = 0;
            
            for (int i = 0; i < inputs.getLength(); i++) {
                Element input = (Element) inputs.item(i);
                String semantic = input.getAttribute("semantic");
                String offsetStr = input.getAttribute("offset");
                String sourceRef = input.getAttribute("source");
                if (sourceRef.startsWith("#")) {
                    sourceRef = sourceRef.substring(1);
                }
                
                int offset = offsetStr.isEmpty() ? 0 : Integer.parseInt(offsetStr);
                if (offset > maxOffset) {
                    maxOffset = offset;
                }
                
                if (semantic.equals("JOINT")) {
                    jointOffset = offset;
                } else if (semantic.equals("WEIGHT")) {
                    weightOffset = offset;
                    weightsSourceId = sourceRef;
                }
            }
            
            int stride = maxOffset + 1;
            
            // Get weights array
            for (DAESource source2 : skin.getSources()) {
                if (source2.getId().equals(weightsSourceId)) {
                    float[] weightsArray = source2.getDataAsArray();
                    skin.setWeights(weightsArray);
                    break;
                }
            }
            
            // Parse vcount (number of influences per vertex)
            NodeList vcountElements = vertexWeightsElement.getElementsByTagName("vcount");
            int[] vcounts = null;
            if (vcountElements.getLength() > 0) {
                Element vcountElement = (Element) vcountElements.item(0);
                String[] vcountValues = vcountElement.getTextContent().trim().split("\\s+");
                vcounts = new int[vcountValues.length];
                int maxInfluences = 0;
                for (int i = 0; i < vcountValues.length; i++) {
                    vcounts[i] = Integer.parseInt(vcountValues[i]);
                    if (vcounts[i] > maxInfluences) {
                        maxInfluences = vcounts[i];
                    }
                }
                skin.setMaxJointInfluences(maxInfluences);
            }
            
            // Parse v (joint and weight indices)
            NodeList vElements = vertexWeightsElement.getElementsByTagName("v");
            if (vElements.getLength() > 0 && vcounts != null) {
                Element vElement = (Element) vElements.item(0);
                String[] vValues = vElement.getTextContent().trim().split("\\s+");
                
                int vIndex = 0;
                for (int i = 0; i < vcounts.length; i++) {
                    int influenceCount = vcounts[i];
                    int[] vertexWeight = new int[influenceCount * 2]; // pairs of (joint_index, weight_index)
                    
                    for (int j = 0; j < influenceCount; j++) {
                        if (vIndex + stride <= vValues.length) {
                            int jointIdx = Integer.parseInt(vValues[vIndex + jointOffset]);
                            int weightIdx = Integer.parseInt(vValues[vIndex + weightOffset]);
                            
                            vertexWeight[j * 2] = jointIdx;
                            vertexWeight[j * 2 + 1] = weightIdx;
                            
                            vIndex += stride;
                        }
                    }
                    
                    skin.addVertexWeight(vertexWeight);
                }
            }
        }

        return skin;
    }

    private static DAESource parseSourceForSkin(Element sourceElement) {
        DAESource source = new DAESource();
        source.setId(sourceElement.getAttribute("id"));
        source.setName(sourceElement.getAttribute("name"));

        // Parse float_array for numeric data
        NodeList floatArrays = sourceElement.getElementsByTagName("float_array");
        if (floatArrays.getLength() > 0) {
            Element floatArray = (Element) floatArrays.item(0);
            String countStr = floatArray.getAttribute("count");
            if (!countStr.isEmpty()) {
                source.setCount(Integer.parseInt(countStr));
            }

            String[] values = floatArray.getTextContent().trim().split("\\s+");
            List<Float> data = new ArrayList<>();
            for (String value : values) {
                if (!value.isEmpty()) {
                    data.add(Float.parseFloat(value));
                }
            }
            source.setData(data);
        }
        
        // Parse Name_array for joint names
        NodeList nameArrays = sourceElement.getElementsByTagName("Name_array");
        if (nameArrays.getLength() > 0) {
            Element nameArray = (Element) nameArrays.item(0);
            String countStr = nameArray.getAttribute("count");
            if (!countStr.isEmpty()) {
                source.setCount(Integer.parseInt(countStr));
            }

            String[] values = nameArray.getTextContent().trim().split("\\s+");
            List<String> names = new ArrayList<>();
            for (String value : values) {
                if (!value.isEmpty()) {
                    names.add(value);
                }
            }
            source.setNames(names);
        }

        // Parse accessor for stride information
        NodeList accessors = sourceElement.getElementsByTagName("accessor");
        if (accessors.getLength() > 0) {
            Element accessor = (Element) accessors.item(0);
            String strideStr = accessor.getAttribute("stride");
            if (!strideStr.isEmpty()) {
                source.setStride(Integer.parseInt(strideStr));
            }
        }

        return source;
    }
}
