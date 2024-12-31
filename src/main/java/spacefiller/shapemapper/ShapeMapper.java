package spacefiller.shapemapper;

import processing.core.*;
import spacefiller.peasy.CameraState;
import spacefiller.peasy.PeasyCam;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.opengl.PGraphics3D;
import processing.opengl.PShader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static processing.core.PConstants.*;
import static spacefiller.shapemapper.GeometryUtils.*;

public class ShapeMapper {
  private static final float UI_CIRCLE_RADIUS = 20;

  private enum Mode {
    CALIBRATE, RENDER
  }

  private enum CalibrateMode {
    SELECT_POINT, PROJECTION, MASK_FACES
  }

  private PApplet parent;

  private PGraphics3D parentGraphics;
  public PGraphics3D shapeCanvas;
  private PGraphics3D projectionCanvas;

  private List<MappedShape> shapes;
  private List<MappedShape> previouslySavedShapes;
  private int currentShapeIndex = -1;
  private int currentMappingIndex = -1;

  private Mode mode;
  private CalibrateMode calibrateMode;
  private PeasyCam camera;

  private PVector selectedVertex;

  PShader shapeRenderShader;
  PShader normalShader;

  private int recentlyHoveredSubshapeIndex;

  public ShapeMapper(PApplet parent) {
    this(parent, null);
  }

