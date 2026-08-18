#version 430

layout (location = 0) in int tri_index;
layout(std430, binding = 1) buffer RoamSsbo {
    vec2 verts[];
};

uniform mat4 u_model;
uniform mat4 u_world;
uniform sampler2D u_heightmap;

out vec2 mapCoord_GS;

void main() {
    vec2 a_local = verts[tri_index + gl_VertexID];

    vec2 localPosition = (u_model * vec4(a_local.x, 0, a_local.y, 1)).xz;
    float height = texture(u_heightmap, localPosition).g;
    mapCoord_GS = localPosition;

    gl_Position = u_world * vec4(localPosition.x, height, localPosition.y, 1);
}