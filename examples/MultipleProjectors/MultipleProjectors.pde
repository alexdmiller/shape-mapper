import spacefiller.shapemapper.ShapeMapper;
import spacefiller.shapemapper.MappedShape;
import spacefiller.shapemapper.Mapping;

ShapeMapper mapper;
PShape myShape;
MappedShape mappedShape;

void setup() {
  fullScreen(P3D);
  myShape = createShape(BOX, 150);
  mapper = new ShapeMapper(this);
  mappedShape = mapper.addShape("box", myShape);
}

void draw() {
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
