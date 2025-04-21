#version 430 core

layout (triangles) in;
layout (line_strip, max_vertices = 6) out;

out vec3 g_Normal;

uniform mat4 u_ViewProjection;

const float u_NormalLength = 1.0f;
const float u_ArrowSize = 0.3f;
const float u_ArrowLengthRatio = 0.5f;

void main() {
    vec3 v0 = gl_in[0].gl_Position.xyz;
    vec3 v1 = gl_in[1].gl_Position.xyz;
    vec3 v2 = gl_in[2].gl_Position.xyz;

    vec3 edge1 = v1 - v0;
    vec3 edge2 = v2 - v0;

    g_Normal = normalize(cross(edge1, edge2));

    vec3 center = (v0 + v1 + v2) / 3.0;
    vec3 normalEnd = center + g_Normal * u_NormalLength;

    // Line
    gl_Position = u_ViewProjection * vec4(center, 1.0);
    EmitVertex();
    gl_Position = u_ViewProjection * vec4(normalEnd, 1.0);
    EmitVertex();
    EndPrimitive();

    // Arrowhead
    vec3 arrowBase = normalEnd;
    float arrowRealLength = u_NormalLength * u_ArrowLengthRatio; // Arrow length relative to normal length
    vec3 arrowTip = normalEnd + g_Normal * arrowRealLength;

    // Choose a vector that's not parallel to the normal
    vec3 arbitraryVector = abs(g_Normal.x) > 0.9 ? vec3(0.0, 1.0, 0.0) : vec3(1.0, 0.0, 0.0);

    // Calculate two vectors perpendicular to the normal (Arrow sides)
    vec3 arrowSide1 = normalize(cross(g_Normal, arbitraryVector)) * u_ArrowSize;
    vec3 arrowSide2 = -arrowSide1;

    // Arrow sides must be at the same point as arrowBase. To draw arrow, use line_strip
    gl_Position = u_ViewProjection * vec4(arrowTip, 1.0);
    EmitVertex();
    gl_Position = u_ViewProjection * vec4(arrowBase + arrowSide1, 1.0);
    EmitVertex();
    gl_Position = u_ViewProjection * vec4(arrowBase + arrowSide2, 1.0);
    EmitVertex();
    gl_Position = u_ViewProjection * vec4(arrowTip, 1.0);
    EmitVertex();
    EndPrimitive();
}