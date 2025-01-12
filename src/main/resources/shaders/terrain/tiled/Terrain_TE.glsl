#version 430

layout(quads, fractional_odd_spacing, cw) in;

in vec2 mapCoord_TE[];
out vec2 mapCoord_GS;

uniform sampler2D heightmap;
uniform float scaleY;

void main(){
    // Barycentric coordinates (u, v)
    vec2 uv = gl_TessCoord.xy;

    // Interpolate position
    vec4 position = gl_in[0].gl_Position * (1.0 - uv.x) * (1.0 - uv.y) +
    gl_in[2].gl_Position * uv.x * (1.0 - uv.y) +
    gl_in[3].gl_Position * uv.x * uv.y +
    gl_in[1].gl_Position  * (1.0 - uv.x) * uv.y;

    // Interpolate uv-s
    vec2 mapCoord = mapCoord_TE[0] * (1.0 - uv.x) * (1.0 - uv.y) +
    mapCoord_TE[2] * uv.x * (1.0 - uv.y) +
    mapCoord_TE[3] * uv.x * uv.y +
    mapCoord_TE[1] * (1.0 - uv.x) * uv.y;

    float height = texture(heightmap, mapCoord).r;
    position.y = height * scaleY;

    mapCoord_GS = mapCoord;
    gl_Position = position;
}