package spacefiller.shapemapper;

import processing.core.PMatrix3D;

import java.io.Serializable;

// A simple data only class that represents calibration parameters which can be applied
// to a graphics context in order to achieve projection mapping alignment.
public class GraphicsTransform implements Serializable {
  public PMatrix3D projectionMatrix;
  public PMatrix3D modelViewMatrix;

  public GraphicsTransform(PMatrix3D projectionMatrix, PMatrix3D modelViewMatrix) {
    this.projectionMatrix = projectionMatrix;
    this.modelViewMatrix = modelViewMatrix;
  }

  public GraphicsTransform() {
  }

  public static GraphicsTransform empty() {
    return new GraphicsTransform();
  }

  public boolean isReady() {
    return projectionMatrix != null && modelViewMatrix != null;
  }
}