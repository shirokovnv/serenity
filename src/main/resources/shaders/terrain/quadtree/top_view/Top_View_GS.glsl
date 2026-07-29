#version 430

#include <Frustum.glsl>

layout(triangles) in;
layout(line_strip, max_vertices = 5) out;

in vec2 v_local[];
in vec3 v_low[];
in vec3 v_high[];

uniform mat4x4 u_viewProj;

void main() {
    vec3 low = v_low[0];
    vec3 high = v_high[0];

    if (!frustumCullingTest(u_viewProj, low, high)) {
//        return;
    }

    float minX = min(min(v_local[0].x, v_local[1].x), v_local[2].x);
    float maxX = max(max(v_local[0].x, v_local[1].x), v_local[2].x);
    float minY = min(min(v_local[0].y, v_local[1].y), v_local[2].y);
    float maxY = max(max(v_local[0].y, v_local[1].y), v_local[2].y);

    // BL -> BR -> TR -> TL -> BL
    gl_Position = vec4(minX, minY, -1.0, 1.0); EmitVertex(); // BL
    gl_Position = vec4(maxX, minY, -1.0, 1.0); EmitVertex(); // BR
    gl_Position = vec4(maxX, maxY, -1.0, 1.0); EmitVertex(); // TR
    gl_Position = vec4(minX, maxY, -1.0, 1.0); EmitVertex(); // TL
    gl_Position = vec4(minX, minY, -1.0, 1.0); EmitVertex(); // BL
    EndPrimitive();
}