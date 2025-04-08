#version 430 core

layout (location = 0) in vec2 position;
out vec2 textureCoords;

uniform mat4 u_viewProjection;
uniform mat4 u_worldMatrix;
uniform mat4 u_localMatrix;
uniform float u_worldHeight = 0.0;

void main() {
    vec4 worldPos = u_worldMatrix * u_localMatrix * vec4(position.x, u_worldHeight, position.y, 1.0);
    gl_Position = u_viewProjection * worldPos;
    textureCoords = position;
}