#version 430 core

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in vec3 v_Normal[];
out vec3 g_Normal;

uniform mat4 u_ViewProjection;

void main() {
    for (int i = 0; i < 3; i++) {
        g_Normal = v_Normal[i];
        gl_Position = u_ViewProjection * gl_in[i].gl_Position;
        EmitVertex();
    }
    EndPrimitive();
}