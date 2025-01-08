uniform float time;

varying vec4 vertColor;
varying vec3 vertNormal;
varying vec3 vertPosition;

void main() {
    // normalize position to between 0 - 1
    vec3 normalizedPosition = normalize(vertPosition);
    vec3 targetPoint = vec3(sin(time), cos(time), -1);
    float distanceToTarget = length(normalizedPosition - targetPoint);
    // set gl_FragColor to a color based on the distance to the target
    gl_FragColor = vec4(vec3(sin(distanceToTarget * 100.0 + time * 10)), 1.0);
}
