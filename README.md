# DAE Parser for Java

A lightweight COLLADA (DAE) file parser for Java and Android applications. This library allows you to parse 3D model files in the COLLADA Digital Asset Exchange (.dae) format.

## Features

- ✅ Parse COLLADA DAE files (version 1.4.1)
- ✅ Extract 3D geometry data (vertices, normals, texture coordinates)
- ✅ Parse mesh triangles and polygons
- ✅ **Generate triangulated vertex data for VBO creation**
- ✅ **Parse skeleton and skinning data for character animation**
- ✅ **Support for skeletal hierarchies and joint transformations**
- ✅ Parse animations (keyframes, samplers, channels)
- ✅ Access material properties
- ✅ Parse scene hierarchies and transformations
- ✅ Pure Java implementation (no external dependencies)
- ✅ Android compatible
- ✅ Easy-to-use API

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.daeparser</groupId>
    <artifactId>dae-parser</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

Add the following to your `build.gradle`:

```gradle
implementation 'com.daeparser:dae-parser:1.0.0'
```

### Manual Installation

Clone the repository and build with Maven:

```bash
git clone https://github.com/Ujjwalbiswas09/dae_parser_java.git
cd dae_parser_java
mvn clean install
```

## Usage

### Basic Example

```java
import com.daeparser.*;

// Parse a DAE file
DAEDocument doc = DAEParser.parse("path/to/model.dae");

// Access geometries
for (DAEGeometry geometry : doc.getGeometries()) {
    System.out.println("Geometry: " + geometry.getName());
    
    DAEMesh mesh = geometry.getMesh();
    System.out.println("Triangle count: " + mesh.getTriangleCount());
    
    // Access vertex data
    for (DAESource source : mesh.getSources()) {
        float[] data = source.getDataAsArray();
        System.out.println("Source data: " + data.length + " values");
    }
}

// Access scene information
DAEScene scene = doc.getScene();
for (DAENode node : scene.getNodes()) {
    System.out.println("Node: " + node.getName());
    System.out.println("Geometry reference: " + node.getGeometryRef());
}

// Access animations
for (DAEAnimation animation : doc.getAnimations()) {
    System.out.println("Animation: " + animation.getName());
    
    // Get animation channels
    for (DAEChannel channel : animation.getChannels()) {
        System.out.println("  Target: " + channel.getTarget());
    }
    
    // Get animation sources (keyframes, values)
    for (DAESource source : animation.getSources()) {
        float[] data = source.getDataAsArray();
        System.out.println("  Source data: " + Arrays.toString(data));
    }
}
```

### Parsing Options

The parser supports multiple input methods:

```java
// From file path
DAEDocument doc = DAEParser.parse("model.dae");

// From File object
File file = new File("model.dae");
DAEDocument doc = DAEParser.parse(file);

// From InputStream
InputStream is = new FileInputStream("model.dae");
DAEDocument doc = DAEParser.parse(is);
```

### Accessing Data

```java
// Get COLLADA version
String version = doc.getVersion();

// Get all geometries
List<DAEGeometry> geometries = doc.getGeometries();

// Get materials
List<DAEMaterial> materials = doc.getMaterials();

// Get animations
List<DAEAnimation> animations = doc.getAnimations();

// Get controllers (for skinned meshes)
List<DAEController> controllers = doc.getControllers();

// Get scene hierarchy
DAEScene scene = doc.getScene();
List<DAENode> nodes = scene.getNodes();
```

### Triangulated Data for VBO Creation

The parser provides methods to get triangulated vertex data suitable for direct use with Vertex Buffer Objects (VBO) in OpenGL, Vulkan, DirectX, etc.

```java
DAEGeometry geometry = doc.getGeometries().get(0);
DAEMesh mesh = geometry.getMesh();

// Get interleaved vertex data (position + normal + texcoord)
// Format: [x, y, z, nx, ny, nz, u, v, x, y, z, nx, ny, nz, u, v, ...]
float[] vboData = mesh.getTriangulatedVertexData();

// Or get individual attributes:
float[] positions = mesh.getTriangulatedPositions();  // [x, y, z, x, y, z, ...]
float[] normals = mesh.getTriangulatedNormals();      // [nx, ny, nz, nx, ny, nz, ...]
float[] texCoords = mesh.getTriangulatedTexCoords();  // [u, v, u, v, ...]

// Use with OpenGL
// glBufferData(GL_ARRAY_BUFFER, vboData, GL_STATIC_DRAW);
```

**Why triangulated data?**
- COLLADA files use indexed vertices (like `<p>0 1 2</p>`) that reference separate arrays for positions, normals, and texture coordinates
- For VBO creation, you typically need expanded (non-indexed) vertex data where each triangle vertex has all its attributes
- The triangulation methods expand the indexed data into contiguous arrays ready for GPU upload

