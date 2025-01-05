# Tutorial: Getting started

## Installation

### Manual installation

1. Install [Processing](https://processing.org/) if you have not already.
2. Navigate to Shape Mapper's [Releases page](https://github.com/alexdmiller/shape-mapper/releases).
3. Download the `shapemapper.pdex` file for the most recent release.
4. Open the file to automatically install the library.

### Install via Contributions Manager

Installing via the Contributions Manager is not yet available.

## Find an object

A box is an easy thing to model, so look around your space for something box shaped. We'll be using a cardboard box as an example.

![Photo of cardboard box](images/getting-started-1.jpg)

Measure the size of the physical box. Our example box is 18.5" × 18.5" × 16.5" (this is relevant to our code in the next step).

![Person measures cardboard box](images/getting-started-2.jpg)

## Write the Processing sketch

1.  Create a new Processing sketch and import the Shape Mapper library, as well as the Processing [PShape](https://processing.org/tutorials/pshape) class:

    ```
    import spacefiller.shapemapper.ShapeMapper;
    import processing.core.PShape;
    ```

2.  Create two top level variables to store the Shape Mapper library object and the shape that we'll be mapping:

    ```
    ShapeMapper mapper;
    PShape shape;
    ```

3.  Write a setup function that initializes the shape and the Shape Mapper library. When initializing the screen size, it is recommended to use [`fullScreen()`](https://processing.org/reference/fullScreen_.html). It is required to use the [`P3D` render mode](https://processing.org/tutorials/p3d); Shape Mapper will not work without it.

    ``` java
    void setup() {
        fullScreen(P3D);

        // The size of our box is proportional to the physical measurements we made
        int scale = 10;
        shape = createShape(BOX, 18.5 * scale, 18.5 * scale, 16.5 * scale);

        // Initialize the Shape Mapper library with our box
        mapper = new ShapeMapper(this, shape);
    }
    ```

    Note that the size of our box is proportional to the measurements we took of the physical cardboard box. We're modeling the physical object in virtual space by ensuring our virtual dimensions are proportional to our physical dimensions.

4.  Write a draw function that renders the outline of the box. To map our rendered geometry to the physical box, we'll need to sandwich the drawing code within the `mapper.beginMapping()` and `mapper.endMapping()` lines.

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

5.  Putting it all together, our sketch should look like this:

    ``` java
    import spacefiller.shapemapper.ShapeMapper;
    import processing.core.PShape;

    ShapeMapper mapper;
    PShape shape;

    void setup() {
        fullScreen(P3D);
        
        // The size of our box is proportional to the physical measurements we made
        int scale = 10;
        shape = createShape(BOX, 18.5 * scale, 18.5 * scale, 16.5 * scale);

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

## Calibrate the projection mapping

Now that we have the code for the sketch set up, we can calibrate our mapping.

1.  Connect your computer to a projector and point the projector at the object you're mapping.
2.  Run the sketch. The Shape Mapper GUI will appear in the upper left hand portion of the screen. (Note: you can hide this GUI by hitting `T`, or in the code using `mapper.hideGui()`.)

    ![Screen shot of the initial Shape Mapper GUI](images/getting-started-3.png)

3.  Hit `Space` to switch from `Render` mode to `Calibrate` mode. This will reveal the calibration GUI.

    ![Screen shot of the initial Shape Mapper GUI](images/getting-started-4.png)

4.  Your 3D model will appear in the center of the screen. You can navigate the model with the following controls:
    1.  Click + drag to orbit
    2.  Command + click + drag to pan
    3.  Scroll to zoom in and out
5.  Click a point on your model to select it.
6.  Hit `Tab` to switch to mapping mode.
7.  Look at your object in physical space and move your mouse so that the crosshairs are centered on the corresponding vertex of the physical object. Click to create a point in the projected space.

    ![Calibrating the first point of the mapping](images/getting-started-5.gif)

8.  Hit `Tab` to switch back to point selection. Choose another point and repeat the process.
9.  After mapping 6 points, a full calibration will be automatically estimated. Press `Space` so switch back to `Render` mode. In physical space, your object should now be successfully mapped.

    ![Calibrating the final point](images/getting-started-6.gif)

## Tips & tricks

- You can adjust mapped points after placing them, or add more points, to refine your calibration.
- If your model does not accurately represent your physical object, then the mapping will be misaligned.
- Press `Delete` to delete a selected point.
- To completely clear all calibrations, press `Ctrl + Delete`.

## Creating animations

Shape Mapper does not offer any included animation or visual effect functionality; that is up for you to create with Processing code! One quick way to animate your mapped object is to use [`pointLight(...)`](https://processing.org/reference/pointLight_.html):

``` java
void draw() {
    background(0);
    mapper.beginMapping();

    // Add two rotating point lights to color the faces
    pointLight(
        255, 0, 0,
        sin(frameCount / 10f) * 400,
        cos(frameCount / 10f) * 400,
        sin(frameCount / 10f) * 400);
    pointLight(
        0, 0, 255,
        cos(frameCount / 10f) * 400,
        sin(frameCount / 10f) * 400,
        sin(frameCount / 10f) * 400);

    shape(shape);
    mapper.endMapping();
}
```

![Calibrating the final point](images/getting-started-7.gif)

A more advanced technique is to use [shaders](https://processing.org/reference/PShader.html) to create animations. This is out of scope for this tutorial, but some examples of this technique are shown below.

<div style="padding:100% 0 0 0;position:relative;"><iframe src="https://player.vimeo.com/video/1044018259?badge=0&amp;autopause=0&amp;player_id=0&amp;app_id=58479" frameborder="0" allow="autoplay; fullscreen; picture-in-picture; clipboard-write" style="position:absolute;top:0;left:0;width:100%;height:100%;" title="Cardboard box projection mapping"></iframe></div><script src="https://player.vimeo.com/api/player.js"></script>