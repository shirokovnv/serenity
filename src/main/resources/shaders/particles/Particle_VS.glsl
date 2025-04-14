#version 430 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in float a_Size;

out vec4 g_Color;
out float g_Size;

uniform mat4 u_Model;
uniform mat4 u_View;

void main()
{
    gl_Position = u_View * u_Model * vec4(a_Position, 1.0);
    g_Color = a_Color;
    g_Size = a_Size;
}