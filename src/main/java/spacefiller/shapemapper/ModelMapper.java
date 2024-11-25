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

import static processing.core.PConstants.*;
import static spacefiller.shapemapper.GeometryUtils.*;

public class ModelMapper {
  private static final float UI_CIRCLE_RADIUS = 10;

  private enum Mode {
    CALIBRATE, RENDER
  }

  private enum CalibrateMode {
    SELECT_POINT, PROJECTION
  }

  private PApplet parent;

  private PGraphics3D parentGraphics;
  private PGraphics3D modelCanvas;
  private PGraphics3D projectionCanvas;

  private List<Model> models;
  private List<Model> previouslySavedModels;
  private int currentModelIndex = -1;
  private int currentMappingIndex = -1;

  private Mode mode;
  private CalibrateMode calibrateMode;
  private PeasyCam camera;

  private PVector selectedVertex;

  PShader modelRenderShader;

  // UI images
  PImage uiModel;
  PImage uiProjection;
  PImage uiNoCalibration;
  PImage uiPressSpace;
  private int uiPressSpaceCountdown;

  // TODO: add constructor that accepts a model

  public ModelMapper(PApplet parent) {
    try {
      this.parent = parent;
      try {
        this.parentGraphics = (PGraphics3D) parent.getGraphics();
      } catch (ClassCastException e) {
        System.out.println("ModelMapper: Must use P3D rendering mode with ModelMapper library");
        System.out.println("ModelMapper:   size(P3D, 500, 500)");
      }
      this.modelCanvas = (PGraphics3D) parent.createGraphics(parent.width, parent.height, P3D);
      this.projectionCanvas = (PGraphics3D) parent.createGraphics(parent.width, parent.height, P3D);
      this.mode = Mode.RENDER;
      this.calibrateMode = CalibrateMode.SELECT_POINT;
      this.camera = new PeasyCam(parent, modelCanvas, 400);

      this.parent.registerMethod("draw", this);
      this.parent.registerMethod("mouseEvent", this);
      this.parent.registerMethod("keyEvent", this);

      modelRenderShader = parent.loadShader(IOUtils.extractResourceToFile("/model.frag.glsl"));
      uiModel = parent.loadImage(IOUtils.extractResourceToFile("/ui-model.png"));
      uiProjection = parent.loadImage(IOUtils.extractResourceToFile("/ui-projection.png"));
      uiNoCalibration = parent.loadImage(IOUtils.extractResourceToFile("/no-calibration.png"));
      uiPressSpace = parent.loadImage(IOUtils.extractResourceToFile("/press-space.png"));
      uiPressSpaceCountdown = 1000;

      this.models = new ArrayList<>();
      loadCalibration();

//      calibrationData = CalibrationUtils.calibrate(pointMapping, parent.width, parent.height);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  public Model getModel(String name) {
    for (Model m : models) {
      if (m.getName().equals(name)) {
        return m;
      }
    }
    return null;
  }

  public Model getPreviouslySavedModel(String name) {
    for (Model m : previouslySavedModels) {
      if (m.getName().equals(name)) {
        return m;
      }
    }
    return null;
  }

  public void addModel(PShape shape) {
    addModel("default", shape);
  }

  public void addModel(String name, PShape shape) {
    if (getModel(name) != null) {
      System.out.println("ModelMapper: Could not add model with name '" + name + "'.");
      System.out.println("ModelMapper: The model already exists, and model names must be unique.");
      return;
    }

    // If we share the model with the client, then when the client renders it, they can
    // update state that will impact our ability to render it. For consistent rendering,
    // make our own private copy.
    PShape shapeCopy = ShapeUtils.createShape(parent, shape);
    Model model = new Model(name, parent, shapeCopy);

    Model previouslySaved = getPreviouslySavedModel(name);
    if (previouslySaved != null) {
      System.out.println("ModelMapper: Found a previously saved model for " + name);
      model.setMappingsFromModel(previouslySaved);
    }

    this.models.add(model);

    currentModelIndex = models.size() - 1;
    currentMappingIndex = 0;
  }

  public Model getCurrentModel() {
    if (currentModelIndex >= 0) {
      return this.models.get(currentModelIndex);
    } else {
      return null;
    }
  }

  public Mapping getCurrentMapping() {
    if (currentModelIndex >= 0 && currentMappingIndex >= 0) {
      return this.models.get(currentModelIndex).getMapping(currentMappingIndex);
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

  private void drawModel(PShape model, PGraphics canvas) {
    canvas.resetShader();

    model.disableStyle();

    canvas.fill(0);
    canvas.stroke(255);
    canvas.strokeWeight(2);
    canvas.shape(model);
    canvas.endDraw();
  }

  public Iterable<Model> getModels() {
    return models;
  }

  private void saveCalibration() {
    try {
      String path = parent.dataPath("calibration.ser");
      Files.createDirectories(Paths.get(path).getParent());
      FileOutputStream fileOutputStream = new FileOutputStream(path);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
      objectOutputStream.writeObject(models);
      objectOutputStream.flush();
      objectOutputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadCalibration() {
    previouslySavedModels = new ArrayList<>();
    try {
      FileInputStream fileInputStream = new FileInputStream(parent.dataPath("calibration.ser"));
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      previouslySavedModels = (List<Model>) objectInputStream.readObject();
      objectInputStream.close();
    } catch (IOException | ClassNotFoundException e) {
      System.out.println("ModelMapper: Attempted to load calibration data, but it does not exist yet.");
      System.out.println("ModelMapper: If you have not yet calibrated your projection, this is normal!");
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
    parentGraphics.text("Model: " + currentModelIndex + " (press LEFT/RIGHT to change)", 10, 60);
    parentGraphics.text("Mapping: " + currentMappingIndex + " (press UP/DOWN to change)", 10, 80);
    parentGraphics.text("Press C to create new mapping", 10, 100);

    parentGraphics.hint(ENABLE_DEPTH_TEST);
  }

  /**
   * Processing hooks
   */
  public void draw() {
    Model currentModel = getCurrentModel();
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

          modelCanvas.beginDraw();
          modelCanvas.clear();
          modelCanvas.scale(1, -1, 1);

          modelCanvas.fill(25);
          modelCanvas.stroke(255);
          modelCanvas.strokeWeight(2);

          currentModel.draw(modelCanvas);

          modelCanvas.endDraw();

          parent.resetShader();

          parent.textureMode(NORMAL);
          parent.beginShape();
          parent.texture(modelCanvas);

          modelRenderShader.set("time", parent.frameCount);
          parent.shader(modelRenderShader);

          parent.noStroke();
          parent.vertex(0, 0, 0, 0);
          parent.vertex(parent.width, 0, 1, 0);
          parent.vertex(parent.width, parent.height, 1, 1);
          parent.vertex(0, parent.height, 0, 1);
          parent.endShape();

          PVector closestPoint = currentModel.getClosestPointTo(mouse, modelCanvas);

          for (PVector modelPoint : currentMapping.getMappedPoints()) {
            PVector projectedPoint = worldToScreen(modelPoint, modelCanvas);
            parent.noStroke();
            parent.fill(255, 200);
            parent.ellipse(projectedPoint.x, projectedPoint.y, UI_CIRCLE_RADIUS, UI_CIRCLE_RADIUS);
          }

          if (closestPoint != null) {
            PVector projectedVertex = worldToScreen(closestPoint, modelCanvas);
            parent.stroke(255);
            parent.strokeWeight(2);
            parent.noFill();
            parent.ellipse(projectedVertex.x, projectedVertex.y, UI_CIRCLE_RADIUS + 5, UI_CIRCLE_RADIUS + 5);
          }

          if (selectedVertex != null) {
            PVector projectedVertex = worldToScreen(selectedVertex, modelCanvas);
            drawCrossHairs(projectedVertex.x, projectedVertex.y, parent.color(255, 0, 255));
          }

          parent.image(
              uiModel,
              20,
              parent.height - uiModel.height / 2f - 20,
              uiModel.width / 2f,
              uiModel.height / 2f);
        } else if (calibrateMode == CalibrateMode.PROJECTION) {
          camera.setActive(false);

          for (Model model : models) {
            for (Mapping mapping: model.getMappings()) {
              if (mapping.isReady()) {
                mapping.begin(projectionCanvas);

                if (model == currentModel && mapping == currentMapping) {
                  projectionCanvas.fill(25);
                  projectionCanvas.stroke(255);
                  projectionCanvas.strokeWeight(2);
                } else {
                  projectionCanvas.fill(0);
                  projectionCanvas.stroke(50);
                  projectionCanvas.strokeWeight(2);
                }

                model.draw(projectionCanvas);
                mapping.end(projectionCanvas);
              } else {
//                parent.image(
//                    uiNoCalibration,
//                    parent.width / 2f - uiNoCalibration.width / 4f,
//                    parent.height / 2f - uiNoCalibration.height / 4f,
//                    uiNoCalibration.width / 2f,
//                    uiNoCalibration.height / 2f);
              }
            }
          }


          parent.image(projectionCanvas, 0, 0);

          for (PVector modelPoint : currentMapping.getMappedPoints()) {
            PVector projectedPoint = currentMapping.get(modelPoint);
            parent.strokeWeight(5);

            parent.noStroke();
            parent.fill(255, 200);
            parent.ellipse(projectedPoint.x, projectedPoint.y, UI_CIRCLE_RADIUS, UI_CIRCLE_RADIUS);
            parent.fill(255);
            parent.ellipse(projectedPoint.x, projectedPoint.y, 2, 2);

            if (selectedVertex != null && selectedVertex.equals(modelPoint)) {
              drawCrossHairs(projectedPoint.x, projectedPoint.y, parent.color(0, 255, 255));
            }
          }


          PVector closestPoint = currentMapping.getClosestMappedPointTo(mouse);
          if (closestPoint != null) {
            PVector projectedPoint = currentMapping.get(closestPoint);
            parent.stroke(255);
            parent.strokeWeight(2);
            parent.noFill();
            parent.ellipse(projectedPoint.x, projectedPoint.y, UI_CIRCLE_RADIUS + 5, UI_CIRCLE_RADIUS + 5);
          }

          parent.image(
            uiProjection,
            20,
            parent.height - uiProjection.height / 2f - 20,
            uiProjection.width / 2f,
            uiProjection.height / 2f);
        }

        // Draw mouse cross-hairs
        drawCrossHairs(parent.mouseX, parent.mouseY, parent.color(255));
      } else if (mode == Mode.RENDER) {
        camera.setActive(false);
        parent.cursor();

        // Show UI hint for switching to calibration mode on a countdown timer; hide it
        // after `uiPressSpaceCountdown` frames have passed. `uiPressSpaceCountdown` is
        // set when the sketch is initialized and whenever the user switches from
        // calibration mode to render mode.
        if (uiPressSpaceCountdown > 0) {
          parent.hint(DISABLE_DEPTH_TEST);
          parent.rectMode(CENTER);
          if (uiPressSpaceCountdown < 100){
            parent.tint(255, uiPressSpaceCountdown / 100f * 255);
          }
          parent.image(
              uiPressSpace,
              20,
              parent.height - uiPressSpace.height / 2f - 20,
              uiPressSpace.width / 2f,
              uiPressSpace.height / 2f);
          parent.hint(ENABLE_DEPTH_TEST);
          parent.tint(255, 255);
          uiPressSpaceCountdown--;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

    parentGraphics.push();
    parentGraphics.translate(20, 20);
    drawDebugInfo();
    parentGraphics.pop();
  }

  private void drawCrossHairs(float x, float y, int color) {
    parent.stroke(color, 150);
    parent.strokeWeight(2);
    parent.line(0, y, parent.width, y);
    parent.line(x, 0, x, parent.height);
    parent.noStroke();
    parent.fill(color);
    parent.ellipseMode(CENTER);
    parent.ellipse(x, y, UI_CIRCLE_RADIUS, UI_CIRCLE_RADIUS);
  }

  public void mouseEvent(MouseEvent event) {
    Model model = getCurrentModel();
    Mapping mapping = getCurrentMapping();

    PVector mouse = new PVector(event.getX(), event.getY());

    if (mode != Mode.CALIBRATE) {
      // Library only responds to mouse input when in calibrate mode
      return;
    }

    if (calibrateMode == CalibrateMode.SELECT_POINT) {
      if (event.getAction() == MouseEvent.CLICK) {
        selectedVertex = model.getClosestPointTo(mouse, modelCanvas);
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
    }
  }

  private void resetCamera() {
    CameraState state = getCurrentMapping().getCameraState();
    if (state != null) {
      camera.setState(state);
    } else {
      camera.reset();
    }
  }

  public void keyEvent(KeyEvent event) {
    if (event.getAction() == KeyEvent.PRESS) {
      if (event.getKeyCode() == 32) { // space
        uiPressSpaceCountdown = 300;
        mode = (mode == Mode.CALIBRATE) ? Mode.RENDER : Mode.CALIBRATE;
        resetCamera();
      } else if (event.getKeyCode() == 9) { // tab
        calibrateMode = (calibrateMode == CalibrateMode.SELECT_POINT)
            ? CalibrateMode.PROJECTION
            : CalibrateMode.SELECT_POINT;
        resetCamera();
      } else if (event.getKeyCode() == 37) { // left
        currentModelIndex = (currentModelIndex + 1) % models.size();
        currentMappingIndex = 0;
        selectedVertex = null;
        resetCamera();
      } else if (event.getKeyCode() == 39) { // right
        currentModelIndex = ((currentModelIndex - 1) + models.size()) % models.size();
        currentMappingIndex = 0;
        selectedVertex = null;
        resetCamera();
      } else if (event.getKeyCode() == 38) { // up
        int totalMappings = getCurrentModel().getNumMappings();
        currentMappingIndex = (currentMappingIndex + 1) % totalMappings;
        selectedVertex = null;
        resetCamera();
      } else if (event.getKeyCode() == 40) { // down
        int totalMappings = getCurrentModel().getNumMappings();
        currentMappingIndex = ((currentMappingIndex - 1) + totalMappings) % totalMappings;
        selectedVertex = null;
        resetCamera();
      } else if (event.getKey() == 'c') {
        Model current = getCurrentModel();
        current.createMapping();
        currentMappingIndex = current.getNumMappings() - 1;
        selectedVertex = null;
        resetCamera();
      }
    }
  }
}
