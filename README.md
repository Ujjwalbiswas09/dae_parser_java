# DAE Parser for Java

A lightweight COLLADA (DAE) file parser for Java and Android applications. This library allows you to parse 3D model files in the COLLADA Digital Asset Exchange (.dae) format.

## Features

- ✅ Parse COLLADA DAE files (version 1.4.1)
- ✅ Extract 3D geometry data (vertices, normals, texture coordinates)
- ✅ Parse mesh triangles and polygons
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

// Get scene hierarchy
DAEScene scene = doc.getScene();
List<DAENode> nodes = scene.getNodes();
```

## API Overview

### Core Classes

- **DAEParser**: Main parser class with static `parse()` methods
- **DAEDocument**: Root document containing all parsed elements
- **DAEGeometry**: Represents a 3D geometry with an ID and name
- **DAEMesh**: Contains mesh data including sources, vertices, and triangles
- **DAESource**: Data arrays for positions, normals, UVs, etc.
- **DAEMaterial**: Material properties (colors, textures)
- **DAEScene**: Scene hierarchy container
- **DAENode**: Scene node with transformation matrix and geometry references

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
- `cube.dae` - A simple cube mesh
- `triangle.dae` - A basic triangle

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
- Support for animations
- Support for skinning/rigging data
- Support for camera and light parsing
- COLLADA 1.5.0 support
- Binary DAE support

## Author

Ujjwal Biswas
