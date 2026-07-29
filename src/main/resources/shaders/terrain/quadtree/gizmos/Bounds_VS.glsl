#version 430 core

layout(location = 0) in vec2 position;
layout (location = 1) in vec2 locVector;
layout (location = 2) in vec3 scaleVector;
layout (location = 3) in vec4 lodVector;
layout (location = 4) in vec3 lowPoint;
layout (location = 5) in vec3 highPoint;

out vec3 v_low;
out vec3 v_high;

void main() {
    v_low = lowPoint;
    v_high = highPoint;

    gl_Position = vec4(position.x, 0.0f, position.y, 1.0);
}