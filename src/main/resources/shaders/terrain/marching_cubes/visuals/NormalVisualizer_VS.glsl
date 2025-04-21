#version 430 core

layout (location = 0) in vec3 a_Position;

out vec3 v_Normal;

uniform mat4 u_World;

void main() {
    gl_Position = u_World * vec4(a_Position, 1.0f);
}