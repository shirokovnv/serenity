#version 430 core

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in vec3 v_Normal[];
in float v_Occlusion[];
out vec3 g_Position;
out vec3 g_Normal;
out float g_Occlusion;

uniform int u_Resolution;
uniform mat4 u_World;
uniform mat4 u_ViewProjection;

void main() {
    for (int i = 0; i < 3; i++) {
        g_Position = (gl_in[i].gl_Position.xyz / float(u_Resolution));
        g_Normal = normalize(mat3(transpose(inverse(u_World))) * v_Normal[i]);
        g_Occlusion = v_Occlusion[i];

        gl_Position = u_ViewProjection * u_World * gl_in[i].gl_Position;
        EmitVertex();
    }
    EndPrimitive();
}