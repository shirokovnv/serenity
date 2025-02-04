#version 430 core

uniform vec3 uRayColor;

out vec4 fragColor;

void main()
{
    fragColor = vec4(uRayColor, 1.0);
}