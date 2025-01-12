#version 430

in float height;
in vec2 mapCoord_FS;
out vec4 color;

uniform sampler2D normalmap;
uniform sampler2D blendmap;
uniform sampler2D grassTexture;
uniform sampler2D dirtTexture;
uniform sampler2D rockTexture;

uniform float scaleY;

void main()
{
    float scaledHeight = height / scaleY;
    vec3 normalColor = texture(normalmap, mapCoord_FS).rgb;
    vec3 heightColor = vec3(scaledHeight, scaledHeight, scaledHeight);

    vec3 grassColor = texture(grassTexture, mapCoord_FS).rgb;
    vec3 dirtColor = texture(dirtTexture, mapCoord_FS).rgb;
    vec3 rockColor = texture(rockTexture, mapCoord_FS).rgb;

    vec3 blendColor = texture(blendmap, mapCoord_FS).rgb;

    grassColor *= blendColor.r;
    dirtColor *= blendColor.g;
    rockColor *= blendColor.b;

    vec3 totalBlendColor = grassColor + dirtColor + rockColor;

    color = vec4(totalBlendColor * heightColor, 1);
}
