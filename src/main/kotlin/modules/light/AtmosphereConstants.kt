package modules.light

import core.math.Quaternion

class AtmosphereConstants(
    var vBeta1: Quaternion = Quaternion(),
    var vBeta2: Quaternion = Quaternion(),
    var vBetaD1: Quaternion = Quaternion(),
    var vBetaD2: Quaternion = Quaternion(),
    var vSumBeta1Beta2: Quaternion = Quaternion(),
    var vLog2eBetaSum: Quaternion = Quaternion(),
    var vRcpSumBeta1Beta2: Quaternion = Quaternion(),
    var vHG: Quaternion = Quaternion(),
    var vConstants: Quaternion = Quaternion(),
    var vTermMultipliers: Quaternion = Quaternion(),
    var vSoilReflectivity: Quaternion = Quaternion()
) {
    fun memorySize(): Int = 11 * 4 * 4 // 11 Quaternion, 4 floats each
}