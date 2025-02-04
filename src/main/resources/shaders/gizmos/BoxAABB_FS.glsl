#version 430 core

uniform vec3 uBoxColor;

out vec4 fragColor;

void main()
{
    fragColor = vec4(uBoxColor, 1.0);
}