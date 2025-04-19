#version 430 core

uniform vec3 u_Color;

out vec4 o_Color;

void main() {
    o_Color = vec4(u_Color, 1.0f);
}