#version 430 core

#include <Frustum.glsl>

layout(triangles) in;
layout(line_strip, max_vertices = 16) out;

in vec3 v_low[];
in vec3 v_high[];

uniform mat4 u_viewProj;

void main() {
    vec3 low = v_low[0];
    vec3 high = v_high[0];

    if (!frustumCullingTest(u_viewProj, low, high)) {
        return;
    }

    float minX = low.x;
    float maxX = high.x;
    float minY = low.y;
    float maxY = high.y;
    float minZ = low.z;
    float maxZ = high.z;

    // corners
    vec3 p0 = vec3(minX, minY, minZ);
    vec3 p1 = vec3(maxX, minY, minZ);
    vec3 p2 = vec3(maxX, minY, maxZ);
    vec3 p3 = vec3(minX, minY, maxZ);

    vec3 p4 = vec3(minX, maxY, minZ);
    vec3 p5 = vec3(maxX, maxY, minZ);
    vec3 p6 = vec3(maxX, maxY, maxZ);
    vec3 p7 = vec3(minX, maxY, maxZ);

    // base bottom
    gl_Position = u_viewProj * vec4(p0, 1.0); EmitVertex();
    gl_Position = u_viewProj * vec4(p1, 1.0); EmitVertex();
    gl_Position = u_viewProj * vec4(p2, 1.0); EmitVertex();
    gl_Position = u_viewProj * vec4(p3, 1.0); EmitVertex();
    gl_Position = u_viewProj * vec4(p0, 1.0); EmitVertex();
    EndPrimitive();

    // base top
    gl_Position = u_viewProj * vec4(p4, 1.0); EmitVertex();
    gl_Position = u_viewProj * vec4(p5, 1.0); EmitVertex();
    gl_Position = u_viewProj * vec4(p6, 1.0); EmitVertex();
    gl_Position = u_viewProj * vec4(p7, 1.0); EmitVertex();
    gl_Position = u_viewProj * vec4(p4, 1.0); EmitVertex();
    EndPrimitive();

    // p0 -> p4
    gl_Position = u_viewProj * vec4(p0, 1.0); EmitVertex();
    gl_Position = u_viewProj * vec4(p4, 1.0); EmitVertex();
    EndPrimitive();

    // p1 -> p5
    gl_Position = u_viewProj * vec4(p1, 1.0); EmitVertex();
    gl_Position = u_viewProj * vec4(p5, 1.0); EmitVertex();
    EndPrimitive();

    // p2 -> p6
    gl_Position = u_viewProj * vec4(p2, 1.0); EmitVertex();
    gl_Position = u_viewProj * vec4(p6, 1.0); EmitVertex();
    EndPrimitive();

    // p3 -> p7
    gl_Position = u_viewProj * vec4(p3, 1.0); EmitVertex();
    gl_Position = u_viewProj * vec4(p7, 1.0); EmitVertex();
    EndPrimitive();
}