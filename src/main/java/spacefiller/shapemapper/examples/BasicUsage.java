package spacefiller.shapemapper.examples;

import processing.core.PApplet;
import spacefiller.shapemapper.ShapeMapper;
import processing.core.PShape;

public class BasicUsage extends PApplet {
  ShapeMapper mapper;
  PShape shape;

  public static void main(String[] args) {
    main(BasicUsage.class);
  }

  @Override
  public void settings() {
    fullScreen(P3D);
  }

  @Override
  public void setup() {
    shape = createShape(BOX, 100, 200, 300);
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
    pointLight(
        0, 0, 255,
        cos(frameCount / 10f) * 400,
        sin(frameCount / 10f) * 400,
        sin(frameCount / 10f) * 400);
    shape(shape);
    mapper.endMapping();
  }
}
