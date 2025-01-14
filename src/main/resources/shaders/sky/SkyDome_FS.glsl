#version 430

layout(location = 0) out vec4 fragColor;

uniform sampler2D cloudTexture;

uniform vec4 lowColor = vec4(0.98, 0.37, 0.32, 1.0);
uniform vec4 highColor = vec4(0.08, 0.16, 0.32, 1.0);

in vec2 mapCoord_FS;
in float height;

void main()
{
    vec4 texColor = texture(cloudTexture, mapCoord_FS);
    vec4 skyColor = mix(lowColor, highColor, height);

    fragColor = texColor * 0.7 + skyColor * 0.3;
}