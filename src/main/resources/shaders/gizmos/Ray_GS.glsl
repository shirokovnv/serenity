#version 430 core

layout (points) in;
layout (line_strip, max_vertices = 2) out;

uniform vec3 uRayOrigin;
uniform vec3 uRayDirection;
uniform float uRayLength;
uniform mat4 uViewProjection;

out vec3 vColor;

void main()
{
    gl_Position = uViewProjection * vec4(uRayOrigin, 1.0f);
    EmitVertex();
    gl_Position = uViewProjection * vec4(uRayOrigin + uRayDirection * uRayLength, 1.0f);
    EmitVertex();

    EndPrimitive();
}