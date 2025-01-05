package spacefiller.shapemapper.examples;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphics3D;
import spacefiller.shapemapper.GeometryUtils;
import spacefiller.shapemapper.MappedShape;
import spacefiller.shapemapper.Mapping;
import spacefiller.shapemapper.ShapeMapper;

public class MultipleProjectors extends PApplet {
  ShapeMapper mapper;
  PShape myShape;
  MappedShape mappedShape;

  public static void main(String[] args) {
    main(MultipleProjectors.class);
  }

  @Override
  public void settings() {
    fullScreen(P3D);
  }

  @Override
  public void setup() {
    fullScreen(P3D);
    myShape = createShape(BOX, 150);
    mapper = new ShapeMapper(this);
    mappedShape = mapper.addShape("box", myShape, 2);
  }

  @Override
  public void draw() {
    background(0);

    for (Mapping mapping : mappedShape.getMappings()) {
      mapping.beginMapping();
      noLights();
      pointLight(
          0,
          255,
          255,
          cos(frameCount / 10f) * 300,
          sin(frameCount / 10f) * 300,
          cos(frameCount / 20f) * 300);
      noStroke();
      shape(myShape);
      mapping.endMapping();
    }
  }
}
