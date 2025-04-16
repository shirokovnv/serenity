#version 430 core

in struct Vertex {
    vec4 color;
    vec2 texCoords1;
    vec2 texCoords2;
    float blend;
} f_Vertex;

layout (location = 0) out vec4 o_Color;

uniform sampler2D u_Texture;
uniform bool u_HasTexture;

void main()
{
    if (u_HasTexture) {
        vec4 color1 = texture(u_Texture, f_Vertex.texCoords1);
        vec4 color2 = texture(u_Texture, f_Vertex.texCoords2);
        o_Color = mix(color1, color2, f_Vertex.blend);
    } else {
        o_Color = f_Vertex.color;
    }
}