#version 430

layout (vertices = 4) out;

in vec2 mapCoord_TC[];
out vec2 mapCoord_TE[];

uniform mat4 m_View;
uniform float minDistance = 1.0;
uniform float maxDistance = 1500.0;
uniform float minLOD = 1.0;
uniform float maxLOD = 16.0;

void main() {
    if (gl_InvocationID == 0) {

        vec3 quadMid = vec3(
        gl_in[0].gl_Position +
        gl_in[1].gl_Position +
        gl_in[2].gl_Position +
        gl_in[3].gl_Position
        ) * 0.25;

        vec3 abMid = vec3(gl_in[0].gl_Position + gl_in[1].gl_Position) * 0.5;
        vec3 bcMid = vec3(gl_in[1].gl_Position + gl_in[3].gl_Position) * 0.5;
        vec3 cdMid = vec3(gl_in[3].gl_Position + gl_in[2].gl_Position) * 0.5;
        vec3 daMid = vec3(gl_in[2].gl_Position + gl_in[0].gl_Position) * 0.5;

        // Step 1: transform the vertex to view space
        vec4 viewSpacePositionAB = m_View * vec4(abMid, 1);
        vec4 viewSpacePositionBC = m_View * vec4(bcMid, 1);
        vec4 viewSpacePositionCD = m_View * vec4(cdMid, 1);
        vec4 viewSpacePositionDA = m_View * vec4(daMid, 1);

        vec4 viewSpacePositionMid = m_View * vec4(quadMid, 1);

        // Step 2: calculate the length of the view space vector to get the distance
        float lenAB = length(viewSpacePositionAB.xyz);
        float lenBC = length(viewSpacePositionBC.xyz);
        float lenCD = length(viewSpacePositionCD.xyz);
        float lenDA = length(viewSpacePositionDA.xyz);

        float lenMid = length(viewSpacePositionMid.xyz);

        // Step 3: map the distance to [0,1]
        lenAB = clamp((lenAB - minDistance) / (maxDistance - minDistance), 0.0, 1.0);
        lenBC = clamp((lenBC - minDistance) / (maxDistance - minDistance), 0.0, 1.0);
        lenCD = clamp((lenCD - minDistance) / (maxDistance - minDistance), 0.0, 1.0);
        lenDA = clamp((lenDA - minDistance) / (maxDistance - minDistance), 0.0, 1.0);

        lenMid = clamp((lenMid - minDistance) / (maxDistance - minDistance), 0.0, 1.0);

        // Step 4: compute tessellation levels
        float tessLevelAB = mix(maxLOD, minLOD, lenAB);
        float tessLevelBC = mix(maxLOD, minLOD, lenBC);
        float tessLevelCD = mix(maxLOD, minLOD, lenCD);
        float tessLevelDA = mix(maxLOD, minLOD, lenDA);

        float tessLevelMid = mix(maxLOD, minLOD, lenMid);

        float tessLevelInner0 = max(tessLevelDA, tessLevelBC);
        tessLevelInner0 = max(tessLevelInner0, tessLevelMid);

        float tessLevelInner1 = max(tessLevelAB, tessLevelCD);
        tessLevelInner1 = max(tessLevelInner1, tessLevelMid);

        // Step 5: set the outer edge tessellation levels
        gl_TessLevelOuter[0] = tessLevelAB;
        gl_TessLevelOuter[3] = tessLevelBC;
        gl_TessLevelOuter[2] = tessLevelCD;
        gl_TessLevelOuter[1] = tessLevelDA;

        // Step 6: set the inner tessellation levels
        gl_TessLevelInner[0] = tessLevelInner0;
        gl_TessLevelInner[1] = tessLevelInner1;
    }

    mapCoord_TE[gl_InvocationID] = mapCoord_TC[gl_InvocationID];
    gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
}