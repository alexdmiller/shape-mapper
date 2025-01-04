package spacefiller.shapemapper.examples;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphics3D;
import spacefiller.shapemapper.GeometryUtils;
import spacefiller.shapemapper.MappedShape;
import spacefiller.shapemapper.Mapping;
import spacefiller.shapemapper.ShapeMapper;

public class SingleShape extends PApplet {
  MappedShape mappedShape;
  MappedShape mappedRect;
  ShapeMapper mapper;
  PShape shape;

  public static void main(String[] args) {
    main(SingleShape.class);
  }

  @Override
  public void settings() {
    fullScreen(P3D);
  }

  @Override
  public void setup() {
    shape = loadShape("models/steps.obj");
    shape.disableStyle();
    mapper = new ShapeMapper(this, shape);
  }

  @Override
  public void draw() {
    background(0);

    mapper.beginMapping();
    stroke(255);
    fill(0);
    strokeWeight(2);
    shape(shape);
    mapper.endMapping();
  }
}
