#version 430

#include <Transform.glsl>

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 locVector;
layout (location = 2) in vec3 scaleVector;
layout (location = 3) in vec4 lodVector;

out vec2 mapCoord_TC;
out vec4 lodVector_TC;

uniform sampler2D u_heightmap;
uniform mat4 u_model;
uniform mat4 u_world;

void main() {
    vec2 localPosition = (u_model * createTransformMatrix(vec3(locVector.x, 0, locVector.y), scaleVector)
    * vec4(position.x, 0, position.y, 1)).xz;

    float height = texture(u_heightmap, localPosition).r;

    vec4 worldPosition = u_world * vec4(localPosition.x, height, localPosition.y, 1);

    lodVector_TC = lodVector;
    mapCoord_TC = localPosition;

    gl_Position = worldPosition;
}