package spacefiller.shapemapper;

import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PGraphics3D;

import static processing.core.PConstants.P3D;

public class Projector {
  private transient PGraphics3D canvas;
  private transient PApplet parent;

  private PVector position;
  private int width;
  private int height;

  public Projector(PApplet parent, int x, int y, int width, int height) {
    this.parent = parent;
    this.position = new PVector(x, y);
    this.width = width;
    this.height = height;
    this.canvas = (PGraphics3D) parent.createGraphics(x, y, P3D);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void beginDraw() {
    this.canvas.beginDraw();
  }

  public void endDraw() {
    this.canvas.endDraw();
  }

  public PVector getPosition() {
    return position;
  }
}

/*
ShapeMapper mapper = new ShapeMapper(this);
MappedShape mappedShape = mapper.createShape(shape);
Projector projector = mapper.createProjector(x, y, width, height);
mappedShape.createMapping(projector);
*/
