#version 430 core

in vec3 g_Normal;
out vec4 o_Color;

uniform vec3 u_Color;
uniform float u_Opacity = 1.0f;

void main() {
    o_Color = vec4(u_Color, u_Opacity);
}