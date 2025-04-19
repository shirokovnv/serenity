#version 430 core

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoords;

out vec4 FragColor;

uniform vec3 sunVector;
uniform vec3 sunColor;
uniform float sunIntensity;
uniform vec4 diffuseColor;
uniform bool isShadowPass = false;

// TODO: separate
float diffuse(vec3 direction, vec3 normal, float intensity)
{
    return max(0.04, dot(normal, -direction) * intensity);
}

void main()
{
    if (isShadowPass) {
        return;
    }

    float diffuseFactor = 10.0f;
    vec4 totalColor = diffuseColor;
    totalColor *= diffuse(-sunVector, normalize(Normal), sunIntensity * diffuseFactor);
    totalColor *= vec4(sunColor, 1);

    FragColor = totalColor;
}