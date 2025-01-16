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

layout(std430, binding = 0) buffer AtmosphereConstantsSsbo {
    AtmosphereConstants atm;
};