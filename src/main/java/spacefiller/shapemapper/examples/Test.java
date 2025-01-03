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
  MappedShape mappedRect;
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

    PShape daddy = new PShape();
    daddy.addChild(createShape(RECT, 0, 0, 500, 500));
    daddy.addChild(createShape(RECT, 0, 500, 500, 500));
    daddy.addChild(createShape(RECT, 500, 0, 500, 500));
    daddy.addChild(createShape(RECT, 500, 500, 500, 500));

    mappedRect = mapper.addShape("rect", daddy, 2);
  }

  float t = 0;

  @Override
  public void draw() {
    background(0);

    t += 0.1f + 0.25f * (sin(frameCount / 100f) + 1) / 2f;

    mappedShape.beginMapping();
    noStroke();
    fill(0, 0, 255 * sin(t));
    shape.disableStyle();
    shape(shape);
    mappedShape.endMapping();

    for (Mapping m : mappedRect.getMappings()) {
      m.beginMapping();
      noLights();
      noStroke();
      fill(255 * sin(t + PI), 0, 0);
      rect(0, 0, 1000, 1000);
      m.endMapping();
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
