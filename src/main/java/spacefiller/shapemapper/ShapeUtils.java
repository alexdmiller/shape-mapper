package spacefiller.shapemapper;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

import static processing.core.PShape.*;


public class ShapeUtils {
  static public PShape createShape(PApplet parent, PShape src) {
    PShape dest = null;
    if (src.getFamily() == GROUP) {
      dest = parent.createShape(GROUP);
      copyGroup(parent, src, dest);
    } else if (src.getFamily() == GEOMETRY) {
      dest = parent.createShape(GEOMETRY);
      copyGeometry(src, dest);
    } else if (src.getFamily() == PRIMITIVE) {
      dest = parent.createShape(src.getKind(), src.getParams());
    }
    dest.setName(src.getName());
    return dest;
  }

  static public void copyGroup(PApplet parent, PShape src, PShape dest) {
    for (int i = 0; i < src.getChildCount(); i++) {
      PShape c = createShape(parent, src.getChild(i));
      dest.addChild(c);
    }
  }

  static public void copyGeometry(PShape src, PShape dest) {
    dest.beginShape(src.getKind());

    for (int i = 0; i < src.getVertexCount(); i++) {
      PVector vert = src.getVertex(i);
      PVector normal = src.getNormal(i);
      PVector uv = new PVector(src.getTextureU(i), src.getTextureV(i));
      dest.normal(normal.x, normal.y, normal.z);
      dest.vertex(vert.x, vert.y, vert.z, uv.x, uv.y);
    }

    dest.endShape();
  }
}
