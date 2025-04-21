#version 430 core

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

out vec3 g_Normal;

uniform mat4 u_ViewProjection;

void main() {
    vec3 v0 = gl_in[0].gl_Position.xyz;
    vec3 v1 = gl_in[1].gl_Position.xyz;
    vec3 v2 = gl_in[2].gl_Position.xyz;

    vec3 edge1 = v1 - v0;
    vec3 edge2 = v2 - v0;

    g_Normal = normalize(cross(edge1, edge2));

    for (int i = 0; i < 3; i++) {
        gl_Position = u_ViewProjection * gl_in[i].gl_Position;
        EmitVertex();
    }
    EndPrimitive();
}