#version 430

#include <Frustum.glsl>

layout (triangles) in;
layout (triangle_strip, max_vertices = 12) out;

uniform mat4 u_viewProj;
uniform sampler2D u_heightmap;
uniform vec2 u_textureSize = vec2(1024, 1024);

in vec2 mapCoord_GS[];
out float mapHeight_FS;
out vec3 mapNormal_FS;
out vec3 mapWorld_FS;
out vec2 mapCoord_FS;

vec3 calculateNormalCentralDifference(vec2 uv)
{
    vec2 texelSize = 1.0 / u_textureSize;
    vec2 clampedUV = clamp(uv, texelSize, 1.0 - texelSize);

    float dx = texture(u_heightmap, clampedUV + vec2(texelSize.x, 0)).r - texture(u_heightmap, clampedUV - vec2(texelSize.x, 0)).r;
    float dy = texture(u_heightmap, clampedUV + vec2(0, texelSize.y)).r - texture(u_heightmap, clampedUV - vec2(0, texelSize.y)).r;

    vec3 normal = vec3(-dx, -dy, 2.0 * texelSize.x);
    return normalize(normal);
}

void emitTriangleVertex(vec4 position, vec2 uv)
{
    float texHeight = texture(u_heightmap, position.xz).g;

    // COMPUTE NORMALS
    mapWorld_FS = position.xyz;
    mapNormal_FS = calculateNormalCentralDifference(uv);
    // TEXTURING
    mapCoord_FS = uv;
    mapHeight_FS = position.y;

    gl_Position = u_viewProj * position;
    EmitVertex();
}

void main()
{
    vec4 v0 = gl_in[0].gl_Position;
    vec4 v1 = gl_in[1].gl_Position;
    vec4 v2 = gl_in[2].gl_Position;

    vec4 m01 = (v0 + v1) * 0.5;
    vec4 m12 = (v1 + v2) * 0.5;
    vec4 m20 = (v2 + v0) * 0.5;

    vec2 uv0 = mapCoord_GS[0];
    vec2 uv1 = mapCoord_GS[1];
    vec2 uv2 = mapCoord_GS[2];

    vec2 uv01 = (uv0 + uv1) * 0.5;
    vec2 uv12 = (uv1 + uv2) * 0.5;
    vec2 uv20 = (uv2 + uv0) * 0.5;

    vec4 vertices[3] = {v0, v1, v2};
//    if (!frustumCullingTest(u_viewProj, vertices)) {
//        return;
//    }

    // T1: v0 - m01 - m20
    emitTriangleVertex(v0, uv0);
    emitTriangleVertex(m01, uv01);
    emitTriangleVertex(m20, uv20);
    EndPrimitive();

    // T2: v1 - m12 - m01
    emitTriangleVertex(v1, uv1);
    emitTriangleVertex(m12, uv12);
    emitTriangleVertex(m01, uv01);
    EndPrimitive();

    // T3: v2 - m20 - m12
    emitTriangleVertex(v2, uv2);
    emitTriangleVertex(m20, uv20);
    emitTriangleVertex(m12, uv12);
    EndPrimitive();

    // T4: m01 - m12 - m20
    emitTriangleVertex(m01, uv01);
    emitTriangleVertex(m12, uv12);
    emitTriangleVertex(m20, uv20);
    EndPrimitive();
}
