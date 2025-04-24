#version 430 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec3 a_Normal;
layout (location = 2) in float a_Occlusion;

out vec3 v_Normal;
out float v_Occlusion;

void main() {
    v_Normal = a_Normal;
    v_Occlusion = a_Occlusion;
    gl_Position = vec4(a_Position, 1.0f);
}