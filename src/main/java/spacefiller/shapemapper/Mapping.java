package spacefiller.shapemapper;

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
  private transient GraphicsTransform transform;
  private transient CameraState cameraState;

  private Map<PVector, PVector> points;

  public Mapping(PGraphics3D parentGraphics) {
    this.parentGraphics = parentGraphics;
    this.points = new HashMap<>();
    this.transform = new GraphicsTransform();
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


  public void begin() {
    begin(parentGraphics);
  }

  public void end() {
    end(parentGraphics);
  }

  public void begin(PGraphics3D graphics) {
    if (isReady()) {
      graphics.pushMatrix();
      graphics.pushProjection();

      graphics.resetMatrix();
      graphics.setProjection(transform.projectionMatrix);
      graphics.camera(0, 0, 0, 0, 0, 1, 0, -1, 0);
      graphics.applyMatrix(transform.modelViewMatrix);
    }
  }

  public void end(PGraphics3D graphics) {
    if (isReady()) {
      graphics.popMatrix();
      graphics.popProjection();
    }
  }

  public void setFromOtherMapping(Mapping otherMapping) {
    // TODO: need to copy points?
    this.points = otherMapping.points;
    computeTransform();
  }

  public void setCameraState(CameraState state) {
    this.cameraState = state;
  }

  public CameraState getCameraState() {
    return this.cameraState;
  }
}
