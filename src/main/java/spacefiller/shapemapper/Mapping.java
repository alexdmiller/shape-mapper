package spacefiller.shapemapper;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphics3D;
import spacefiller.peasy.CameraState;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static spacefiller.shapemapper.GeometryUtils.getClosestPointByMappedPoint;

public class Mapping implements Serializable {
  private transient PGraphics3D parentGraphics;
  private transient PApplet parent;
  private transient GraphicsTransform transform;
  private transient CameraState cameraState;
  private transient PShape shape;

  private Map<PVector, PVector> points;
  private Map<Integer, Boolean> faceMask;

  // TODO: this class should also include a projection bounds
  // when calibrating and drawing, all points should be relative to the projection bounds

  public Mapping(PApplet parent, PGraphics3D parentGraphics, PShape shape) {
    this.parent = parent;
    this.parentGraphics = parentGraphics;
    this.points = new HashMap<>();
    this.transform = new GraphicsTransform();
    this.faceMask = new HashMap<>();
    this.shape = shape;

    for (int i = 0; i < shape.getChildCount(); i++) {
      faceMask.put(i, false);
    }
  }

  public void put(PVector from, PVector to) {
    this.points.put(from, to);
    computeTransform();
  }

  public void computeTransform() {
    transform = CalibrationUtils.calibrate(
        this.points, parentGraphics.width, parentGraphics.height);
  }

  public void remove(PVector from) {
    this.points.remove(from);
    transform = CalibrationUtils.calibrate(
        this.points, parentGraphics.width, parentGraphics.height);
  }

  public Set<PVector> getMappedPoints() {
    return points.keySet();
  }

  public PVector get(PVector key) {
    return points.get(key);
  }

  public PVector getClosestMappedPointTo(PVector query) {
    return getClosestPointByMappedPoint(query, points);
  }

  public boolean isReady() {
    return transform.isReady();
  }

  public void beginMapping() {
    beginMapping(parentGraphics);
  }

  public void endMapping() {
    endMapping(parentGraphics);
  }

  public void beginMapping(PGraphics3D graphics) {
    if (isReady()) {
      graphics.pushMatrix();
      graphics.pushProjection();

      graphics.resetMatrix();
      graphics.setProjection(transform.projectionMatrix);
      graphics.camera(0, 0, 0, 0, 0, 1, 0, -1, 0);
      graphics.applyMatrix(transform.modelViewMatrix);
    }
  }

  public void endMapping(PGraphics3D graphics) {
    endMapping(graphics, true);
  }

  public void endMapping(PGraphics3D graphics, boolean drawFaceMask) {
    if (isReady()) {
      if (drawFaceMask) {
        graphics.fill(0);
        graphics.stroke(0);
        drawFaceMask(graphics);
      }
      graphics.popMatrix();
      graphics.popProjection();
    }
  }

  public void setFromOtherMapping(Mapping otherMapping) {
    // TODO: need to copy points?
    this.points = otherMapping.points;
    this.faceMask = otherMapping.faceMask;
    computeTransform();
  }

  public void setCameraState(CameraState state) {
    this.cameraState = state;
  }

  public CameraState getCameraState() {
    return this.cameraState;
  }

  public void setFaceMask(int faceIndex, boolean value) {
    faceMask.put(faceIndex, value);
  }

  public boolean getFaceMask(int faceIndex) {
    return faceMask.get(faceIndex);
  }

  public void drawFaceMask(PGraphics canvas) {
    for (Integer shapeIndex : faceMask.keySet()) {
      PShape subshape = shape.getChild(shapeIndex);
      subshape.disableStyle();
      if (faceMask.get(shapeIndex)) {
        canvas.shape(subshape);
      }
    }
  }

  public void clear() {
    points.clear();
  }
}
