uniform mat4 transform;
uniform mat3 normalMatrix;
attribute vec4 position;
attribute vec3 normal;
varying vec3 vertNormal;
varying vec3 vertPosition;

void main() {
  gl_Position = transform * position;
  vertPosition = position.xyz;
  vertNormal = normalize(normalMatrix * normal);
}
