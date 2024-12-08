#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
uniform int time;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main(void) {
    vec4 color = texture2D(texture, vertTexCoord.st);
    float cellSize = 50;
    if (color.a > 0) {
        gl_FragColor = color;
    } else {
        vec2 tilePos = floor((gl_FragCoord.xy + float(time) / 5.0) / cellSize);
        float g = mod(tilePos.x + tilePos.y, 2) * 0.1;
        gl_FragColor = vec4(g, g, g, 1.);
    }
}
