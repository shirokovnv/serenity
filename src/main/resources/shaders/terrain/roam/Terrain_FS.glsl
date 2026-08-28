#version 430

#include <Atmosphere.glsl>

in float mapHeight_FS;
in vec3 mapNormal_FS;
in vec3 mapWorld_FS;
in vec2 mapCoord_FS;
out vec4 color;

uniform float u_scaleY;
uniform vec3 u_camPos;
uniform vec3 u_sunVector;
uniform vec3 u_sunColor;
uniform float u_sunIntensity;

const vec3 sandLight = vec3(0.96, 0.88, 0.74);
const vec3 sandDark  = vec3(0.56, 0.44, 0.32);

float diffuse(vec3 lightDir, vec3 normal, float intensity) {
    return max(0.03, dot(normal, lightDir)) * intensity;
}

void main()
{
    float scaleY = u_scaleY;
    vec3 lightDir = normalize(u_sunVector);
    vec3 viewDir  = normalize(u_camPos - mapWorld_FS);

    float slope = dot(mapNormal_FS, vec3(0.0, 1.0, 0.0));
    float blendValue = clamp(1.0 - abs(slope), 0.0, 1.0);
    vec3 baseColor = mix(sandDark, sandLight, blendValue);

    float diff = diffuse(lightDir, mapNormal_FS, u_sunIntensity);
    vec3 ambient = vec3(0.08, 0.07, 0.06);
    vec3 litColor = baseColor * (diff * vec3(1.0f) + ambient);

    vec3 eyeVector = u_camPos - mapWorld_FS;
    float s = dot(eyeVector, eyeVector);
    s = 1.0f/sqrt(s);
    eyeVector.xyz *= s;
    s = 1.0f / s;

    vec4 vI;
    vec4 vE;
    atmosphericLighting(
        eyeVector,
        u_sunVector,
        vec4(u_sunColor, u_sunIntensity),
        mapNormal_FS,
        s,
        vE,
        vI
    );

    vec3 finalColor = litColor * vE.xyz + vI.xyz;

    color = vec4(finalColor, 1.0);
}