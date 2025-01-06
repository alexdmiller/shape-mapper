# Mapping multiple objects

Shape Mapper allows you to map two physical objects separately.

## Write the Processing sketch

Starting from the code in the [Getting Started tutorial](tutorial-getting-started.md), we need to make a couple of modifications.

1.  In addition to importing the main `ShapeMapper` class, you'll also need to import two other classes, `MappedShape` and `Mapping`:

    ``` java
    import spacefiller.shapemapper.ShapeMapper;
    import spacefiller.shapemapper.MappedShape;
    import spacefiller.shapemapper.Mapping;
    ```

2.  We'll need to store references to two `PShape`s at the top level of our sketch. In addition, we'll also need to store references to two `MappedShape`s.

    ``` java
    PShape box;
    PShape sphere;
    MappedShape mappedBox;
    MappedShape mappedSphere;
    ```

3.  Modify the setup function to add the shapes with the [`addShape(...)`](https://alexdmiller.github.io/shape-mapper/javadoc/spacefiller/shapemapper/ShapeMapper.html#addShape(java.lang.String,processing.core.PShape,int)) method.

    ``` java
    box = createShape(BOX, 150);
    sphereDetail(8);
    sphere = createShape(SPHERE, 150);

    mapper = new ShapeMapper(this);
    mappedBox = mapper.addShape("box", box);
    mappedSphere = mapper.addShape("sphere", sphere);
    ```

4.  Now modify the draw function with this code:

    ``` java
    mappedBox.beginMapping();
    shape(box);
    mappedBox.endMapping();
    
    mappedSphere.beginMapping();
    shape(sphere);
    mappedSphere.endMapping();
    ```

5.  Putting it all together:

    ``` java
    import spacefiller.shapemapper.ShapeMapper;
    import spacefiller.shapemapper.MappedShape;
    import spacefiller.shapemapper.Mapping;

    ShapeMapper mapper;
    PShape box;
    PShape sphere;
    MappedShape mappedBox;
    MappedShape mappedSphere;

    void setup() {
      fullScreen(P3D);
      box = createShape(BOX, 150);
      sphereDetail(8);
      sphere = createShape(SPHERE, 150);

      mapper = new ShapeMapper(this);
      mappedBox = mapper.addShape("box", box);
      mappedSphere = mapper.addShape("sphere", sphere);
    }

    void draw() {
      background(0);  

      mappedBox.beginMapping();
      shape(box);
      mappedBox.endMapping();
      
      mappedSphere.beginMapping();
      shape(sphere);
      mappedSphere.endMapping();
    }
    ```

## Calibrate the projection mapping

1.  Run the sketch.
2.  Hit `Space` to switch from `Render` mode to `Calibrate` mode.
3.  Click a point on your model to select it.
4.  Hit `Tab` to switch to mapping mode.
5.  Look at your object in physical space and move your mouse so that the crosshairs are centered on the corresponding vertex of the physical object. Click to create a point in the projected space.

    ![Calibrating the first point of the mapping](images/getting-started-5.gif)

6.  Hit `Tab` to switch back to point selection. Choose another point and repeat the process.
7.  After mapping 6 points, a full calibration will be automatically estimated.
8.  Now press `Down ↓` to switch to the next shape.
9.  Repeat steps 3-7 for your second shape.
