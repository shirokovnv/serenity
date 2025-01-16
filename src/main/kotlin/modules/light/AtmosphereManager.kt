package modules.light

import core.math.Quaternion
import kotlin.math.ln

/*
    The AtmosphereManager class contains atmospheric
    lighting calculations for outdoor light
    scattering. This class is based on the work
    of Nathaniel Hoffman, Kenneth J. Mitchell
    and Arcot J. Preetham. Details on their work
    can be found at

    http://www.ati.com/developer/
    dx9/ATI-LightScattering.pdf

    This class is responsible for building a
    set of shader parameters which
    can be used to perform light scattering for outdoor
    scenes.
*/

class AtmosphereManager {
    companion object {
        private val vRayleighBeta = Quaternion(0.000697153f, 0.00117891f, 0.00244460f, 0.0f)
        private val vRayleighAngularBeta = Quaternion(4.16082e-005f, 7.03612e-005f, 0.000145901f, 0.0f)
        private val vMieBeta = Quaternion(0.00574060f, 0.00739969f, 0.0105143f, 0.0f)
        private val vMieAngularBeta = Quaternion(0.00133379f, 0.00173466f, 0.00249762f, 0.0f)
        private val vSoilReflectivity = Quaternion(0.138f, 0.113f, 0.08f, 0.0f)
    }

    private var henyeyG: Float = 0.98f
    private var rayleighBetaMultiplier: Float = 0.06f
    private var mieBetaMultiplier: Float = 0.001f
    private var inscatteringMultiplier: Float = 0.27f
    private var extinctionMultiplier: Float = 1.33f
    private var reflectivePower: Float = 0.1f
    private var shaderParams: AtmosphereConstants = AtmosphereConstants()

    private var isChanged: Boolean = true

    init {
        recalculateShaderParams()
    }

    fun setHenyeyG(g: Float) {
        henyeyG = g
        isChanged = true
    }

    fun setRayleighScale(s: Float) {
        rayleighBetaMultiplier = s
        isChanged = true
    }

    fun setMieScale(s: Float) {
        mieBetaMultiplier = s
        isChanged = true
    }

    fun setInscatteringScale(s: Float) {
        inscatteringMultiplier = s
        isChanged = true
    }

    fun setExtinctionScale(s: Float) {
        extinctionMultiplier = s
        isChanged = true
    }

    fun setTerrainReflectionScale(s: Float) {
        reflectivePower = s
        isChanged = true
    }

    fun getHenyeyG(): Float = henyeyG

    fun getRayleighScale(): Float = rayleighBetaMultiplier

    fun getMieScale(): Float = mieBetaMultiplier

    fun getInscatteringScale(): Float = inscatteringMultiplier

    fun getExtinctionScale(): Float = extinctionMultiplier

    fun getTerrainReflectionScale(): Float = reflectivePower

    fun getShaderParams(): AtmosphereConstants {
        if (isChanged) {
            recalculateShaderParams()
        }
        return shaderParams
    }

    fun isChanged(): Boolean = isChanged

    private fun recalculateShaderParams() {
        val invLog2 = 1.0f / ln(2.0).toFloat()

        shaderParams.vBeta1 = vRayleighBeta * rayleighBetaMultiplier
        shaderParams.vBeta2 = vMieBeta * mieBetaMultiplier
        shaderParams.vBetaD1 = vRayleighAngularBeta * rayleighBetaMultiplier
        shaderParams.vBetaD2 = vMieAngularBeta * mieBetaMultiplier
        shaderParams.vSumBeta1Beta2 = shaderParams.vBeta1 + shaderParams.vBeta2

        shaderParams.vLog2eBetaSum = shaderParams.vSumBeta1Beta2 * invLog2
        shaderParams.vRcpSumBeta1Beta2 = Quaternion(
            1.0f / shaderParams.vSumBeta1Beta2.x,
            1.0f / shaderParams.vSumBeta1Beta2.y,
            1.0f / shaderParams.vSumBeta1Beta2.z,
            0.0f
        )
        shaderParams.vHG = Quaternion(
            1.0f - henyeyG * henyeyG,
            1.0f + henyeyG,
            2.0f * henyeyG,
            1.0f
        )
        shaderParams.vConstants = Quaternion(1.0f, invLog2, 0.5f, 0.0f)
        shaderParams.vTermMultipliers = Quaternion(
            inscatteringMultiplier,
            extinctionMultiplier,
            2.0f,
            0.0f
        )
        shaderParams.vSoilReflectivity = vSoilReflectivity * reflectivePower

        isChanged = false
    }
}