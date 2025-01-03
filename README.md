# Shape Mapper

Shape Mapper is a [Processing](processing.org/) library for projection mapping.

![output](https://github.com/user-attachments/assets/af2e75f6-b4d0-4e52-bb8e-f2c5d644a5bc)

## Use cases

[Projection mapping](https://en.wikipedia.org/wiki/Projection_mapping) is the process of calibrating a digital projection to align with a three dimensional surface. The Shape Mapper library is useful if you have a 3D model of the object you want to projection map. Some example workflows:

- Create a model using 3D software such as [Blender](https://www.blender.org/), fabricate it with a 3D printer, and projection map it using Shape Mapper.
- Take precise measurements of an object and reconstruct it using 3D software such as Blender, then projection map it using Shape Mapper.
- Use Processing code to model a physical object, then projection map it using Shape Mapper (works well for simple primitives such as cubes).

If you do not have a 3D model of the object you are trying to projection map, or you are not able to create a model in code, this library will probably not be useful to you. It is still very much possible to projection map your object, though! Check out the [Keystone](https://github.com/davidbouchard/keystone) library for Processing, or software such as MadMapper, Resolume or TouchDesigner for alternative projection mapping solutions.

## Documentation

TODO