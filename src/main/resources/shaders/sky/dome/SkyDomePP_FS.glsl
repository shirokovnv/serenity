#version 430

layout(location = 0) out vec4 fragColor;

in vec4 sunlitColor;

void main()
{
    fragColor = sunlitColor;
}