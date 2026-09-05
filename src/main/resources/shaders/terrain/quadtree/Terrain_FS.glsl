#version 430

in float mapHeight_FS;
in vec3 mapWorld_FS;
in vec2 mapCoord_FS;
out vec4 color;

uniform vec3 u_camPos;
uniform vec3 u_sunColor;
uniform vec3 u_sunVector;
uniform float u_sunIntensity;
uniform float u_scaleY;
uniform sampler2D u_normalMap;
uniform sampler2D u_blendMap;

const vec3 colorGrass  = vec3(0.28, 0.58, 0.24);
const vec3 colorDirt   = vec3(0.42, 0.35, 0.30);
const vec3 colorRock = vec3(0.28, 0.22, 0.18);
const vec3 colorSnow   = vec3(0.95, 0.98, 1.00);

float diffuse(vec3 lightDir, vec3 normal, float intensity) {
    return max(0.0, dot(normal, lightDir)) * intensity;
}

vec3 blinnSpecular(vec3 lightDir, vec3 viewDir, vec3 normal, vec3 specColor, float specPower) {
    vec3 halfDir = normalize(lightDir + viewDir);
    float specAngle = max(0.0, dot(normal, halfDir));
    return pow(specAngle, specPower) * specColor;
}

vec3 toneMap(vec3 color) {
    return color / (color + vec3(1.0));
}

void main() {
    vec3 lightDir = normalize(u_sunVector);
    vec3 viewDir  = normalize(u_camPos - mapWorld_FS);
    vec3 normal = normalize(texture(u_normalMap, mapCoord_FS).rbg);

    // base color from blendmap
    vec4 blend = texture(u_blendMap, mapCoord_FS);
    vec3 baseColor =
    colorGrass * blend.r +
    colorDirt  * blend.g +
    colorRock  * blend.b +
    colorSnow  * blend.a;

    // lighting
    float diff = diffuse(lightDir, normal, u_sunIntensity);
    vec3 spec = blinnSpecular(lightDir, viewDir, normal, vec3(1.0f), 32.0f);
    const vec3 ambient = vec3(0.12, 0.13, 0.18);

    // atmosphere
    float sunElevation = u_sunVector.y;
    float atmosphereFactor = smoothstep(-0.2, 0.1, sunElevation);

    float h = mapHeight_FS / u_scaleY;
    float d = length(u_camPos - mapWorld_FS);

    float density = 0.25;
    float hDensity = 1.5;

    float opticalDepth = d * density + h * hDensity;
    vec3 extinction = exp(-vec3(5e-3, 1.2e-2, 2.8e-2) * opticalDepth);

    // inscatter simplified
    float sunDot = max(0.0, dot(viewDir, u_sunVector));
    float inscatterFactor = pow(max(0.0, sunDot + 0.1), 0.6) * (1.0 - clamp(h * 0.7, 0.0, 1.0));
    vec3 skyColor = vec3(0.4, 0.65, 0.95) * atmosphereFactor * inscatterFactor;

    // rim lighting
    float rim = 1.0 - max(0.0, dot(normal, viewDir));
    float rimFactor = pow(rim, 4.0);
    vec3 rimColor = vec3(1.0, 0.85, 0.65) * rimFactor * atmosphereFactor;
    float heightFade = 1.0 - clamp(h, 0.0, 1.0);

    vec3 atmosphere = (rimColor + skyColor) * heightFade * atmosphereFactor;

    vec3 finalColor = baseColor * (diff * u_sunColor + ambient); // + spec;
    finalColor = mix(finalColor, atmosphere, 1.0 - extinction.r);

    vec3 fogColorDay   = vec3(0.35, 0.45, 0.55);
    vec3 fogColorNight = vec3(0.10, 0.10, 0.15);
    vec3 fogColor = mix(fogColorNight, fogColorDay, atmosphereFactor);
    float fogFactor = 1.0 - exp(-0.002 * opticalDepth);

    finalColor = mix(finalColor, fogColor, fogFactor);

    color = vec4(finalColor, 1.0);
}