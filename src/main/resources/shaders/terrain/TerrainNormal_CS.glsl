#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;
layout (binding = 0, rgba32f) uniform writeonly image2D normalmap;

uniform sampler2D heightmap;
uniform int width;
uniform int height;
uniform float normalStrength;

void main(void)
{
    // z0 -- z1 -- z2
    // |	 |     |
    // z3 -- h  -- z4
    // |     |     |
    // z5 -- z6 -- z7

    ivec2 x = ivec2(gl_GlobalInvocationID.xy);
    vec2 texCoord = gl_GlobalInvocationID.xy / vec2(width, height);

    vec2 texelSize = vec2(1.0 / width, 1.0 / height);

    float z0 = texture(heightmap, texCoord + vec2(-texelSize.x, -texelSize.y)).r;
    float z1 = texture(heightmap, texCoord + vec2(0, -texelSize.y)).r;
    float z2 = texture(heightmap, texCoord + vec2(texelSize.x, -texelSize.y)).r;
    float z3 = texture(heightmap, texCoord + vec2(-texelSize.x, 0)).r;
    float z4 = texture(heightmap, texCoord + vec2(texelSize.x, 0)).r;
    float z5 = texture(heightmap, texCoord + vec2(-texelSize.x, texelSize.y)).r;
    float z6 = texture(heightmap, texCoord + vec2(0, texelSize.y)).r;
    float z7 = texture(heightmap, texCoord + vec2(texelSize.x, texelSize.y)).r;

    vec3 normal;

    // Sobel Filter
    normal.z = 1.0 / normalStrength;
    normal.x = z0 + 2 * z3 + z5 - z2 - 2 * z4 - z7;
    normal.y = z0 + 2 * z1 + z2 - z5 - 2 * z6 - z7;

    imageStore(normalmap, x, vec4(normalize(normal), 1));
}