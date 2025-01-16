package core.math.noise

class GaussNoise : NoiseInterface {
    fun gaussRandom(): Float {
        val random = java.util.Random()
        return random.nextGaussian().toFloat()
    }
}