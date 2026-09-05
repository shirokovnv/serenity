struct AtmosphereConstants {
    vec4 vBeta1;
    vec4 vBeta2;
    vec4 vBetaD1;
    vec4 vBetaD2;
    vec4 vSumBeta1Beta2;
    vec4 vLog2eBetaSum;
    vec4 vRcpSumBeta1Beta2;
    vec4 vHG;
    vec4 vConstants;
    vec4 vTermMultipliers;
    vec4 vSoilReflectivity;
};

layout (std430, binding = 0) buffer AtmosphereConstantsSsbo {
    AtmosphereConstants atm;
};

// TODO: better way to calculate proper vExt, vIns?
const float atmScale = 100.0f;

vec3 atmosphericExtinction(vec3 eyeVector, vec3 sunVector, float s)
{
    vec3 vExt;

    // compute cosine of theta angle
    float cosTheta = dot(eyeVector, sunVector);

    // compute extinction term vExt
    // -(beta_1+beta_2) * s * log_2 e
    vExt = -atm.vSumBeta1Beta2.xyz * s * atm.vConstants.y;
    vExt.x = exp(vExt.x);
    vExt.y = exp(vExt.y);
    vExt.z = exp(vExt.z);

    // scale extinction prior to its use
    // (this is optional)
    //vExt = vExt*atm.vTermMultipliers.y*atm.vSoilReflectivity;
    return vExt;
}

void atmosphericLighting(
    vec3 eyeVector,
    vec3 sunVector,
    vec4 sunColor,
    vec3 norm,
    float s,
    out vec4 vExt,
    out vec4 vIns
)
{
    //
    // This shader assumes a world-space vertex is
    // provided, and distance values represent
    // real-world distances. A set of pre-
    // calculated atmospheric data is provided
    // in the atm structure.
    //

    // compute cosine of theta angle
    float cosTheta = dot(eyeVector, sunVector);

    // compute extinction term vExt
    // -(beta_1+beta_2) * s * log_2 e
    vExt = -atm.vSumBeta1Beta2 * s * atm.vConstants.y;
    vExt.x = exp(vExt.x);
    vExt.y = exp(vExt.y);
    vExt.z = exp(vExt.z);
    vExt.w = 0.0f;

    // Compute theta terms used by inscattering.
    // compute phase2 theta as
    // (1-g^2)/(1+g-2g*cos(theta))^(3/2)
    // atm.vHG = [1-g^2, 1+g, 2g]
    float p1Theta = (cosTheta * cosTheta) + atm.vConstants.x;
    float p2Theta = (atm.vHG.z * cosTheta) + atm.vHG.y;
    p2Theta = 1.0f / (sqrt(p2Theta));
    p2Theta = (p2Theta * p2Theta * p2Theta) * atm.vHG.x;


    // compute inscattering (vIns) as
    // (vBetaD1*p1Theta + vBetaD1*p2Theta) *
    // (1-vExt) * atm.vRcpSumBeta1Beta2
    //
    // atm.vRcpSumBeta1Beta2 =
    // 1.0f/ (Rayleigh+Mie)
    vIns = ((atm.vBetaD1 * p1Theta) +
    (atm.vBetaD2 * p2Theta))
    * (atm.vConstants.x - vExt)
    * atm.vRcpSumBeta1Beta2;

    // scale inscatter and extinction
    // for effect (optional)
    vIns = vIns * atm.vTermMultipliers.x;
    // scale extinction prior to its use
    // (this is optional)
    vExt = vExt * atm.vTermMultipliers.y * atm.vSoilReflectivity;

    // reduce inscattering on unlit surfaces
    // by modulating with a monochrome
    // Lambertian scalar. This is slightly
    // offset to allow some inscattering to
    // bleed into unlit areas
    float NdL = dot(norm, sunVector);
    vIns = vIns * NdL;

    // apply sunlight color
    // and strength to each term
    // and output
    vIns = vIns * sunColor * sunColor.w * atmScale;
    vIns.w = 0.0f;

    vExt = vExt * sunColor * sunColor.w * atmScale;
    vExt.w = 1.0f;
}