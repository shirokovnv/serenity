#version 430

#include <Frustum.glsl>

layout(triangles) in;
layout(line_strip, max_vertices = 16) out;

uniform mat4x4 u_viewProj;

void main() {
    vec4 v0 = gl_in[0].gl_Position;
    vec4 v1 = gl_in[1].gl_Position;
    vec4 v2 = gl_in[2].gl_Position;

    vec3 bmin = min(min(v0.xyz, v1.xyz), v2.xyz);
    vec3 bmax = max(max(v0.xyz, v1.xyz), v2.xyz);

    if (!frustumCullingTest(u_viewProj, bmin, bmax)) {
        return;
    }

    float minX = bmin.x;
    float maxX = bmax.x;
    float minY = bmin.y;
    float maxY = bmax.y;
    float minZ = bmin.z;
    float maxZ = bmax.z;

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