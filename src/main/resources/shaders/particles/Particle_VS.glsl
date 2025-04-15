#version 430 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in float a_Size;
layout (location = 3) in float a_Life;

out struct Vertex {
    vec4 color;
    float size;
    float life;
} g_Vertex;

uniform mat4 u_Model;
uniform mat4 u_View;

void main()
{
    gl_Position = u_View * u_Model * vec4(a_Position, 1.0);

    g_Vertex.size = a_Size;
    g_Vertex.color = a_Color;
    g_Vertex.life = a_Life;
}