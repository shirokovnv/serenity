#version 430

layout (location = 0) in vec2 a_local;

uniform mat4 u_model;
uniform mat4 u_world;
uniform sampler2D u_heightmap;

void main() {
    vec2 localPosition = (u_model * vec4(a_local.x, 0, a_local.y, 1)).xz;
    float height = texture(u_heightmap, localPosition).g;

    gl_Position = u_world * vec4(localPosition.x, height, localPosition.y, 1);
}