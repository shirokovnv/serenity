#version 430 core

layout (location = 0) in vec3 a_Position;

out vec3 f_TexCoord;

uniform mat4 u_World;
uniform mat4 u_View;
uniform mat4 u_Projection;

void main() {
    mat4 view = u_View;
    view[3][0] = 0.0;
    view[3][1] = 0.0;
    view[3][2] = 0.0;

    vec4 position = u_Projection * view * u_World * vec4(a_Position, 1.0);
    gl_Position = position.xyww;
    f_TexCoord = a_Position;
}