#version 430

// Inputs coming from the vertex shader
in struct data
{
    vec3 normal;   // normal in the world space
    vec2 uv;       // current uv-texture on the fragment
} fragment;

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
uniform bool isShadowPass = false;

uniform vec3 sunVector;
uniform vec3 sunColor;
uniform float sunIntensity;

// TODO: separate
float diffuse(vec3 direction, vec3 normal, float intensity)
{
    return max(0.04, dot(normal, -direction) * intensity);
}

void main() {
    if (isShadowPass) {
        discard;
    }

    vec4 ambientColor = vec4(0.0f);
    if (isAmbientMapUsed) {
        ambientColor = texture(ambientMap, fragment.uv);
    }

    vec4 diffuseColor = vec4(1.0f);
    if (isDiffuseMapUsed) {
        diffuseColor = texture(diffuseMap, fragment.uv);
    }
    diffuseColor *= diffuse(-sunVector, fragment.normal, sunIntensity);
    diffuseColor *= vec4(sunColor, 1);

    vec4 normalColor = vec4(1.0f);
    if (isNormalMapUsed) {
        normalColor = texture(normalMap, fragment.uv);
    }

    vec4 specularColor = vec4(1.0f);
    if (isSpecularMapUsed) {
        specularColor = texture(specularMap, fragment.uv);
    }

    vec4 finalColor = ambientColor + diffuseColor;
    if (finalColor.a < alphaThreshold) {
        discard;
    }

    outColor = finalColor;
}