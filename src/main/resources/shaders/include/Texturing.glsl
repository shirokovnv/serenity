vec3 triplanarMappingRGB(sampler2D tex, vec3 worldPos, vec3 normal, float scale) {
    vec3 blendWeights = abs(normal);

    blendWeights = max(blendWeights, 0.001);
    blendWeights /= (blendWeights.x + blendWeights.y + blendWeights.z);

    vec2 uvX = worldPos.yz / scale;
    vec2 uvY = worldPos.xz / scale;
    vec2 uvZ = worldPos.xy / scale;

    vec3 colX = texture(tex, uvX).rgb;
    vec3 colY = texture(tex, uvY).rgb;
    vec3 colZ = texture(tex, uvZ).rgb;

    return colX * blendWeights.x + colY * blendWeights.y + colZ * blendWeights.z;
}