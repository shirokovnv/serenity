#version 430 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;

out vec3 v_Normal;

uniform mat4 u_World;

void main() {
    v_Normal = normalize(mat3(transpose(inverse(u_World))) * a_Normal);
    gl_Position = u_World * vec4(a_Position, 1.0f);
}