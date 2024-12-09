package spacefiller.shapemapper.examples;

import processing.core.PApplet;
import processing.core.PShape;
import spacefiller.shapemapper.MappedShape;
import spacefiller.shapemapper.ShapeMapper;

public class Test extends PApplet {
  MappedShape mappedShape;
  ShapeMapper mapper;
  PShape shape;

  public static void main(String[] args) {
    main(Test.class);
  }

  @Override
  public void settings() {
    fullScreen(P3D);
  }

  @Override
  public void setup() {
    shape = loadShape("models/icosahedron.obj");
    mapper = new ShapeMapper(this, shape);
  }

  @Override
  public void draw() {
    background(0);

    push();
    translate(width / 2, height / 2);
    lights();
    shape(shape);
    pop();
  }
}
