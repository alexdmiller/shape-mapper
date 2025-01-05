import spacefiller.shapemapper.ShapeMapper;

ShapeMapper mapper;
PShape myShape;

void setup() {
  fullScreen(P3D);
  myShape = createShape(BOX, 150);
  mapper = new ShapeMapper(this, myShape);
}

void draw() {
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
