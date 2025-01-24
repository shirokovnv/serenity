#version 140

in vec2 textureCoords;
out vec4 outColor;

uniform sampler2D colorTexture;

void main(void){
    outColor = texture(colorTexture, textureCoords);
}