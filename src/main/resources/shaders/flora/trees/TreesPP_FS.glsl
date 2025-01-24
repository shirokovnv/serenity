#version 430

// Inputs coming from the vertex shader
in struct data
{
    vec2 uv;       // current uv-texture on the fragment
} fragment;

out vec4 outColor;

uniform sampler2D diffuseMap;
uniform float alphaThreshold;

void main() {

    vec4 diffuseColor = texture(diffuseMap, fragment.uv);
    if (diffuseColor.a < alphaThreshold) {
        discard;
    }

    outColor = vec4(0, 0, 0, 1);
}