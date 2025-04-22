#version 430 core

in vec3 g_Position;
in vec3 g_Normal;
in float g_Occlusion;
out vec4 o_Color;

uniform vec3 u_LightDirection; // Directional light
uniform vec3 u_LightColor;     // Color of the light
uniform float u_LightIntensity; // Light intensity
uniform vec3 u_ColorOne;       // Primary color of the object
uniform vec3 u_ColorTwo;       // Secondary color of the object
uniform float u_AmbientStrength; // Strength of the ambient lighting
uniform float u_DiffuseStrength; // Strength of the diffuse lighting

// TODO: separate
float diffuse(vec3 direction, vec3 normal, float intensity)
{
    return max(0.04, dot(normal, -direction) * intensity);
}

void main() {
    // 1. Ambient Lighting
    vec3 ambient = u_AmbientStrength * u_LightColor;

    // 2. Diffuse Lighting
    vec3 normal = normalize(g_Normal);  // Ensure normal is normalized
    vec3 lightDir = normalize(u_LightDirection); // Direction from fragment to light source

    float diff = diffuse(-u_LightDirection, normal, u_LightIntensity);
    vec3 diffuse = u_DiffuseStrength * diff * u_LightColor;

    // Coloring based on the angle of the normal to the Y axis
    float angle = acos(dot(normal, vec3(0.0, 1.0, 0.0)));

    // Setting colors for different angles
    vec3 angleColor = mix(u_ColorOne, u_ColorTwo, angle / (3.14159265359 / 2.0));

    // Combine ambient and diffuse lighting components
    vec3 result = (ambient + diffuse * g_Occlusion) * angleColor;
    o_Color = vec4(result, 1.0);
}