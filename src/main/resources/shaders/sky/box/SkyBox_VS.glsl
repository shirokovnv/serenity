#version 430 core

layout (location = 0) in vec3 a_Position;

out vec3 f_TexCoord;

uniform mat4 u_WorldViewProjection;

void main() {
    vec4 position = u_WorldViewProjection * vec4(a_Position, 1.0);
    gl_Position = position.xyww;
    f_TexCoord = a_Position;
}