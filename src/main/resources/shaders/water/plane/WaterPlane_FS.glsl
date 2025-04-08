#version 430 core

in vec2 textureCoords;
in vec4 clipSpace;
in vec3 toCameraVector;
in vec3 fromLightVector;

out vec4 outColor;

uniform sampler2D u_reflectionMap;
uniform sampler2D u_refractionMap;
uniform sampler2D u_dudvMap;
uniform sampler2D u_normalMap;
uniform sampler2D u_depthMap;
uniform float u_moveFactor;
uniform vec3 u_lightColor = vec3(1.0, 1.0, 1.0);
uniform float u_near = 1.0f;
uniform float u_far = 1000.0f;

const float waveStrength = 0.02;
const float shineDamper = 20.0;
const float reflectivity = 0.6;

void main() {
    vec2 ndc = (clipSpace.xy/clipSpace.w) * 0.5 + 0.5;

    vec2 refractTexCoords = vec2(ndc.x, ndc.y);
    vec2 reflectTexCoords = vec2(ndc.x, -ndc.y);

    float depth = texture(u_depthMap, refractTexCoords).r;
    float floorDistance = 2.0 * u_near * u_far / (u_far + u_near - (2.0 * depth - 1.0) * (u_far - u_near));

    depth = gl_FragCoord.z;
    float waterDistance = 2.0 * u_near * u_far / (u_far + u_near - (2.0 * depth - 1.0) * (u_far - u_near));
    float waterDepth = floorDistance - waterDistance;

    // dudv
    vec2 distortedTexCoords = texture(u_dudvMap, vec2(textureCoords.x + u_moveFactor, textureCoords.y)).rg*0.1;
    distortedTexCoords = textureCoords + vec2(distortedTexCoords.x, distortedTexCoords.y+ u_moveFactor);
    vec2 totalDistortion = (texture(u_dudvMap, distortedTexCoords).rg * 2.0 - 1.0) * waveStrength;

    totalDistortion *= clamp(waterDepth / 5.0, 0.0, 1.0);

    reflectTexCoords += totalDistortion;
    refractTexCoords += totalDistortion;

    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, -0.999, -0.001);
    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

    vec4 reflectionColor = texture(u_reflectionMap, reflectTexCoords);
    vec4 refractionColor = texture(u_refractionMap, refractTexCoords);

    // normals
    vec4 normalMapColor = texture(u_normalMap, distortedTexCoords);
    vec3 normal = vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b, normalMapColor.g * 2.0 - 1.0);
    normal = normalize(normal);

    vec3 viewVector = normalize(toCameraVector);
    float refractFactor = dot(viewVector, vec3(normal));

    vec3 reflectedLight = reflect(normalize(fromLightVector), normal);
    float specular = max(dot(reflectedLight, viewVector), 0.0);
    specular = pow(specular, shineDamper);
    vec3 specularHighlights = u_lightColor * specular * reflectivity;
    specularHighlights *= clamp(waterDepth / 5.0, 0.0, 1.0);

    outColor = mix(reflectionColor, refractionColor, max(refractFactor, 1.0));
    outColor = mix(outColor, vec4(0.0, 0.3, 0.5, 1.0), 0.6) + vec4(specularHighlights, 0.0);
}