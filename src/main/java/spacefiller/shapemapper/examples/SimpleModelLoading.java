package spacefiller.shapemapper.examples;

import processing.core.PApplet;
import processing.core.PShape;
import spacefiller.shapemapper.ShapeMapper;

public class SimpleModelLoading extends PApplet {
  ShapeMapper mapper;
  PShape shape;

  public static void main(String[] args) {
    main(SimpleModelLoading.class);
  }

  @Override
  public void settings() {
    fullScreen(P3D);
  }

  @Override
  public void setup() {
    shape = loadShape("models/steps.obj");
    mapper = new ShapeMapper(this, shape);
  }

  @Override
  public void draw() {
    background(0);
    mapper.beginMapping();
    pointLight(
        255, 0, 0,
        sin(frameCount / 10f) * 400,
        cos(frameCount / 10f) * 400,
        sin(frameCount / 10f) * 400);
    shape(shape);
    mapper.endMapping();
  }
}
