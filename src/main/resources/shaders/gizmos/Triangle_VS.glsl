#version 430

layout (location = 0) in vec3 a_world;

uniform mat4x4 u_viewProj;

void main() {
    gl_Position = u_viewProj * vec4(a_world.xyz, 1.0f);
}