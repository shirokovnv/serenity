#version 330

in vec2 pass_textureCoords;

out vec4 out_colour;

uniform sampler2D activeFlare;
uniform float brightness = 0.0f;

void main(void){

    out_colour = texture(activeFlare, pass_textureCoords);
    out_colour.a *= brightness;
}