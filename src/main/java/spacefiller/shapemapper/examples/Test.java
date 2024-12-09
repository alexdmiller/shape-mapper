package spacefiller.shapemapper.examples;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphics3D;
import spacefiller.shapemapper.GeometryUtils;
import spacefiller.shapemapper.MappedShape;
import spacefiller.shapemapper.Mapping;
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
    mapper = new ShapeMapper(this);

    mappedShape = mapper.addShape(shape);
//    mappedShape.getMapping(0).getMappedPoints();
  }

  @Override
  public void draw() {
    background(0);

    for (Mapping mapping : mappedShape.getMappings()) {
      mapping.beginMapping();
      noStroke();
      fill(255);
      shape.disableStyle();
      pointLight(
          0,
          255,
          255,
          sin(frameCount / 10f) * 500,
          cos(frameCount / 10f) * 500,
          sin(frameCount / 10f) * 500);
      shape(shape);
      mapping.endMapping();
    }
  }

  public void keyPressed() {
    if (key == 'p') {
      mapper.shapeCanvas.beginDraw();
      mapper.shapeCanvas.push();
      mapper.shapeCanvas.translate(-200, 0);
      mapper.shapeCanvas.scale(0.2f, -0.2f);

      int c = 0;
      Mapping mapping = mappedShape.getMapping(0);
      done:
      for (PShape child : shape.getChildren()) {
        for (int i = 0; i < child.getVertexCount(); i++) {
          PVector v = child.getVertex(i);
          PVector projected = GeometryUtils.worldToScreen(v, mapper.shapeCanvas);
          mapping.put(v, projected);
          c++;
        }
      }
      mapper.shapeCanvas.pop();
      mapper.shapeCanvas.endDraw();

    }
  }
}
