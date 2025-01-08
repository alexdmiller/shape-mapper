import spacefiller.shapemapper.ShapeMapper;
import processing.core.PShape;

ShapeMapper mapper;
PShape shape;
PShader shader;

void setup() {
  fullScreen(P3D);
  shape = createShape(BOX, 150);
  mapper = new ShapeMapper(this, shape);
  shader = loadShader("frag.glsl", "vert.glsl");
}

void draw() {
  background(0);  
  mapper.beginMapping();
  shader.set("time", frameCount / 100f);
  shader(shader);
  shape(shape);
  mapper.endMapping();
}
