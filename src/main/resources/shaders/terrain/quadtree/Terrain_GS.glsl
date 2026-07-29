#version 430

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;

in vec2 mapCoord_GS[];
out vec3 mapWorld_FS;
out vec2 mapCoord_FS;
out float mapHeight_FS;

uniform mat4 u_viewProj;
uniform sampler2D u_heightmap;
uniform float u_scaleY;

void main() {

    float h0 = texture(u_heightmap, mapCoord_GS[0]).r;
    float h1 = texture(u_heightmap, mapCoord_GS[1]).r;
    float h2 = texture(u_heightmap, mapCoord_GS[2]).r;

    for (int i = 0; i < gl_in.length(); ++i)
    {
        float height = texture(u_heightmap, mapCoord_GS[i]).r;
        vec4 position = gl_in[i].gl_Position;
        position.y = height * u_scaleY;
        mapHeight_FS = position.y;
        mapCoord_FS = mapCoord_GS[i];
        mapWorld_FS = position.xyz;

        gl_Position = u_viewProj * position;
        EmitVertex();
    }

    EndPrimitive();
}