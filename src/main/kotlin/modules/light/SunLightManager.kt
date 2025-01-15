package modules.light

import core.ecs.BaseComponent
import core.math.Vector3
import core.math.extensions.clamp
import kotlin.math.*

typealias ComputeSunColorFn = (timeOfDay: Float) -> Vector3
typealias ComputeSunIntensityFn = (sunVector: Vector3) -> Float

class SunLightManager(
    private val computeSunColorFn: ComputeSunColorFn = ::computeSunColorUsingPBM,
    private val computeSunIntensityFn: ComputeSunIntensityFn = ::computeSunIntensityUsingFadeOut
): BaseComponent() {

    companion object {
        private const val TIME_CYCLE = 2 * PI.toFloat()
        const val TIME_NOON = TIME_CYCLE * 0.0f
        const val TIME_DAWN = TIME_CYCLE * 0.3f
        const val TIME_DUSK = TIME_CYCLE * 0.7f
        const val TIME_MIDNIGHT = 0.5f
    }

    private var timeOfDay: Float = 0.5f
    private var timeStep: Float = 0.001f
    private var sunIntensity: Float = 0.8f
    private var sunColor: Vector3 = Vector3(1.0f, 0.8f, 0.8f)
    private var sunVector: Vector3 = Vector3(0.0f, 1.0f, 0.0f)
    private var dawnSunColor: Vector3 = Vector3()
    private var noonSunColor: Vector3 = Vector3()
    private var deltaSunColor: Vector3 = Vector3()

    init {
        dawnSunColor = computeSunColorFn(TIME_DAWN)
        noonSunColor = computeSunColorFn(TIME_NOON)
        deltaSunColor = noonSunColor - dawnSunColor

        setTimeOfDay(TIME_NOON)
    }

    fun sunColor(): Vector3 {
        return sunColor
    }

    fun sunVector(): Vector3 {
        return sunVector
    }

    fun sunIntensity(): Float {
        return sunIntensity
    }

    fun timeOfDay(): Float = timeOfDay

    fun timeStep(): Float = timeStep

    fun setTimeOfDay(timeOfDay: Float) {
        this.timeOfDay = timeOfDay % TIME_CYCLE
    }

    fun setTimeStep(timeStep: Float) {
        this.timeStep = timeStep
    }

    fun update() {
        recalculateSunlightParameters()
    }

    private fun recalculateSunlightParameters() {
        sunVector.x = 0.0f
        sunVector.y = cos(timeOfDay)
        sunVector.z = sin(timeOfDay)

        val zenithFactor = sunVector.y.clamp(0.0f, 1.0f)
        sunColor = dawnSunColor + (deltaSunColor * zenithFactor)
        sunIntensity = computeSunIntensityFn(sunVector)
    }
}

fun computeSunIntensityFromAltitude(sunVector: Vector3): Float {
    return 0.5f + 0.5f * sunVector.y
}

fun computeSunIntensityUsingFadeOut(sunVector: Vector3): Float {
    var fadeOut = 1.0f
    if (sunVector.y < 0.0f) {
        fadeOut = -sunVector.y * 5.0f
        fadeOut = (1.0f - fadeOut).clamp(0.0f, 1.0f)
    }

    return fadeOut
}

fun computeSunColorUsingSinCos(timeOfDay: Float): Vector3 {
    // Final color depends on the angle of the sun
    val red = 1.0f  // The red color is more intense at sunrise and sunset
    val green = min(1.0f, (0.5f + 0.5f*cos(timeOfDay)))
    val blue = min(1.0f, (0.5f + 0.5f*sin(timeOfDay)))

    return Vector3(red, green, blue)
}

fun computeSunColorUsingPBM(timeOfDay: Float): Vector3 {
    val turbidity = 2.0f
    // compute theta as the angle from
    // the noon (zenith) position in
    // radians
    var theta = if (timeOfDay < PI) timeOfDay else 2 * PI - timeOfDay
    // angles greater than the horizon are clamped
    theta = theta.toFloat().clamp(0.0f, PI.toFloat() * 0.5f)

    // beta is a measure of aerosols.
    val beta = 0.04608366f * turbidity - 0.04586026f
    val opticalMass = -1.0f / (cos(theta) + 0.15f * (93.885f - theta / PI * 180.0f).pow(-1.253))
    // constants for lambda
    // provided by Preetham et al.
    val lambda = Vector3(0.65f, 0.57f, 0.475f)

    val output = Vector3()

    // compute each color channel
    // tauR - Rayleigh Scattering
    // tuaA - Aerosal (water + dust) attenuation
    // particle size ratio set at (1.3)
    val tauRX = exp(opticalMass * 0.008735f * lambda.x.pow(-4.08f))
    val tauAX = exp(opticalMass * beta * lambda.x.pow(-1.3f))

    val tauRY = exp(opticalMass * 0.008735f * lambda.y.pow(-4.08f))
    val tauAY = exp(opticalMass * beta * lambda.y.pow(-1.3f))

    val tauRZ = exp(opticalMass * 0.008735f * lambda.z.pow(-4.08f))
    val tauAZ = exp(opticalMass * beta * lambda.z.pow(-1.3f))

    output.x = (tauRX * tauAX).toFloat()
    output.y = (tauRY * tauAY).toFloat()
    output.z = (tauRZ * tauAZ).toFloat()

    return output.normalize()
}