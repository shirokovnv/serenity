#version 430

layout(vertices = 4) out;

in vec2 mapCoord_TC[];
in vec4 lodVector_TC[];

out vec2 mapCoord_TE[];

const int AB = 0;
const int BC = 3;
const int CD = 2;
const int DA = 1;

uniform int u_tessFactor;

void main() {
    if (true) {
        // x - AB, y - BC, z - CD, w - DA
        vec4 lv = lodVector_TC[gl_InvocationID];

        float maxLevel = max(lv.x, lv.y);
        maxLevel = max(maxLevel, lv.z);
        maxLevel = max(maxLevel, lv.w);
        maxLevel = max(maxLevel, u_tessFactor);

        gl_TessLevelOuter[AB] = lv.x;
        gl_TessLevelOuter[BC] = lv.y;
        gl_TessLevelOuter[CD] = lv.z;
        gl_TessLevelOuter[DA] = lv.w;

        gl_TessLevelInner[0] = maxLevel;
        gl_TessLevelInner[1] = maxLevel;
    }

    mapCoord_TE[gl_InvocationID] = mapCoord_TC[gl_InvocationID];
    gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
}