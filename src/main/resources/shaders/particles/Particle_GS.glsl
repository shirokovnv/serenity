#version 430 core

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

in struct VertexIn {
    vec4 color;
    float size;
    float life;
} g_Vertex[];

out struct VertexOut {
    vec4 color;
    vec2 texCoords1;
    vec2 texCoords2;
    float blend;
} f_Vertex;

uniform int u_TexNumRows;
uniform mat4 u_Projection;

vec2 calculateTextureOffset(int index) {
    int column = index % u_TexNumRows;
    int row = index / u_TexNumRows;

    return vec2(column, row) / u_TexNumRows;
}

void prepareTextureInfo(out int index1, out int index2, out float atlasProgression) {
    int stageCount = u_TexNumRows * u_TexNumRows;
    atlasProgression = g_Vertex[0].life * stageCount;
    index1 = int(floor(atlasProgression));
    index2 = min(index1 + 1, stageCount - 1);
}

void main() {
    vec4 P = gl_in[0].gl_Position;

    int index1, index2;
    float atlasProgression;
    prepareTextureInfo(index1, index2, atlasProgression);

    f_Vertex.blend = fract(atlasProgression);

    // a: left-bottom 
    vec2 va = P.xy + vec2(-0.5, -0.5) * g_Vertex[0].size;
    gl_Position = u_Projection * vec4(va, P.zw);
    f_Vertex.color = g_Vertex[0].color;
    f_Vertex.texCoords1 = vec2(0, 0) / u_TexNumRows + calculateTextureOffset(index1);
    f_Vertex.texCoords2 = vec2(0, 0) / u_TexNumRows + calculateTextureOffset(index1);
    EmitVertex();

    // b: left-top
    vec2 vb = P.xy + vec2(-0.5, 0.5) * g_Vertex[0].size;
    gl_Position = u_Projection * vec4(vb, P.zw);
    f_Vertex.color = g_Vertex[0].color;
    f_Vertex.texCoords1 = vec2(0, 1) / u_TexNumRows + calculateTextureOffset(index1);
    f_Vertex.texCoords2 = vec2(0, 1) / u_TexNumRows + calculateTextureOffset(index1);
    EmitVertex();

    // d: right-bottom
    vec2 vd = P.xy + vec2(0.5, -0.5) * g_Vertex[0].size;
    gl_Position = u_Projection * vec4(vd, P.zw);
    f_Vertex.color = g_Vertex[0].color;
    f_Vertex.texCoords1 = vec2(1, 0) / u_TexNumRows + calculateTextureOffset(index1);
    f_Vertex.texCoords2 = vec2(1, 0) / u_TexNumRows + calculateTextureOffset(index1);
    EmitVertex();

    // c: right-top
    vec2 vc = P.xy + vec2(0.5, 0.5) * g_Vertex[0].size;
    gl_Position = u_Projection * vec4(vc, P.zw);
    f_Vertex.color = g_Vertex[0].color;
    f_Vertex.texCoords1 = vec2(1, 1) / u_TexNumRows + calculateTextureOffset(index1);
    f_Vertex.texCoords2 = vec2(1, 1) / u_TexNumRows + calculateTextureOffset(index1);
    EmitVertex();

    EndPrimitive();
}