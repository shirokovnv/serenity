#version 430

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;

uniform mat4 m_ViewProjection;
uniform sampler2D heightmap;

in vec2 mapCoord_GS[];
out vec2 mapCoord_FS;
out float height;

/** FRUSTUM **/

/**
 * Extract Frustum Planes from MVP Matrix
 *
 * Based on "Fast Extraction of Viewing Frustum Planes from the World-
 * View-Projection Matrix", by Gil Gribb and Klaus Hartmann.
 * This procedure computes the planes of the frustum and normalizes
 * them.
 */
vec4[6] loadFrustum(mat4 mvp)
{
    vec4 planes[6];

    for (int i = 0; i < 3; ++i)
    for (int j = 0; j < 2; ++j) {
        planes[i*2+j].x = mvp[0][3] + (j == 0 ? mvp[0][i] : -mvp[0][i]);
        planes[i*2+j].y = mvp[1][3] + (j == 0 ? mvp[1][i] : -mvp[1][i]);
        planes[i*2+j].z = mvp[2][3] + (j == 0 ? mvp[2][i] : -mvp[2][i]);
        planes[i*2+j].w = mvp[3][3] + (j == 0 ? mvp[3][i] : -mvp[3][i]);
        planes[i*2+j]*= length(planes[i*2+j].xyz);
    }

    return planes;
}

/**
 * Negative Vertex of an AABB
 *
 * This procedure computes the negative vertex of an AABB
 * given a normal.
 * See the View Frustum Culling tutorial @ LightHouse3D.com
 * http://www.lighthouse3d.com/tutorials/view-frustum-culling/geometric-approach-testing-boxes-ii/
 */
vec3 negativeVertex(vec3 bmin, vec3 bmax, vec3 n)
{
    bvec3 b = greaterThan(n, vec3(0));
    return mix(bmin, bmax, b);
}

/**
 * Frustum-AABB Culling Test
 *
 * This procedure returns true if the AABB is either inside, or in
 * intersection with the frustum, and false otherwise.
 * The test is based on the View Frustum Culling tutorial @ LightHouse3D.com
 * http://www.lighthouse3d.com/tutorials/view-frustum-culling/geometric-approach-testing-boxes-ii/
 */
bool frustumCullingTest(in const vec4 planes[6], vec3 bmin, vec3 bmax)
{
    float a = 1.0f;

    for (int i = 0; i < 6 && a >= 0.0f; ++i) {
        vec3 n = negativeVertex(bmin, bmax, planes[i].xyz);

        a = dot(vec4(n, 1.0f), planes[i]);
    }

    return (a >= 0.0);
}

bool frustumCullingTest(mat4 mvp, vec3 bmin, vec3 bmax)
{
    return frustumCullingTest(loadFrustum(mvp), bmin, bmax);
}

void main() {
    vec3 minVec = vec3(1e38);
    vec3 maxVec = vec3(-1e38);

    /** FRUSTUM CULLING **/
    vec3 vertices[4];
    for (int i = 0; i < gl_in.length(); ++i)
    {
        minVec = min(minVec, gl_in[i].gl_Position.xyz);
        maxVec = max(maxVec, gl_in[i].gl_Position.xyz);
    }

    if (frustumCullingTest(m_ViewProjection, minVec, maxVec)) {
        for (int i = 0; i < gl_in.length(); ++i)
        {
            vec4 position = gl_in[i].gl_Position;
            height = position.y;

            mapCoord_FS = mapCoord_GS[i];

            gl_Position = m_ViewProjection * position;

            EmitVertex();
        }

        EndPrimitive();
    }
}