# How-to: Use multiple projectors

TODO

``` java
myShape = createShape(BOX, 150);
mapper = new ShapeMapper(this);
mappedShape = mapper.addShape("box", myShape, 2);
```

``` java
for (Mapping mapping : mappedShape.getMappings()) {
  mapping.beginMapping();
  shape(myShape);
  mapping.endMapping();
}
```