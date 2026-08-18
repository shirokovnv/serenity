#version 430

#include <Transform.glsl>

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 locVector;
layout (location = 2) in vec3 scaleVector;
layout (location = 3) in float lodLevel;

out float mapHeight_FS;

uniform sampler2D u_heightmap;
uniform vec3 u_camPos;
uniform mat4 u_model;
uniform mat4 u_world;
uniform mat4 u_viewProj;
uniform float lodRanges[9];
uniform float resolution;

vec2 mesh_dim = vec2(resolution, resolution);

// Calculates the morph value from 0.0 to 1.0 given the distance
// from the camera to the vertex, and the current LOD level.
float morphValue(float dist){
    float low = 0.0;
    int iLevel = int(floor(lodLevel));
    if(iLevel != 0){
        low = lodRanges[iLevel - 1];
    }
    float high = lodRanges[iLevel];
    float delta = high - low;
    float factor = (dist - low) / delta;
    return clamp(factor / 0.5 - 1.0, 0.0, 1.0);
}

//// Morphs the vertex position in object-space given its
//// position in the mesh grid ranging from 0.0 to the mesh grid dimensions.
//// All positions only contain its x and z values, y values will be
//// retrieved later from the height texture.
vec2 morphVertex(vec2 vertex, vec2 mesh_pos, float morphValue){
    vec2 fraction = fract(mesh_pos * mesh_dim * 0.5 ) * 2.0 / mesh_dim;
    return vertex - fraction * morphValue;
}

void main() {
    // UV calculation
    vec2 uv = position;
    vec2 uvFine = position * scaleVector.xz + locVector;
    vec2 frac = fract(uv * (mesh_dim * 0.5)) / (mesh_dim * 0.5);
    vec2 uvCoarse = (uv - frac) * scaleVector.xz + locVector;

    // Sample heightmap for fine and coarse heights
    float fineH = texture2D(u_heightmap, uvFine).r;
    float coarseH = texture2D(u_heightmap, uvCoarse).r;

    vec2 localPosition = (u_model * createTransformMatrix(vec3(locVector.x, 0, locVector.y), scaleVector)
    * vec4(position.x, 0, position.y, 1)).xz;

    vec4 worldPosition = u_world * vec4(localPosition.x, fineH, localPosition.y, 1);

    float distance = length(u_camPos - worldPosition.xyz);
    float morphK = morphValue(distance);
    vec2 morphedPos = morphVertex(position, uv, morphK);

    // Interpolate height
    float finalHeight = mix(fineH, coarseH, morphK);

    vec2 localMorpedPos = (u_model * createTransformMatrix(vec3(locVector.x, 0, locVector.y), scaleVector)
    * vec4(morphedPos.x, 0, morphedPos.y, 1)).xz;

    vec4 worldMorphedPos = u_world * vec4(localMorpedPos.x, finalHeight, localMorpedPos.y, 1);

    mapHeight_FS = finalHeight;

    gl_Position = u_viewProj * worldMorphedPos;
}