import spacefiller.shapemapper.ShapeMapper;
import spacefiller.shapemapper.MappedShape;
import spacefiller.shapemapper.Mapping;

ShapeMapper mapper;
PShape box;
PShape sphere;
MappedShape mappedBox;
MappedShape mappedSphere;

void setup() {
  fullScreen(P3D);
  box = createShape(BOX, 150);
  sphere = createShape(SPHERE, 150);

  mapper = new ShapeMapper(this);
  mappedBox = mapper.addShape("box", box);
  mappedSphere = mapper.addShape("sphere", sphere);
}

void draw() {
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
