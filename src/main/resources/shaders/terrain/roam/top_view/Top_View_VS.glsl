#version 430

layout (location = 0) in vec2 a_local;

uniform mat4 u_model;

void main() {
    vec2 position = vec2((1.0 - a_local.x), (1.0 - a_local.y)) * 2.0 - 1.0;

    gl_Position = u_model * vec4(position.x, position.y, -1.0, 1.0);
}