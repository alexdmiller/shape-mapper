# Shape Mapper

Shape Mapper is a [Processing](processing.org/) library for projection mapping.

![shape-mapper-clip](https://github.com/user-attachments/assets/9a1c0ce3-ff4e-42ab-b20c-06ef966df57a)
![cardboard](https://github.com/user-attachments/assets/59ac0c9a-4bd5-48f1-9ce9-d2512b451caa)

<img width="700" alt="getting-started-2" src="https://github.com/user-attachments/assets/0595fd10-d813-4704-8645-975ee4f563a4" />

> [!WARNING]  
> Shape Mapper is currently in beta. The library has been used for production installations, but has not yet been tested by the wider community, and some changes may occur based on feedback.

## 👩‍💻  Example code

Here is a minimal example showing what the library interface looks like:

``` java
import spacefiller.shapemapper.ShapeMapper;
import processing.core.PShape;

ShapeMapper mapper;
PShape shape;

void setup() {
  fullScreen(P3D);
  shape = createShape(BOX, 100, 200, 300);
  mapper = new ShapeMapper(this, shape);
}

void draw() {
  background(0);

  mapper.beginMapping();
  shape(shape);
  mapper.endMapping();
}
```

Find more examples [in the repository](examples/), packaged within the library, or on the [documentation site](https://alexdmiller.github.io/shape-mapper/).

## 🪛  Installation

1.  Ensure you have the most recent version of [Processing](https://processing.org) installed.
2.  [Download Shape Mapper v0.1.4](https://github.com/alexdmiller/shape-mapper/releases/download/v0.1.4/shapemapper.pdex).
3.  Open file on your computer; the library will be automatically installed.

## 📜  Documentation

Go to the [documentation site](https://alexdmiller.github.io/shape-mapper/).

## 💪  Use cases

[Projection mapping](https://en.wikipedia.org/wiki/Projection_mapping) is the process of calibrating a digital projection to align with a three dimensional surface. The Shape Mapper library is useful if you have a 3D model of the object you want to projection map. Some example workflows:

- Create a model using 3D software such as [Blender](https://www.blender.org/), fabricate it with a 3D printer, and projection map it using Shape Mapper.
- Take precise measurements of an object and reconstruct it using 3D software such as Blender, then projection map it using Shape Mapper.
- Use Processing code to model a physical object, then projection map it using Shape Mapper (works well for simple primitives such as cubes).

If you do not have a 3D model of the object you are trying to projection map, or you are not able to create a model in code, this library will probably not be useful to you. It is still very much possible to projection map your object, though! Check out the [Keystone](https://github.com/davidbouchard/keystone) library for Processing, or software such as MadMapper, Resolume or TouchDesigner for alternative projection mapping solutions.

## ✨  Features

- 🧮 Calibrate your projection by manually aligning at least 6 points; the mapping for the rest of the object will be automatically estimated.
- 🗺 Supports mapping multiple shapes at once (i.e. if you have two or more separate objects).
- 📽 Supports multiple mappings per shape (i.e. if you have two or more projectors).
- 💾 Calibrations are saved to disk.
- 🎭 Supports face masking, which allows you to avoid projecting twice onto the same face if using more than one projector.

## 🤷‍♂️  Non-features

Shape Mapper does not (yet) support:

- Blending between projectors
- Built in projection mapping effects (all effects must be drawn by the user with code)

## 🙏  Credits & Acknowledgements

Shape Mapper is by [Alex Miller](https://alexmiller.cv/) (x: [@spacefillerart](https://x.com/spacefillerart), ig: [@space.filler.art](https://www.instagram.com/space.filler.art/), bs: [@spacefiller](https://bsky.app/profile/spacefiller.bsky.social)).

Shape Mapper is heavily inspired by and based on the [mapamok](https://github.com/YCAMInterlab/mapamok) library for [openFrameworks](https://openframeworks.cc/), originally developed by [Kyle McDonald](https://kylemcdonald.net/) as part of ProCamToolkit, co-developed by [YCAM Interlab](https://www.ycam.jp/en/aboutus/interlab/). It expands on the feature set of mapamok with multi-shape and multi-projection support.

Thanks to Raphaël de Courville ([@SableRaf](https://github.com/SableRaf)) and Claudine Chen ([@mingness](https://github.com/mingness)) for their help & support with Processing library development.

For camera control, Shape Mapper uses the [Peasycam library](https://mrfeinberg.com/peasycam/) by [Jonathan Feinberg](https://mrfeinberg.com).

For estimating the projection mapping matrix, Shape Mapper uses [OpenCV](https://opencv.org/).

This library was initially developed at [Gradient](https://www.gradientretreat.com/). Thanks to Avi for the week of focus.
