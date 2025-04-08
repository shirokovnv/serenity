#version 430

#include <Frustum.glsl>

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

uniform mat4 m_ViewProjection;
uniform mat4 m_LightViewProjection = mat4(1);
uniform sampler2D heightmap;
uniform sampler2D normalmap;
uniform sampler2D blendmap;
uniform vec3 cameraPosition;
uniform vec4 clipPlane;
uniform float tbnRange;

in vec2 mapCoord_GS[];
out vec2 mapCoord_FS;
out vec3 position_FS;
out vec4 positionLightSpace_FS;
out vec3 normal_FS;
out vec3 tangent_FS;

struct Material {
    sampler2D diffusemap;
    sampler2D normalmap;
    sampler2D displacementmap;
    float verticalScale;
    float horizontalScale;
};

uniform Material materials[3];
// 0 - grass
// 1 - dirt
// 2 - rock

vec3 calcTangent()
{
    vec3 v0 = gl_in[0].gl_Position.xyz;
    vec3 v1 = gl_in[1].gl_Position.xyz;
    vec3 v2 = gl_in[2].gl_Position.xyz;

    // edges of the face/triangle
    vec3 e1 = v1 - v0;
    vec3 e2 = v2 - v0;

    vec2 uv0 = mapCoord_GS[0];
    vec2 uv1 = mapCoord_GS[1];
    vec2 uv2 = mapCoord_GS[2];

    vec2 deltaUV1 = uv1 - uv0;
    vec2 deltaUV2 = uv2 - uv0;

    float r = 1.0 / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);

    return normalize((e1 * deltaUV2.y - e2 * deltaUV1.y) * r);
}

void main() {
    vec3 displacement[3];
    vec3 tangent;

    for (int i = 0; i < 3; ++i) {
        displacement[i] = vec3(0, 0, 0);
    }

    vec3 minVec = vec3(1e38);
    vec3 maxVec = vec3(-1e38);

    vec3 vertices[4];
    for (int i = 0; i < gl_in.length(); ++i)
    {
        minVec = min(minVec, gl_in[i].gl_Position.xyz);
        maxVec = max(maxVec, gl_in[i].gl_Position.xyz);
    }

    if (frustumCullingTest(m_ViewProjection, minVec, maxVec)) {
        float dist = (distance(gl_in[0].gl_Position.xyz, cameraPosition)
        + distance(gl_in[1].gl_Position.xyz, cameraPosition)
        + distance(gl_in[2].gl_Position.xyz, cameraPosition)) / 3;

        if (dist < tbnRange) {

            tangent = calcTangent();

            for (int k = 0; k < gl_in.length(); k++) {

                displacement[k] = vec3(0, 1, 0);

                float height = gl_in[k].gl_Position.y;

                vec3 normal = normalize(texture(normalmap, mapCoord_GS[k]).rbg);

                vec4 blendValues = texture(blendmap, mapCoord_GS[k]).rgba;

                float[4] blendValueArray = float[](blendValues.r, blendValues.g, blendValues.b, blendValues.a);

                float scale = 0;
                for (int i = 0; i < 3; i++) {
                    scale += texture(materials[i].displacementmap, mapCoord_GS[k]
                    * materials[i].horizontalScale).r
                    * materials[i].verticalScale
                    * blendValueArray[i];
                }

                float attenuation = clamp(-distance(gl_in[k].gl_Position.xyz, cameraPosition) / (tbnRange - 50) + 1, 0.0, 1.0);
                scale *= attenuation;

                displacement[k] *= scale;
            }
        }

        for (int i = 0; i < gl_in.length(); ++i)
        {
            vec4 position = gl_in[i].gl_Position;

            mapCoord_FS = mapCoord_GS[i];
            position_FS = (position.xyz + displacement[i]);
            positionLightSpace_FS = (m_LightViewProjection * position);
            normal_FS = normalize(texture(normalmap, mapCoord_GS[i]).rbg);
            tangent_FS = tangent;

            gl_Position = m_ViewProjection * position;
            gl_ClipDistance[0] = dot(position, clipPlane);

            EmitVertex();
        }

        EndPrimitive();
    }
}