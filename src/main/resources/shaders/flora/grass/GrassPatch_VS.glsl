#version 430 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 uvs;
layout (location = 3) in mat4 instance;

const vec2 windDirection = normalize(vec2(0.2, 0.5));
const float windSpeed = 0.1;
const float windDisplacement = 0.02;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 worldMatrix;
uniform vec3 cameraPosition;
uniform float time;
uniform vec3 worldScale;
uniform vec3 worldOffset;
uniform sampler2D heightmap;

out float height;
out float alpha;

mat4 rotationY(in float angle) {
    return mat4(cos(angle), 0, sin(angle), 0,
    0, 1.0, 0, 0,
    -sin(angle), 0, cos(angle), 0,
    0, 0, 0, 1);
}

float rand(vec2 co) {
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

float getDisplacementMap(vec2 grassPosition) {
    return abs(sin((grassPosition.x * windDirection.x + grassPosition.y * windDirection.y) + windSpeed * time)) * 1.3 + (sin(time * 10 + rand(grassPosition) * 40) * 0.03);
}

vec2 worldToTexture(float worldX, float worldZ, vec3 worldScale, vec3 worldOffset) {
    float scaledX = clamp((worldX - worldOffset.x) / worldScale.x, 0, 1);
    float scaledY = clamp((worldZ - worldOffset.z) / worldScale.z, 0, 1);

    return vec2(scaledX, scaledY);
}

void main() {
    vec2 grassPosition = vec2(instance[3][0], instance[3][2]);

    float random = rand(grassPosition);

    float localWindVariance = random * 0.5;
    float rotation = rand(grassPosition + vec2(5)) * 360.0;

    mat4 modelRotation = rotationY(radians(rotation));

    vec3 localPos = (modelRotation * vec4(position, 1.0)).xyz;
    vec3 finalPosition = vec3(localPos.x + grassPosition.x, localPos.y, localPos.z + grassPosition.y);

    height = position.y / 0.06;

    vec2 displacement = getDisplacementMap(grassPosition) * windDirection;
    finalPosition += vec3(displacement.x + localWindVariance, localPos.y, displacement.y + localWindVariance) * (height * height) * windDisplacement;

    mat4 wm = worldMatrix;
    wm[3][1] = 0; // set heightoffset to zero, later retrieve it from heightmap

    vec4 worldPosition = (wm * vec4(finalPosition, 1));
    vec2 heightUV = worldToTexture(worldPosition.x, worldPosition.z, worldScale, worldOffset);
    float terrainHeight = worldScale.y * texture(heightmap, heightUV).r;
    worldPosition.y += terrainHeight;

    gl_Position = projMatrix * viewMatrix * worldPosition;

    // 1. Calculate distance to the patch
    float dist = length((worldMatrix * vec4(finalPosition, 1)).xyz - cameraPosition);

    // 2. Determine alpha blend range
    float minDistance = 10.0;  // Min distance, when alpha = 1
    float maxDistance = 100.0; // Max distance, when alpha = 0

    // 3. Calculate alpha blend factor
    alpha = 1 - clamp((dist - minDistance) / (maxDistance - minDistance), 0.0, 1.0);
}
