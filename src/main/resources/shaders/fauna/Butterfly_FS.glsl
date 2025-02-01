#version 430 core

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoords;

out vec4 FragColor;

uniform vec3 sunVector;
uniform vec3 sunColor;
uniform float sunIntensity;
uniform sampler2D diffuseTexture;

// TODO: separate
float diffuse(vec3 direction, vec3 normal, float intensity)
{
    return max(0.04, dot(normal, -direction) * intensity);
}

void main()
{
    float diffuseFactor = 10.0f;
    vec4 diffuseColor = texture(diffuseTexture, TexCoords);
    diffuseColor *= diffuse(-sunVector, Normal, sunIntensity * diffuseFactor);
    diffuseColor *= vec4(sunColor, 1);

    FragColor = diffuseColor;
}