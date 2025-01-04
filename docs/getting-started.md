# Getting started

## Installation

### Manual installation

1. Ensure you have the latest version of [Processing](https://processing.org/) installed on your computer.
1. Navigate to Shape Mapper's [Releases page](https://github.com/alexdmiller/shape-mapper/releases).
1. Download the `shapemapper.pdex` file for the most recent release.
1. Open the file to automatically install the library.

### Install via Contributions Manager

Installing via the Contributions Manager is not yet currently available.

## Mapping an object

In order to use Shape Mapper, you must have a physical object that you want to projection map, and you must model that object virtually (whether through code or through 3D modeling software like [Blender](https://www.blender.org/)).

To get started quickly, we can use a box-shaped object. In this example, we'll be using a cardboard box, but a book or boxy piece of furniture could also work.

[picture of box]

1.  In order to model our box in code, we'll need to measure the size of the physical box. Our box is [measurements].
1.  Create a new Processing sketch and import the Shape Mapper library, as well as the Processing [PShape](https://processing.org/tutorials/pshape) class:

    ```
    import spacefiller.shapemapper.ShapeMapper;
    import processing.core.PShape;
    ```

1.  Create two top level variables to store the Shape Mapper library object and the shape that we'll be mapping:

    ```
    ShapeMapper mapper;
    PShape shape;
    ```

1.  Write a setup function that initializes the shape and the Shape Mapper library. When initializing the screen size, it is recommended to use [`fullScreen()`](https://processing.org/reference/fullScreen_.html). It is required to use the [`P3D` render mode](https://processing.org/tutorials/p3d); Shape Mapper will not work without it.

    ``` java
    void setup() {
        fullScreen(P3D);

        // The size of our box is proportional to the physical measurements we made
        shape = createShape(BOX, 100, 200, 300);

        // Initialize the Shape Mapper library with our box
        mapper = new ShapeMapper(this, shape);
    }
    ```

1.  Next, we'll write a simple draw function that renders an outline of the box. To map our rendered geometry to the physical box, we'll need to sandwich the drawing code within the `mapper.beginMapping()` and `mapper.endMapping()` lines.

    ``` java
    void draw() {
        background(0);
        
        mapper.beginMapping();
        
        // Disable the default shape style so that we can choose fill and stroke
        // manually in the code
        shape.disableStyle();
        
        // Draw the shape
        fill(0);
        stroke(255);
        shape(shape);
        
        mapper.endMapping();
    }
    ```

1.  Putting it all together, our sketch should look like this:

    ``` java
    import spacefiller.shapemapper.ShapeMapper;
    import processing.core.PShape;

    ShapeMapper mapper;
    PShape shape;

    void setup() {
        fullScreen(P3D);
        shape = createShape(BOX, 100, 200, 300);
        mapper = new ShapeMapper(this, shape);
    }

    void draw() {
        background(0);
        
        mapper.beginMapping();
        
        // Disable the default shape style so that we can choose fill and stroke
        // manually in the code
        shape.disableStyle();
        
        // Draw the shape
        fill(0);
        stroke(255);
        shape(shape);
        
        mapper.endMapping();
    }
    ```

``` java
import spacefiller.shapemapper.ShapeMapper;
import processing.core.PShape;

ShapeMapper mapper;
PShape shape;

void setup() {
    fullScreen(P3D);
    shape = createShape(BOX, 100, 200, 300);
    mapper = new ShapeMapper(this, shape);
}

void draw() {
    background(0);
    
    mapper.beginMapping();
    
    // Disable the default shape style so that we can choose fill and stroke
    // manually in the code
    shape.disableStyle();
    
    // Draw the shape
    fill(0);
    stroke(255);
    shape(shape);
    
    mapper.endMapping();
}
```

## Creating animations

TODO

1. Loop over faces
2. Simple lights
3. Stroke
