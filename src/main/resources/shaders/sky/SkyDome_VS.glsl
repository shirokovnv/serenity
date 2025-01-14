#version 430

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uvs;

uniform mat4 m_WorldViewProjection = mat4(1.0);

out vec2 mapCoord_FS;
out float height;

void main()
{
    gl_Position = m_WorldViewProjection * vec4(position, 1.0);
    gl_Position.z = gl_Position.w;
    height = position.y;
    mapCoord_FS = uvs;
}