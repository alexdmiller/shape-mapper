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
  noLights();
  pointLight(
    0,
    255,
    255,
    sin(frameCount / 10f) * 500,
    cos(frameCount / 10f) * 500,
    sin(frameCount / 10f) * 500);  
  noStroke();
  shape(myShape);
  mapper.endMapping();
}
