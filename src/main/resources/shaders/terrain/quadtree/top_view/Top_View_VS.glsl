#version 430 core

#include <Transform.glsl>

layout(location = 0) in vec2 position;
layout (location = 1) in vec2 locVector;
layout (location = 2) in vec3 scaleVector;
layout (location = 3) in vec4 lodVector;
layout (location = 4) in vec3 lowPoint;
layout (location = 5) in vec3 highPoint;

out vec2 v_local;
out vec3 v_low;
out vec3 v_high;

uniform mat4 u_model;

void main() {
    v_local = (u_model * createTransformMatrix(vec3(locVector.x, 0, locVector.y), scaleVector)
    * vec4(position.x, 0, position.y, 1)).xz;

    // [0,1] -> [-1,1]
    v_local = v_local * 2.0 - 1.0;
    v_low = lowPoint;
    v_high = highPoint;

    gl_Position = vec4(position.x, 0.0, position.y, 1.0);
}