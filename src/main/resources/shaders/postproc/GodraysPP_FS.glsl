#version 430

in vec2 textureCoords;
out vec4 outColor;

uniform float exposure;
uniform float decay;
uniform float density;
uniform float weight;
uniform bool isLightOnScreen;
uniform vec2 lightScreenPosition;
uniform sampler2D firstPass;
uniform sampler2D secondPass;

const int NUM_SAMPLES = 100;

void main(void) {
    vec4 finalColor = texture(firstPass, textureCoords);
    outColor = vec4(0);

    if (isLightOnScreen) {
        vec2 deltaTextCoord = vec2(textureCoords - lightScreenPosition.xy);
        vec2 uvs = textureCoords;
        deltaTextCoord *= 1.0 / float(NUM_SAMPLES) * density;
        float illuminationDecay = 1.0;

        vec4 colorSample = vec4(0);
        outColor = vec4(0);
        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            uvs -= deltaTextCoord;
            colorSample = texture(secondPass, uvs);
            colorSample *= illuminationDecay * weight;
            outColor += colorSample;
            illuminationDecay *= decay;
        }
        outColor *= exposure;
    }
    outColor += finalColor;
}