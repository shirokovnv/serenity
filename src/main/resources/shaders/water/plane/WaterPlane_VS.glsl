#version 430 core

layout (location = 0) in vec2 position;
out vec2 textureCoords;
out vec4 clipSpace;
out vec3 toCameraVector;
out vec3 fromLightVector;

uniform mat4 u_viewProjection;
uniform mat4 u_worldMatrix;
uniform mat4 u_localMatrix;
uniform vec3 u_cameraPosition;
uniform vec3 u_lightPosition;
uniform float u_worldHeight = 0.0;

void main() {
    vec4 worldPos = u_worldMatrix * u_localMatrix * vec4(position.x, u_worldHeight, position.y, 1.0);
    gl_Position = u_viewProjection * worldPos;
    textureCoords = position;
    clipSpace = gl_Position;
    toCameraVector = u_cameraPosition - worldPos.xyz;
    fromLightVector = worldPos.xyz - u_lightPosition;
}