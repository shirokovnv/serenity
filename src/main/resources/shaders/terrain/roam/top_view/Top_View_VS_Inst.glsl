#version 100

layout (location = 0) in int tri_index;
layout(std430, binding = 1) buffer RoamSsbo {
    vec2 verts[];
};

uniform mat4 u_model;

void main() {
    vec2 a_local = verts[tri_index + gl_VertexID];

    vec2 position = vec2((1.0 - a_local.x), (1.0 - a_local.y)) * 2.0 - 1.0;

    gl_Position = u_model * vec4(position.x, 0.0, position.y, 1.0);
}