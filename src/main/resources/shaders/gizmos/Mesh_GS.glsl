#version 430 core

layout (triangles) in;
layout (line_strip, max_vertices = 6) out;

uniform mat4 u_ViewProjection;

void main() {
    for (int i = 0; i < 3; i++) {
        gl_Position = u_ViewProjection * gl_in[i].gl_Position;
        EmitVertex();

        gl_Position = u_ViewProjection * gl_in[(i + 1) % 3].gl_Position;
        EmitVertex();
    }
}