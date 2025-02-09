#version 430

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aColor;

out vec3 vColor;

uniform mat4 uViewProjection;
uniform float uyOffset = 1.0f;

void main() {
    gl_Position = uViewProjection * vec4(aPos.x, aPos.y + uyOffset, aPos.z, 1.0f);
    vColor = aColor;
}