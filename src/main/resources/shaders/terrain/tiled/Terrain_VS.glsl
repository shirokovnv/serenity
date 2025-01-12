#version 430

layout (location = 0) in vec2 position0;
layout (location = 1) in vec2 offset;

out vec2 mapCoord_TC;

uniform mat4 m_World;
uniform mat4 m_ViewProjection;
uniform float gridScale; // 1.0f / gridSize
uniform sampler2D heightmap;

void main() {
    vec2 localPosition = (position0 + offset) * gridScale;
    float height = texture(heightmap, localPosition).r;
    vec4 worldPosition = m_World * vec4(localPosition.x, height, localPosition.y, 1);

    mapCoord_TC = localPosition;
    gl_Position = worldPosition;
}