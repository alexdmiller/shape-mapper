#define PROCESSING_LIGHT_SHADER

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform float normalColorStrength;

varying vec4 vertColor;
varying vec4 vertTexCoord;
varying vec3 vertNormal;

void main(void) {
    gl_FragColor = vec4(normalColorStrength * (normalize(vertNormal) * 0.5 + 0.5), 1.0);
}
