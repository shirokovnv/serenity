#version 430

in vec3 vColor;
out vec4 fragColor;

uniform float uOpacity;

void main()
{
    fragColor = vec4(vColor, uOpacity);
}
