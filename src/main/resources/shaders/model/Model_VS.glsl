#version 430

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 uvs;
layout (location = 3) in mat4 instance;

uniform mat4 m_WorldMatrix;
uniform mat4 m_WorldViewProjection = mat4(1.0);
uniform vec4 clipPlane;
uniform bool isInstanced = false;

// Output variables sent to the fragment shader
out struct data
{
    vec3 normal;     // normal position in world space
    vec2 uv;         // vertex uv
} fragment;

void main() {
    vec4 localPosition = vec4(position, 1.0f);

    if (isInstanced) {
        localPosition = instance * localPosition;
    }

    fragment.normal = normal;
    fragment.uv = uvs;

    gl_Position = m_WorldViewProjection * localPosition;
    gl_ClipDistance[0] = dot(m_WorldMatrix * localPosition, clipPlane);
}