#version 430

in vec2 fragUV;
out vec4 outColor;

uniform sampler2D ambientMap;
uniform sampler2D diffuseMap;
uniform sampler2D normalMap;
uniform sampler2D specularMap;
uniform bool isAmbientMapUsed;
uniform bool isDiffuseMapUsed;
uniform bool isNormalMapUsed;
uniform bool isSpecularMapUsed;
uniform float alphaThreshold;

void main() {

    vec4 ambientColor = vec4(0.0f);
    if (isAmbientMapUsed) {
        ambientColor = texture(ambientMap, fragUV);
    }

    vec4 diffuseColor = vec4(1.0f);
    if (isDiffuseMapUsed) {
        diffuseColor = texture(diffuseMap, fragUV);
    }

    vec4 normalColor = vec4(1.0f);
    if (isNormalMapUsed) {
        normalColor = texture(normalMap, fragUV);
    }

    vec4 specularColor = vec4(1.0f);
    if (isSpecularMapUsed) {
        specularColor = texture(specularMap, fragUV);
    }

    vec4 finalColor = ambientColor + diffuseColor;
    if (finalColor.a < alphaThreshold) {
        discard;
    }

    outColor = finalColor;
}