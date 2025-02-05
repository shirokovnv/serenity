#version 430 core

layout (points) in;
layout (line_strip, max_vertices = 24) out;

uniform vec3 uBoxCenter;
uniform vec3 uBoxSize;
uniform mat4 uViewProjection;

out vec3 vColor;

void main()
{
    vec3 halfSize = uBoxSize * 0.5;

    vec3 corners[8] = vec3[8](
    vec3(-halfSize.x, -halfSize.y, -halfSize.z),
    vec3( halfSize.x, -halfSize.y, -halfSize.z),
    vec3( halfSize.x,  halfSize.y, -halfSize.z),
    vec3(-halfSize.x,  halfSize.y, -halfSize.z),
    vec3(-halfSize.x, -halfSize.y,  halfSize.z),
    vec3( halfSize.x, -halfSize.y,  halfSize.z),
    vec3( halfSize.x,  halfSize.y,  halfSize.z),
    vec3(-halfSize.x,  halfSize.y,  halfSize.z)
    );

    int indices[24] = int[24](
    0, 1,
    1, 2,
    2, 3,
    3, 0,
    4, 5,
    5, 6,
    6, 7,
    7, 4,
    0, 4,
    1, 5,
    2, 6,
    3, 7
    );

    for (int i = 0; i < 24; i+=2) {
        gl_Position = uViewProjection * vec4(uBoxCenter + corners[indices[i]], 1.0);
        EmitVertex();
        gl_Position = uViewProjection * vec4(uBoxCenter + corners[indices[i+1]], 1.0);
        EmitVertex();

        EndPrimitive();
    }
}