#version 430

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uvs;

#include <Atmosphere.glsl>

uniform mat4 m_WorldViewProjection = mat4(1.0);
uniform vec3 sunVector;
uniform vec3 sunColor;
uniform float sunIntensity;
uniform vec2 cloudAnimationOffset = vec2(0);

out vec2 mapCoord_FS;
out float height;
out vec4 sunlitColor;

const float vCameraNearFar = 0.001f;
const float intensityFactor = 100.0f;
const float cloudAnimationSpeed = 0.02f;

void main()
{
    gl_Position = m_WorldViewProjection * vec4(position, 1.0);
    gl_Position.z = gl_Position.w;

    height = position.y;
    mapCoord_FS = uvs + cloudAnimationOffset;

    // eye vector and view distance s
    vec3 eyeVector = normalize(position.xyz);
    float s = 3000.0;

    // compute angle and phase1 theta
    float cosTheta = dot(eyeVector, -sunVector.xyz);
    float p1Theta = (cosTheta * cosTheta) + atm.vConstants.x;

    // compute extinction term E
    // -(beta_1+beta_2) * s * log_2 e
    vec4 E = -atm.vSumBeta1Beta2 * s * atm.vConstants.y;
    E.x = exp(E.x);
    E.y = exp(E.y);
    E.z = exp(E.z);

    // compute phase2 theta as
    // (1-g^2)/(1+g-2g*cos(theta))^(3/2)
    // notes:
    // theta is 180 - actual theta (this corrects for sign)
    // atm.vHG = [1-g^2, 1+g, 2g]
    float p2Theta = (atm.vHG.z * cosTheta) + atm.vHG.y;
    p2Theta = 1.0 / sqrt(p2Theta);
    p2Theta = pow(p2Theta, 3.0) * atm.vHG.x;

    // compute inscattering (I) as
    //(Beta_1 * Phase_1(theta) + Beta_2 * Phase_2(theta)) *
    //[1-exp(-Beta_1*s).exp(-Beta_2*s)] / (Beta_1 + Beta_2)
    //
    // or, more simply
    //
    // (vBetaD1*p1Theta + vBetaD1*p2Theta) *
    // (1-E) * atm.vRcpSumBeta1Beta2

    vec4 I = (atm.vBetaD1 * p1Theta) +
    (atm.vBetaD2 * p2Theta);
    I = I * (atm.vConstants.x - E) * atm.vRcpSumBeta1Beta2;

    // scale inscatter and extinction (optional)
    I = clamp(I * atm.vTermMultipliers.x, 0, 1);
    E = clamp(E * atm.vTermMultipliers.y, 0, 1); //*atm.vSoilReflectivity;

    float falloff = s * vCameraNearFar;
    falloff = falloff * falloff * falloff * falloff;

    // apply sunlight color
    // and strength to each term
    // and output
    vec4 vI = clamp(I, 0, 1) * (vec4(sunColor.xyz * sunIntensity * intensityFactor, 1));
    vI.w = falloff;
    vec4 vE = E * vec4(sunColor, 1) * sunIntensity * intensityFactor;

    sunlitColor = vI;
}