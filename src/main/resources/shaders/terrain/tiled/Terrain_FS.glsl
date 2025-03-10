#version 430

#include <Shadow.glsl>

in vec2 mapCoord_FS;
in vec3 position_FS;
in vec3 normal_FS;
in vec4 positionLightSpace_FS;
in vec3 tangent_FS;
out vec4 outColor;

uniform sampler2D normalmap;
uniform sampler2D blendmap;
uniform sampler2D shadowmap;
uniform float tbnRange;
uniform float tbnThreshold;
uniform vec3 cameraPosition;
uniform vec3 sunVector;
uniform float sunIntensity;
uniform vec3 sunColor;
uniform bool renderInBlack;

struct Material {
    sampler2D diffusemap;
    sampler2D normalmap;
    sampler2D displacementmap;
    float verticalScale;
    float horizontalScale;
};

uniform Material materials[3];
// 0 - grass
// 1 - dirt
// 2 - rock

float diffuse(vec3 direction, vec3 normal, float intensity)
{
    return max(0.04, dot(normal, -direction) * intensity);
}

void main()
{
    if (renderInBlack) {
        outColor = vec4(0, 0, 0, 1);

        return;
    }

    float dist = length(cameraPosition - position_FS);
    vec3 normal = normalize(texture(normalmap, mapCoord_FS).rbg);

    vec4 blendValues = texture(blendmap, mapCoord_FS).rgba;
    float[4] blendValueArray = float[](blendValues.r, blendValues.g, blendValues.b, blendValues.a);

    if (dist < tbnRange - tbnThreshold)
    {
        float attenuation = clamp(-dist / (tbnRange - tbnThreshold) + 1, 0.0, 1.0);

        vec3 bitangent = normalize(cross(tangent_FS, normal));
        mat3 TBN = mat3(bitangent, normal, tangent_FS);

        vec3 bumpNormal;
        for (int i = 0; i < 3; i++) {
            bumpNormal += (2 * (texture(materials[i].normalmap, mapCoord_FS * materials[i].horizontalScale).rbg) - 1) * blendValueArray[i];
        }

        bumpNormal = normalize(bumpNormal);
        bumpNormal.xz *= attenuation;

        normal = normalize(TBN * bumpNormal);
    }

    vec3 fragColor = vec3(0, 0, 0);

    for (int i = 0; i < 3; i++) {
        fragColor += texture(materials[i].diffusemap, mapCoord_FS * materials[i].horizontalScale).rgb
        * blendValueArray[i];
    }

    vec3 ambient = mix(vec3(0), vec3(0.1, 0.1, 0.2), 1 - dot(sunVector, normal));
    float diffuse = diffuse(-sunVector, normal, sunIntensity);
    fragColor *= diffuse * sunColor + ambient;

    // shadow calculation
    float shadow = shadowCalculation(positionLightSpace_FS, normal_FS, -sunVector, shadowmap, 20.0f);
    fragColor *= (1 - shadow);

    outColor = vec4(fragColor, 1.0);
}
