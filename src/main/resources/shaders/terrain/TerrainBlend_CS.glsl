#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;
layout (binding = 0, rgba32f) uniform writeonly image2D blendmap;

uniform sampler2D heightmap;
uniform sampler2D normalmap;
uniform int width;
uniform int height;

uniform float minElevation[4];
uniform float maxElevation[4];
uniform float minSlope[4];
uniform float maxSlope[4];
uniform float strength[4];
uniform int elevationDataCount;

// these 4 mask values control
// which color component of the
// blend image we write to
vec4 mask[4] = vec4[4](
vec4(1.0f, 0.0f, 0.0f, 0.0f),
vec4(0.0f, 1.0f, 0.0f, 0.0f),
vec4(0.0f, 0.0f, 1.0f, 0.0f),
vec4(0.0f, 0.0f, 0.0f, 1.0f)
);

float computeWeight(
    float value,
    float minExtent,
    float maxExtent)
{
    float weight = 0.0f;

    if (value >= minExtent && value <= maxExtent) {

        float span = maxExtent - minExtent;
        weight = value - minExtent;

        // convert to a 0-1 range value
        // based on its distance to the midpoint
        // of the range extents
        weight *= 1.0f / span;
        weight -= 0.5f;
        weight *= 2.0f;

        // square the result for non-linear falloff
        weight *= weight;

        // invert and bound-check the result
        weight = 1.0f - abs(weight);
        weight = clamp(weight, 0.001f, 1.0f);
    }

    return weight;
}

void main(void)
{
    ivec2 x = ivec2(gl_GlobalInvocationID.xy);
    vec2 texCoord = gl_GlobalInvocationID.xy / vec2(width, height);

    float height = texture(heightmap, texCoord).r;
    vec3 normal = normalize(texture(normalmap, texCoord).rgb);

    float totalBlend = 0.0f;
    vec4 blendFactors = vec4(0, 0, 0, 0);

    for (int i = 0; i < elevationDataCount; ++i) {
        // compute a weight based on elevation
        float elevationScale = computeWeight(
            height,
            minElevation[i],
            maxElevation[i]);

        // compute a weight based on slope
        float slopeScale = computeWeight(
            normal.z,
            minSlope[i],
            maxSlope[i]);

        // combine the two with the relative
        // strength of this surface type
        float scale =
        strength[i] *
        elevationScale *
        slopeScale;

        // write the result to the proper
        // channel of the blend factor vector
        blendFactors += mask[i] * scale;

        // and remember the total weight
        totalBlend += scale;
    }

    // balance the data (so they add up to 1.0)
    float blendScale = 1.0f / totalBlend;

    // now compute the actual color by
    // multiplying each channel
    // by the blend scale
    blendFactors *= blendScale;

    imageStore(blendmap, x, blendFactors);
}