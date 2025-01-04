package spacefiller.shapemapper.examples;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphics3D;
import spacefiller.shapemapper.GeometryUtils;
import spacefiller.shapemapper.MappedShape;
import spacefiller.shapemapper.Mapping;
import spacefiller.shapemapper.ShapeMapper;

public class MultipleShapes extends PApplet {
  MappedShape mappedShape;
  MappedShape mappedRect;
  ShapeMapper mapper;
  PShape shape;

  public static void main(String[] args) {
    main(MultipleShapes.class);
  }

  @Override
  public void settings() {
    fullScreen(P3D);
  }

  @Override
  public void setup() {
    shape = loadShape("models/pyramid.obj");
    shape.disableStyle();
    mapper = new ShapeMapper(this);

    mapper.addShape("pyramid1", shape);
    mapper.addShape("pyramid2", shape);
    mapper.addShape("pyramid3", shape);
  }

  @Override
  public void draw() {
    background(0);

    int i = 0;
    for (MappedShape ms : mapper.getShapes()) {
      ms.beginMapping();

      for (PShape child : shape.getChildren()) {
        fill(255 * sin(frameCount / 50f + i / 4f));
        noStroke();
        shape(child);
        i++;
      }

      ms.endMapping();
      i++;
    }
  }
}
