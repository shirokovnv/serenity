#version 430

layout(quads, equal_spacing, cw) in;

in vec2 mapCoord_TE[];
out vec2 mapCoord_GS;

void main(){

    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;

    // world position
    vec4 position =
    ((1 - u) * (1 - v) * gl_in[0].gl_Position +
    u * (1 - v) * gl_in[2].gl_Position +
    u * v * gl_in[3].gl_Position +
    (1 - u) * v * gl_in[1].gl_Position);

    vec2 mapCoord =
    ((1 - u) * (1 - v) * mapCoord_TE[0] +
    u * (1 - v) * mapCoord_TE[2] +
    u * v * mapCoord_TE[3] +
    (1 - u) * v * mapCoord_TE[1]);

    mapCoord_GS = mapCoord;

    gl_Position = position;
}