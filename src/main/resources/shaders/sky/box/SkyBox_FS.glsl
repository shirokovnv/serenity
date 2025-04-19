#version 430 core

in vec3 f_TexCoord;
out vec4 o_Color;

uniform samplerCube u_CubemapTexture;

void main() {
    o_Color = texture(u_CubemapTexture, f_TexCoord) * vec4(1.0, 0.9, 1.1, 1.0);
}