### Skeleton and Skinning for Character Animation

The parser supports COLLADA's skeleton and skinning system, which allows you to load rigged characters for skeletal animation.

```java
DAEDocument doc = DAEParser.parse("character.dae");

// Get controllers (skin data)
List<DAEController> controllers = doc.getControllers();
DAEController controller = controllers.get(0);
DAESkin skin = controller.getSkin();

// Get joint/bone names
List<String> jointNames = skin.getJointNames();
System.out.println("Joints: " + jointNames);

// Get bind shape matrix (transforms mesh to bind pose)
float[] bindShapeMatrix = skin.getBindShapeMatrix();

// Get inverse bind matrices for each joint (16 floats per joint)
float[] inverseBindMatrices = skin.getInverseBindMatrices();
float[] joint0Matrix = skin.getJointInverseBindMatrix(0);

// Get vertex weights (which joints influence which vertices)
for (int i = 0; i < skin.getVertexWeights().size(); i++) {
    float[] influences = skin.getVertexJointInfluences(i);
    // influences array: [jointIdx0, weight0, jointIdx1, weight1, ...]
    System.out.println("Vertex " + i + " influences: " + Arrays.toString(influences));
}

// Get skeleton hierarchy from scene
DAEScene scene = doc.getScene();
for (DAENode node : scene.getNodes()) {
    // Check if node references a skinned mesh
    if (node.getControllerRef() != null) {
        System.out.println("Skinned mesh node: " + node.getName());
        System.out.println("Controller: " + node.getControllerRef());
        System.out.println("Skeleton roots: " + node.getSkeletonRefs());
    }
    
    // Traverse joint hierarchy
    traverseJoints(node);
}

void traverseJoints(DAENode node) {
    if (node.isJoint()) {
        System.out.println("Joint: " + node.getName());
        float[] transform = node.getTransformation();
        // Use joint transformation matrix
    }
    for (DAENode child : node.getChildren()) {
        traverseJoints(child);
    }
}
```

**Skinning workflow:**
1. Parse the DAE file to get geometry, controller (skin), and skeleton nodes
2. The skin contains joint names, inverse bind matrices, and vertex weights
3. Each vertex can be influenced by multiple joints with different weights
4. Use the bind shape matrix, inverse bind matrices, and joint transformations to compute final vertex positions
5. During animation, update joint transformations based on animation keyframes

**Key concepts:**
- **Skin**: Binds a mesh geometry to a skeleton for animation
- **Joint/Bone**: A node in the skeleton hierarchy (marked with `type="JOINT"`)
- **Bind Shape Matrix**: Transforms the mesh into the bind pose before skinning
- **Inverse Bind Matrix**: The inverse of each joint's world transform at bind time
- **Vertex Weights**: Defines how much each joint influences each vertex (skinning weights)
- **Skeleton Reference**: Points to the root joint node(s) for the skeleton


## API Overview

### Core Classes

- **DAEParser**: Main parser class with static `parse()` methods
- **DAEDocument**: Root document containing all parsed elements
- **DAEGeometry**: Represents a 3D geometry with an ID and name
- **DAEMesh**: Contains mesh data including sources, vertices, and triangles
- **DAESource**: Data arrays for positions, normals, UVs, animation keyframes, joint names, etc.
- **DAEMaterial**: Material properties (colors, textures)
- **DAEController**: Controller containing skin data for skeletal animation
- **DAESkin**: Skin data binding a mesh to a skeleton (joints, weights, bind matrices)
- **DAEAnimation**: Animation data with channels, samplers, and sources
- **DAEChannel**: Animation channel linking sampler to target node property
- **DAESampler**: Animation sampler defining interpolation between keyframes
- **DAEScene**: Scene hierarchy container
- **DAENode**: Scene node with transformation matrix, geometry/controller references, and skeleton links

## Building

Build the project with Maven:

```bash
mvn clean install
```

Run tests:

```bash
mvn test
```

## Testing

The library includes comprehensive unit tests with sample DAE files:

```bash
mvn test
```

Test files are located in `src/test/resources/`:
- `cube.dae` - A simple cube mesh with normals
- `triangle.dae` - A basic triangle
- `animated_cube.dae` - A cube with location animation
- `skinned_cylinder.dae` - A skinned mesh with 3-bone skeleton

## Requirements

- Java 8 or higher
- Maven 3.6+ (for building)

## License

This project is open source and available under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

If you encounter any issues or have questions, please open an issue on GitHub.

## Roadmap

Future enhancements:
- Morph targets/blend shapes support
- Support for camera and light parsing
- COLLADA 1.5.0 support
- Binary DAE support
- Advanced animation interpolation methods

## Author

Ujjwal Biswas
