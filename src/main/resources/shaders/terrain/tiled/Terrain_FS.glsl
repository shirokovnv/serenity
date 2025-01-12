#version 430

in float height;
in vec2 mapCoord_FS;
out vec4 color;

uniform sampler2D normalmap;
uniform float scaleY;

void main()
{
    float scaledHeight = height / scaleY;
    vec3 normalColor = texture2D(normalmap, mapCoord_FS).rgb;
    vec3 heightColor = vec3(scaledHeight, scaledHeight, scaledHeight);

    color = vec4(normalColor * heightColor, 1);
}
