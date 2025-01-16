#version 430

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 uvs;

uniform mat4 m_WorldViewProjection = mat4(1.0);

void main() {
    gl_Position = m_WorldViewProjection * vec4(position, 1.0);
}