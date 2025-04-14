#version 450 core

in vec4 f_Color;
layout (location = 0) out vec4 o_Color;

void main()
{
    o_Color = f_Color;
}