  public ShapeMapper(PApplet parent, PShape shape) {
    try {
      this.parent = parent;
      try {
        this.parentGraphics = (PGraphics3D) parent.getGraphics();
      } catch (ClassCastException e) {
        System.out.println("ShapeMapper: Must use P3D rendering mode with ShapeMapper library");
        System.out.println("ShapeMapper:   size(P3D, 500, 500)");
      }
      this.shapeCanvas = (PGraphics3D) parent.createGraphics(parent.width, parent.height, P3D);
      this.projectionCanvas = (PGraphics3D) parent.createGraphics(parent.width, parent.height, P3D);
      this.mode = Mode.RENDER;
      this.calibrateMode = CalibrateMode.SELECT_POINT;
      this.camera = new PeasyCam(parent, shapeCanvas, 400);

      this.parent.registerMethod("draw", this);
      this.parent.registerMethod("mouseEvent", this);
      this.parent.registerMethod("keyEvent", this);

      shapeRenderShader = parent.loadShader(IOUtils.extractResourceToFile("/checkers.frag.glsl"));
      normalShader = parent.loadShader(
          IOUtils.extractResourceToFile("/normal_shading.frag.glsl"),
          IOUtils.extractResourceToFile("/normal_shading.vert.glsl"));

      this.shapes = new ArrayList<>();
      loadCalibration();

//      calibrationData = CalibrationUtils.calibrate(pointMapping, parent.width, parent.height);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

    if (shape != null) {
      addShape(shape);
    }
  }

  public MappedShape getModel(String name) {
    for (MappedShape m : shapes) {
      if (m.getName().equals(name)) {
        return m;
      }
    }
    return null;
  }

  public MappedShape getPreviouslySavedShape(String name) {
    for (MappedShape m : previouslySavedShapes) {
      if (m.getName().equals(name)) {
        return m;
      }
    }
    return null;
  }

  public MappedShape addShape(PShape shape) {
    return addShape("default", shape);
  }

  public MappedShape addShape(String name, PShape shape) {
    if (getModel(name) != null) {
      System.out.println("ShapeMapper: Could not add model with name '" + name + "'.");
      System.out.println("ShapeMapper: The model already exists, and model names must be unique.");
      throw new RuntimeException();
    }

    // If we share the model with the client, then when the client renders it, they can
    // update state that will impact our ability to render it. For consistent rendering,
    // make our own private copy.
    PShape shapeCopy = ShapeUtils.createShape(parent, shape);
    MappedShape wrappedShape = new MappedShape(name, parent, shapeCopy);

    MappedShape previouslySaved = getPreviouslySavedShape(name);
    if (previouslySaved != null) {
      System.out.println("ShapeMapper: Found a previously saved model for " + name);
      wrappedShape.setMappingsFromModel(previouslySaved);
    }

    this.shapes.add(wrappedShape);

    currentShapeIndex = shapes.size() - 1;
    currentMappingIndex = 0;
    return wrappedShape;
  }

  private MappedShape getCurrentShape() {
    if (currentShapeIndex >= 0) {
      return this.shapes.get(currentShapeIndex);
    } else {
      return null;
    }
  }

  private Mapping getCurrentMapping() {
    if (currentShapeIndex >= 0 && currentMappingIndex >= 0) {
      return this.shapes.get(currentShapeIndex).getMapping(currentMappingIndex);
    } else {
      return null;
    }
  }

  public void calibrateMode() {
    this.mode = Mode.CALIBRATE;
  }

  public void renderMode() {
    this.mode = Mode.RENDER;
  }

  public Iterable<MappedShape> getShapes() {
    return shapes;
  }

  private void saveCalibration() {
    try {
      String path = parent.dataPath("calibration.ser");
      Files.createDirectories(Paths.get(path).getParent());
      FileOutputStream fileOutputStream = new FileOutputStream(path);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
      objectOutputStream.writeObject(shapes);
      objectOutputStream.flush();
      objectOutputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadCalibration() {
    previouslySavedShapes = new ArrayList<>();
    try {
      FileInputStream fileInputStream = new FileInputStream(parent.dataPath("calibration.ser"));
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      previouslySavedShapes = (List<MappedShape>) objectInputStream.readObject();
      objectInputStream.close();
    } catch (IOException | ClassNotFoundException e) {
      System.out.println("ShapeMapper: Attempted to load calibration data, but it does not exist yet.");
      System.out.println("ShapeMapper: If you have not yet calibrated your projection, this is normal!");
    }
  }

  public void drawDebugInfo() {
    parentGraphics.hint(DISABLE_DEPTH_TEST);

    parentGraphics.fill(0);
    parentGraphics.stroke(255);
    parentGraphics.rectMode(CORNER);
    parentGraphics.rect(0, 0, 300, 120);

    parentGraphics.fill(255);
    parentGraphics.text("Mode: " + mode + " (press SPACE to change)", 10, 20);
    parentGraphics.text("Calibrate mode: " + calibrateMode + " (press TAB to change)", 10, 40);
    parentGraphics.text("Model: " + currentShapeIndex + " (press LEFT/RIGHT to change)", 10, 60);
    parentGraphics.text("Mapping: " + currentMappingIndex + " (press UP/DOWN to change)", 10, 80);
    parentGraphics.text("Press C to create new mapping", 10, 100);
    parentGraphics.text("Press M to switch to face masking mode", 10, 120);

    parentGraphics.hint(ENABLE_DEPTH_TEST);
  }

  public void beginMapping() {
    if (shapes.size() != 1) {
      System.out.println("ShapeMapper: Cannot call ShapeMapper.beginMapping() when you have more than one shape.");
      System.out.println("ShapeMapper: You need to call mappedShape.beginMapping() on each shape separately.");
      throw new RuntimeException();
    }
    shapes.get(0).beginMapping();
  }

  public void endMapping() {
    if (shapes.size() != 1) {
      return;
    }
    shapes.get(0).endMapping();
  }

  private void resetCamera() {
    CameraState state = getCurrentMapping().getCameraState();
    if (state != null) {
      camera.setState(state);
    } else {
      camera.reset();
    }
  }

  // TODO: don't draw selected vertices within this function, factor out
  // for point selection, use 3D point maybe
  // but for calibration, should use 2D because that makes sense
  private void drawShape(PShape shape, PGraphics3D canvas, boolean highlight) {
    canvas.fill(255, 0, 0);
    canvas.noStroke();
    canvas.lights();
    normalShader.set("normalColorStrength", highlight ? 0.5f : 0.25f);
    canvas.shader(normalShader);
    shape.disableStyle();
    shape.draw(canvas);

    for (int j = 0; j < shape.getChildCount(); j++) {
      PShape child = shape.getChild(j);
      PVector avg = new PVector();
      PVector norm = new PVector();
      for (int i = 0; i < child.getVertexCount(); i++) {
        avg.add(child.getVertex(i));
        norm.add(child.getNormal(i));
      }
      avg.div(child.getVertexCount());
      norm.div(child.getVertexCount());

      norm.normalize();
      norm.mult(1);

      canvas.fill(255);
      canvas.push();
      canvas.translate(avg.x, avg.y, avg.z);
      canvas.translate(norm.x, norm.y, norm.z);
      GeometryUtils.rotateToVector(norm, canvas);
      canvas.translate(0, 0, 10);
      canvas.rotate(PI / 2, 1, 0, 0);
      canvas.textAlign(CENTER);
      canvas.scale(0.25f);
      canvas.blendMode(EXCLUSION);
      canvas.textSize(30);
      canvas.text(j, 0, 0);
      canvas.blendMode(BLEND);
      canvas.pop();
    }
  }

  // TODO: break down into smaller utility functions that don't assume so much (i.e. "draw points")
  private void drawVerticesOnShape(PGraphics3D projectionCanvas, PGraphics3D canvas, Set<PVector> vertices) {
    for (PVector modelPoint : vertices) {
      PVector projectedPoint = worldToScreen(modelPoint, projectionCanvas);
      canvas.noStroke();
      canvas.blendMode(EXCLUSION);
      canvas.fill(255);
      canvas.ellipse(projectedPoint.x, projectedPoint.y, UI_CIRCLE_RADIUS, UI_CIRCLE_RADIUS);
      canvas.blendMode(BLEND);
    }
  }

  public void draw() {
    MappedShape currentShape = getCurrentShape();
    Mapping currentMapping = getCurrentMapping();

    try {
      parent.resetShader();
      parent.noLights();

      PVector mouse = new PVector(parent.mouseX, parent.mouseY);
      if (mode == Mode.CALIBRATE) {
        parent.noCursor();
        parent.background(0);

        if (calibrateMode == CalibrateMode.SELECT_POINT) {
          parent.background(0);

          // Only turn peasycam on when in calibrate mode and in model space; otherwise use
          // calibrated camera.
          camera.setActive(true);
          camera.setSuppressRollRotationMode();

          shapeCanvas.beginDraw();
          shapeCanvas.clear();
          shapeCanvas.scale(1, -1, 1);
          drawShape(currentShape.getShape(), shapeCanvas, true);
          shapeCanvas.endDraw();

          parent.resetShader();

          parent.textureMode(NORMAL);
          parent.beginShape();
          parent.texture(shapeCanvas);

          shapeRenderShader.set("time", parent.frameCount);
          parent.shader(shapeRenderShader);

          parent.noStroke();
          parent.vertex(0, 0, 0, 0);
          parent.vertex(parent.width, 0, 1, 0);
          parent.vertex(parent.width, parent.height, 1, 1);
          parent.vertex(0, parent.height, 0, 1);
          parent.endShape();

          PVector closestPoint = currentShape.getClosestPointTo(mouse, shapeCanvas);

          drawVerticesOnShape(shapeCanvas, (PGraphics3D) parent.getGraphics(), currentMapping.getMappedPoints());

          if (closestPoint != null) {
            PVector projectedVertex = worldToScreen(closestPoint, shapeCanvas);
            parent.stroke(255);
            parent.strokeWeight(2);
            parent.noFill();
            parent.ellipse(projectedVertex.x, projectedVertex.y, UI_CIRCLE_RADIUS + 5, UI_CIRCLE_RADIUS + 5);
          }

          if (selectedVertex != null) {
            PVector projectedVertex = worldToScreen(selectedVertex, shapeCanvas);
            drawCrossHairs(projectedVertex.x, projectedVertex.y, parent.color(255));
          }
        } else if (calibrateMode == CalibrateMode.PROJECTION) {
          parent.noCursor();
          parent.background(0);

          camera.setActive(false);

          projectionCanvas.beginDraw();
          projectionCanvas.background(0);
          for (MappedShape shape : shapes) {
            for (Mapping mapping: shape.getMappings()) {
              if (mapping.isReady()) {
                mapping.beginMapping(projectionCanvas);
                drawShape(
                    shape.getShape(),
                    projectionCanvas,
                    shape == currentShape && mapping == currentMapping);
                mapping.endMapping(projectionCanvas, false);

//                drawVerticesOnShape(shapeCanvas, mapping.getMappedPoints());
              }
            }
          }

          projectionCanvas.endDraw();
          parent.image(projectionCanvas, 0, 0);

//          for (PVector modelPoint : currentMapping.getMappedPoints()) {
//            PVector projectedPoint = currentMapping.get(modelPoint);
//            parent.strokeWeight(5);
//
//            parent.noStroke();
//            parent.fill(255, 200);
//            parent.ellipse(projectedPoint.x, projectedPoint.y, UI_CIRCLE_RADIUS, UI_CIRCLE_RADIUS);
//            parent.fill(255);
//            parent.ellipse(projectedPoint.x, projectedPoint.y, 2, 2);
//
//            if (selectedVertex != null && selectedVertex.equals(modelPoint)) {
//              drawCrossHairs(projectedPoint.x, projectedPoint.y, parent.color(255));
//            }
//          }

          PVector closestPoint = currentMapping.getClosestMappedPointTo(mouse);
          if (closestPoint != null) {
            PVector projectedPoint = currentMapping.get(closestPoint);
            parent.stroke(255);
            parent.strokeWeight(2);
            parent.noFill();
            parent.ellipse(projectedPoint.x, projectedPoint.y, UI_CIRCLE_RADIUS + 5, UI_CIRCLE_RADIUS + 5);
          }
        } else if (calibrateMode == CalibrateMode.MASK_FACES) {
          parent.noCursor();
          parent.background(0);

          projectionCanvas.beginDraw();
          projectionCanvas.background(0);
          for (MappedShape shape : shapes) {
            for (Mapping mapping: shape.getMappings()) {
              if (mapping.isReady()) {
                mapping.beginMapping(projectionCanvas);

                // TODO: throw error if you try to turn on masking without subshapes
                // TODO: potentially refactor so that masking is a "tool" inside of projection mode?
                if (shape == currentShape && mapping == currentMapping) {
                  projectionCanvas.lights();
                  projectionCanvas.fill(255);
                  projectionCanvas.stroke(255, 100);
                  projectionCanvas.strokeWeight(1);
                  normalShader.set("normalColorStrength", 0.5f);
                  projectionCanvas.shader(normalShader);
                  shape.draw(projectionCanvas);
                  projectionCanvas.resetShader();
                  projectionCanvas.noLights();

                  projectionCanvas.hint(DISABLE_DEPTH_TEST);
                  projectionCanvas.hint(DISABLE_DEPTH_MASK);
                  projectionCanvas.hint(DISABLE_DEPTH_SORT);

                  projectionCanvas.fill(0, 200);
                  mapping.drawFaceMask(projectionCanvas);

                  int shapeIndex = GeometryUtils.pickFace(
                      shape.getShape(),
                      new PVector(parent.mouseX, parent.mouseY),
                      projectionCanvas);

                  recentlyHoveredSubshapeIndex = shapeIndex;
                  if (shapeIndex >= 0) {
                    projectionCanvas.noFill();
                    PShape subshape = shape.getShape().getChild(shapeIndex);

                    // Draw shape manually to avoid weird state bug with rendering
                    projectionCanvas.beginShape();
                    projectionCanvas.stroke(255);
                    projectionCanvas.strokeWeight(6);
                    for (int i = 0; i < subshape.getVertexCount(); i++) {
                      projectionCanvas.vertex(
                          subshape.getVertexX(i),
                          subshape.getVertexY(i),
                          subshape.getVertexZ(i));
                    }
                    projectionCanvas.endShape(CLOSE);
                  }

                  projectionCanvas.hint(ENABLE_DEPTH_TEST);
                  projectionCanvas.hint(ENABLE_DEPTH_MASK);
                  projectionCanvas.hint(ENABLE_DEPTH_SORT);
                } else {
                  projectionCanvas.fill(0);
                  projectionCanvas.stroke(50);
                  projectionCanvas.strokeWeight(2);
                  shape.draw(projectionCanvas);
                }

                mapping.endMapping(projectionCanvas, false);
              }
            }
          }
          projectionCanvas.endDraw();
          parent.image(projectionCanvas, 0, 0);
        }

        // Draw mouse cross-hairs
        parent.blendMode(EXCLUSION);
        drawCrossHairs(parent.mouseX, parent.mouseY, parent.color(255));
        parent.blendMode(BLEND);
        drawDebugInfo();
      } else if (mode == Mode.RENDER) {
        camera.setActive(false);
        parent.cursor();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

    parentGraphics.push();
    parentGraphics.translate(20, 20);
    parentGraphics.pop();
  }

  private void drawCrossHairs(float x, float y, int color) {
    parent.stroke(color, 255);
    parent.strokeWeight(2);
    parent.push();
    parent.translate(x, y);
    float size = 40;
    parent.line(-size, 0, size, 0);
    parent.line(0, -size, 0, size);
    parent.noFill();
    parent.ellipseMode(CENTER);
    parent.ellipse(0, 0, UI_CIRCLE_RADIUS, UI_CIRCLE_RADIUS);
    parent.pop();
  }

  public void mouseEvent(MouseEvent event) {
    MappedShape shape = getCurrentShape();
    Mapping mapping = getCurrentMapping();

    PVector mouse = new PVector(event.getX(), event.getY());

    if (mode == Mode.CALIBRATE) {
      if (calibrateMode == CalibrateMode.SELECT_POINT) {
        if (event.getAction() == MouseEvent.CLICK) {
          selectedVertex = shape.getClosestPointTo(mouse, shapeCanvas);
        }

        getCurrentMapping().setCameraState(camera.getState());
      } else if (calibrateMode == CalibrateMode.PROJECTION) {
        switch (event.getAction()) {
          case MouseEvent.PRESS:
            PVector newSelection = mapping.getClosestMappedPointTo(mouse);
            if (newSelection != null) {
              selectedVertex = newSelection;
            }
            break;
          case MouseEvent.DRAG:
          case MouseEvent.CLICK:
            if (selectedVertex != null) {
              mapping.put(selectedVertex, mouse);
              saveCalibration();
            }
            break;
        }
      } else if (calibrateMode == CalibrateMode.MASK_FACES) {
        if (event.getAction() == MouseEvent.CLICK) {
          if (recentlyHoveredSubshapeIndex >= 0) {
            mapping.setFaceMask(
                recentlyHoveredSubshapeIndex,
                !mapping.getFaceMask(recentlyHoveredSubshapeIndex));
            saveCalibration();
          }
        }
      }
    }
  }

  public void keyEvent(KeyEvent event) {
    if (event.getAction() == KeyEvent.PRESS) {
      if (event.getKeyCode() == 32) { // space
        mode = (mode == Mode.CALIBRATE) ? Mode.RENDER : Mode.CALIBRATE;
        resetCamera();
      } else if (event.getKeyCode() == 9) { // tab
        calibrateMode = (calibrateMode == CalibrateMode.SELECT_POINT)
            ? CalibrateMode.PROJECTION
            : CalibrateMode.SELECT_POINT;
        resetCamera();
      } else if (event.getKey() == 'm') {
        calibrateMode = (calibrateMode == CalibrateMode.PROJECTION) ? CalibrateMode.MASK_FACES : CalibrateMode.PROJECTION;
        resetCamera();
      } else if (event.getKeyCode() == 37) { // left
        currentShapeIndex = (currentShapeIndex + 1) % shapes.size();
        currentMappingIndex = 0;
        selectedVertex = null;
        resetCamera();
      } else if (event.getKeyCode() == 39) { // right
        currentShapeIndex = ((currentShapeIndex - 1) + shapes.size()) % shapes.size();
        currentMappingIndex = 0;
        selectedVertex = null;
        resetCamera();
      } else if (event.getKeyCode() == 38) { // up
        int totalMappings = getCurrentShape().getNumMappings();
        currentMappingIndex = (currentMappingIndex + 1) % totalMappings;
        //selectedVertex = null;
        resetCamera();
      } else if (event.getKeyCode() == 40) { // down
        int totalMappings = getCurrentShape().getNumMappings();
        currentMappingIndex = ((currentMappingIndex - 1) + totalMappings) % totalMappings;
        //selectedVertex = null;
        resetCamera();
      } else if (event.getKey() == 'c') {
        MappedShape current = getCurrentShape();
        current.createMapping();
        currentMappingIndex = current.getNumMappings() - 1;
        selectedVertex = null;
        resetCamera();
      }
    }
  }
}
