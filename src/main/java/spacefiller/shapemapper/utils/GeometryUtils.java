package spacefiller.shapemapper.utils;

import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphics3D;

import java.util.Map;

public class GeometryUtils {
  public static PVector worldToScreen(PVector vertex, PGraphics3D graphics) {
    float screenX = graphics.screenX(vertex.x, vertex.y, vertex.z);
    float screenY = graphics.screenY(vertex.x, vertex.y, vertex.z);
    float screenZ = graphics.screenZ(vertex.x, vertex.y, vertex.z);
    return new PVector(screenX, screenY, screenZ);
  }

  public static PVector getClosestPointOnShape(PVector point, PShape shape, PGraphics3D graphics) {
    if (shape.getChildCount() > 0) {
      for (PShape child : shape.getChildren()) {
        PVector closest = getClosestPointOnShape(point, child, graphics);
        if (closest != null) {
          return closest;
        }
      }
    }

    if (shape.getVertexCount() > 0) {
      PVector closest = null;
      float minDistance = 1000;
      float selectionRadius = 10;

      for (int i = 0; i < shape.getVertexCount(); i++) {
        PVector vertex = shape.getVertex(i);
        PVector projectedVertex = worldToScreen(vertex, graphics);
        float dist = projectedVertex.dist(point);
        if (dist < selectionRadius && projectedVertex.z < minDistance) {
          closest = vertex;
          minDistance = projectedVertex.z;
        }
      }

      return closest;
    }

    return null;
  }

//  public static PVector getClosestPointByMappedPoint(PVector queryPoint) {
//    return getClosestPointByMappedPoint(queryPoint, pointMapping);
//  }

  public static PVector getClosestPointByMappedPoint(PVector queryPoint, Map<PVector, PVector> map) {
    float selectionRadius = 10;
    for (PVector referencePoint : map.keySet()) {
      PVector mapped = map.get(referencePoint);
      float dist = mapped.dist(queryPoint);
      if (dist < selectionRadius) {
        return referencePoint;
      }

    }
    return null;
  }

  public static int pickFace(PShape shape, PVector mouseClick, PGraphics3D graphics) {
    float closestZ = Float.POSITIVE_INFINITY;
    int closestFaceIndex = -1;

    // For each face in the shape
    for (int i = 0; i < shape.getChildCount(); i++) {
      PShape face = shape.getChild(i);

      // Get vertices of the face (assuming triangulated faces)
      PVector[] worldSpaceVertices = new PVector[3];
      for (int v = 0; v < 3; v++) {
        float x = face.getVertexX(v);
        float y = face.getVertexY(v);
        float z = face.getVertexZ(v);

        // Transform vertex to screen space
        PVector vertex = new PVector(x, y, z);
        vertex = worldToScreen(vertex, graphics);
        worldSpaceVertices[v] = vertex;
      }

      // Check if mouse click is inside the triangle
      if (pointInTriangle(mouseClick,
          worldSpaceVertices[0],
          worldSpaceVertices[1],
          worldSpaceVertices[2])) {

        // Calculate the z-depth at the clicked point using interpolation
        float z = interpolateZ(mouseClick,
            worldSpaceVertices[0],
            worldSpaceVertices[1],
            worldSpaceVertices[2]);

        // Update closest face if this one is closer
        if (z < closestZ) {
          closestZ = z;
          closestFaceIndex = i;
        }
      }
    }

    return closestFaceIndex;
  }

  public static boolean pointInTriangle(PVector p, PVector a, PVector b, PVector c) {
    float areaABC = Math.abs((b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y));
    float areaPBC = Math.abs((b.x - p.x) * (c.y - p.y) - (c.x - p.x) * (b.y - p.y));
    float areaPCA = Math.abs((c.x - p.x) * (a.y - p.y) - (a.x - p.x) * (c.y - p.y));
    float areaPAB = Math.abs((a.x - p.x) * (b.y - p.y) - (b.x - p.x) * (a.y - p.y));

    float alpha = areaPBC / areaABC;
    float beta = areaPCA / areaABC;
    float gamma = areaPAB / areaABC;

    return Math.abs(1 - (alpha + beta + gamma)) < 0.0001; // Account for floating point error
  }

  // Helper method to interpolate Z value at a point within a triangle
  private static float interpolateZ(PVector p, PVector a, PVector b, PVector c) {
    float areaABC = Math.abs((b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y));
    float areaPBC = Math.abs((b.x - p.x) * (c.y - p.y) - (c.x - p.x) * (b.y - p.y));
    float areaPCA = Math.abs((c.x - p.x) * (a.y - p.y) - (a.x - p.x) * (c.y - p.y));

    // Calculate barycentric coordinates
    float alpha = areaPBC / areaABC;
    float beta = areaPCA / areaABC;
    float gamma = 1 - alpha - beta;

    // Interpolate z using barycentric coordinates
    return alpha * a.z + beta * b.z + gamma * c.z;
  }

  public static void rotateToVector(PVector vec, PGraphics3D canvas) {
    // Default "up" vector
    PVector up = new PVector(0, 1, 0);

    // Calculate rotation axis (cross product of up and target vector)
    PVector rotAxis = up.cross(vec);

    // Calculate angle between vectors
    float angle = PVector.angleBetween(up, vec);

    // Rotate around the calculated axis
    canvas.rotate(angle, rotAxis.x, rotAxis.y, rotAxis.z);
  }
}
