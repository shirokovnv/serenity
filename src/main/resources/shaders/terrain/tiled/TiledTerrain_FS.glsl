#version 430

in float height;
//in vec2 mapCoord_FS;
out vec4 color;

uniform float scaleY;

void main()
{
    float scaledHeight = height / scaleY;
    color = vec4(scaledHeight, scaledHeight, scaledHeight, 1);
}
