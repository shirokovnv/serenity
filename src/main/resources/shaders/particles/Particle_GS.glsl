#version 430 core

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

in vec4 g_Color[];
in float g_Size[];
out vec4 f_Color;

uniform mat4 u_Projection;

void main() {
    vec4 P = gl_in[0].gl_Position;

    // a: left-bottom 
    vec2 va = P.xy + vec2(-0.5, -0.5) * g_Size[0];
    gl_Position = u_Projection * vec4(va, P.zw);
    f_Color = g_Color[0];
    EmitVertex();

    // b: left-top
    vec2 vb = P.xy + vec2(-0.5, 0.5) * g_Size[0];
    gl_Position = u_Projection * vec4(vb, P.zw);
    f_Color = g_Color[0];
    EmitVertex();

    // d: right-bottom
    vec2 vd = P.xy + vec2(0.5, -0.5) * g_Size[0];
    gl_Position = u_Projection * vec4(vd, P.zw);
    f_Color = g_Color[0];
    EmitVertex();

    // c: right-top
    vec2 vc = P.xy + vec2(0.5, 0.5) * g_Size[0];
    gl_Position = u_Projection * vec4(vc, P.zw);
    f_Color = g_Color[0];
    EmitVertex();

    EndPrimitive();
}