package spacefiller.shapemapper.examples;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphics3D;
import spacefiller.shapemapper.GeometryUtils;
import spacefiller.shapemapper.MappedShape;
import spacefiller.shapemapper.Mapping;
import spacefiller.shapemapper.ShapeMapper;

public class MultipleShapesMultipleProjectors extends PApplet {
  ShapeMapper mapper;
  PShape box;
  PShape sphere;
  MappedShape mappedBox;
  MappedShape mappedSphere;

  public static void main(String[] args) {
    main(MultipleShapesMultipleProjectors.class);
  }

  @Override
  public void settings() {
    fullScreen(P3D);
  }

  @Override
  public void setup() {
    fullScreen(P3D);
    box = createShape(BOX, 150);
    sphereDetail(8);
    sphere = createShape(SPHERE, 150);

    mapper = new ShapeMapper(this);
    mappedBox = mapper.addShape("box", box, 2);
    mappedSphere = mapper.addShape("sphere", sphere, 2);
  }

  @Override
  public void draw() {
    background(0);

    for (Mapping mapping : mappedBox.getMappings()) {
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
      shape(box);
      mapping.endMapping();
    }

    for (Mapping mapping : mappedSphere.getMappings()) {
      mapping.beginMapping();
      noLights();
      pointLight(
          255,
          255,
          0,
          cos(frameCount / 10f) * 300,
          sin(frameCount / 10f) * 300,
          cos(frameCount / 20f) * 300);
      noStroke();
      shape(sphere);
      mapping.endMapping();
    }
  }
}
