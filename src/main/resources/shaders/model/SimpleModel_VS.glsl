#version 430

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 uvs;
layout (location = 3) in mat4 instance;

uniform mat4 m_WorldViewProjection = mat4(1.0);
uniform bool isInstanced = false;

out vec2 fragUV;

void main() {
    vec4 localPosition = vec4(position, 1.0f);
    if (isInstanced) {
        localPosition = instance * localPosition;
    }
    gl_Position = m_WorldViewProjection * localPosition;
    fragUV = uvs;
}