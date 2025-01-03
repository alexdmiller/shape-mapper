package spacefiller.shapemapper;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphics3D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static spacefiller.shapemapper.GeometryUtils.getClosestPointOnShape;

public class MappedShape implements Serializable {
  private transient PApplet parent;
  private transient PGraphics3D parentGraphics;
  private transient PShape shape;
  private transient PShape internalCopy;

  private String name;
  private List<Mapping> mappings;

  public MappedShape(String name, PApplet parent, PShape shape) {
    this.name = name;
    this.parent = parent;

    try {
      this.parentGraphics = (PGraphics3D) parent.getGraphics();
    } catch (ClassCastException e) {
      System.out.println("ModelMapper: Must use P3D rendering mode with ModelMapper library. Example:");
      System.out.println("ModelMapper:   size(500, 500, P3D)");
    }

    // Make a clean copy of the shape so that client modifications don't trickle down
    // into this shape.
    this.shape = ShapeUtils.createShape(parent, shape);

    // For some reason, drawing `this.shape` changes its state such that the user can no
    // longer call shape(...) on it. We draw a debug copy instead to keep it isolated.
    this.internalCopy = ShapeUtils.createShape(parent, shape);

    this.mappings = new ArrayList<>();
  }

  protected void setMappingsFromModel(MappedShape from) {
    mappings = new ArrayList<>();
    for (Mapping otherMapping : from.getMappings()) {
      Mapping m = new Mapping(parent, parentGraphics, internalCopy);
      m.setFromOtherMapping(otherMapping);
      mappings.add(m);
    }
  }

  public void createMapping() {
    Mapping m = new Mapping(parent, parentGraphics, internalCopy);
    mappings.add(m);
  }

  public List<Mapping> getMappings() {
    return mappings;
  }

  public Mapping getMapping(int index) {
    return mappings.get(index);
  }

  public int getNumMappings() {
    return mappings.size();
  }

  public PShape getShape() {
    return shape;
  }

  public void draw(PGraphics3D canvas) {
    this.internalCopy.disableStyle();
    canvas.shape(this.internalCopy);
  }

  public PVector getClosestPointTo(PVector mouse, PGraphics3D modelCanvas) {
    return getClosestPointOnShape(mouse, shape, modelCanvas);
  }

  public String getName() {
    return name;
  }

  public void beginMapping() {
    if (mappings.size() != 1) {
      System.out.println("ShapeMapper: Cannot call beginMapping() on shape that has more than one mapping.");
      System.out.println("ShapeMapper: Shape '" + name + "' has " + mappings.size() + " mappings");
      System.out.println("ShapeMapper: You need to loop over all mappings and draw them separately.");
      throw new RuntimeException();
    }
    mappings.get(0).beginMapping();
  }

  public void endMapping() {
    if (mappings.size() != 1) {
      return;
    }
    mappings.get(0).endMapping();
  }
}
