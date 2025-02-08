#version 430 core

uniform vec3 uSphereColor;

out vec4 fragColor;

void main()
{
    fragColor = vec4(uSphereColor, 1.0);
}