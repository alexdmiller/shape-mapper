#define PROCESSING_LIGHT_SHADER

uniform mat4 transformMatrix;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;

attribute vec4 position;
attribute vec4 color;
attribute vec2 texCoord;
attribute vec3 normal;

varying vec4 vertColor;
varying vec4 vertTexCoord;
varying vec3 vertNormal;

void main() {
    gl_Position = transformMatrix * position;

    vertColor = color;
    vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
    vertNormal = normalize(normal);
}