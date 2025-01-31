#version 430 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) in vec3 aNormal;
layout (location = 3) in vec3 aTangent;
layout (location = 4) in vec4 aBoneIds;
layout (location = 5) in vec4 aBoneWeights;

out vec3 FragPos;
out vec3 Normal;
out vec2 TexCoords;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

//Uniform array of bones matrix
const int MAX_BONES = 200;
uniform mat4 bones[MAX_BONES];

void main()
{
    FragPos = vec3(model * vec4(aPos, 1.0));
    Normal = mat3(transpose(inverse(model))) * aNormal;
    TexCoords = aTexCoords;

    // Apply bone transformations
    mat4 skinningMatrix = bones[uint(aBoneIds.x)] * aBoneIds.z +
        bones[uint(aBoneIds.y)] * aBoneIds.w +
        bones[uint(aBoneWeights.x)] * aBoneWeights.z +
        bones[uint(aBoneWeights.y)] * aBoneWeights.w ;

    gl_Position = projection * view * model * skinningMatrix * vec4(aPos, 1.0);
}