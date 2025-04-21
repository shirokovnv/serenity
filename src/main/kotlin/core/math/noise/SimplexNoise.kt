package core.math.noise

// https://github.com/SRombauts/SimplexNoise
class SimplexNoise(
    private val frequency: Float = 1.0f,
    private val amplitude: Float = 1.0f,
    private val lacunarity: Float = 2.0f,
    private val persistence: Float = 0.5f
) : NoiseInterface {

    companion object {
        // Skewing/Unskewing factors for 2D
        private const val F2 = 0.3660254f  // F2 = (sqrt(3) - 1) / 2
        private const val G2 = 0.21132487f  // G2 = (3 - sqrt(3)) / 6   = F2 / (1 + 2 * K)

        private val perm: IntArray = intArrayOf(
            151, 160, 137, 91, 90, 15,
            131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
            190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
            88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
            77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
            102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
            135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
            5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
            223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
            129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
            251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
            49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
            138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
        )

        private fun hash(i: Int): Int {
            return perm[i.toUByte().toInt()]
        }

        private fun grad(hash: Int, x: Float): Float {
            val h = hash and 0x0F  // Convert low 4 bits of hash code
            var grad = 1.0f + (h and 7)    // Gradient value 1.0, 2.0, ..., 8.0
            if ((h and 8) != 0) grad = -grad // Set a random sign for the gradient
            //  float grad = gradients1D[h];    // NOTE : Test of Gradient look-up table instead of the above
            return (grad * x)              // Multiply the gradient with the distance
        }

        private fun grad(hash: Int, x: Float, y: Float): Float {
            val h = hash and 0x3F  // Convert low 3 bits of hash code
            val u = if (h < 4) x else y  // into 8 simple gradient directions,
            val v = if (h < 4) y else x
            return (if ((h and 1) != 0) -u else u) + (if ((h and 2) != 0) -2.0f * v else 2.0f * v) // and compute the dot product with (x,y).
        }

        private fun grad(hash: Int, x: Float, y: Float, z: Float): Float {
            val h = hash and 15     // Convert low 4 bits of hash code into 12 simple
            val u = if (h < 8) x else y // gradient directions, and compute dot product.
            val v = if (h < 4) y else if (h == 12 || h == 14) x else z // Fix repeats at h = 12 to 15
            return (if ((h and 1) != 0) -u else u) + (if ((h and 2) != 0) -v else v)
        }

        private fun fastFloor(x: Float): Int {
            return if (x > 0) x.toInt() else x.toInt() - 1
        }
    }

    // 1D Simplex noise
    fun noise(x: Float): Float {
        val n0: Float
        val n1: Float   // Noise contributions from the two "corners"

        // No need to skew the input space in 1D

        // Corners coordinates (nearest integer values):
        val i0 = fastFloor(x)
        val i1 = i0 + 1
        // Distances to corners (between 0 and 1):
        val x0 = x - i0
        val x1 = x0 - 1.0f

        // Calculate the contribution from the first corner
        var t0 = 1.0f - x0 * x0
        t0 *= t0
        n0 = t0 * t0 * grad(hash(i0), x0)

        // Calculate the contribution from the second corner
        var t1 = 1.0f - x1 * x1
        t1 *= t1
        n1 = t1 * t1 * grad(hash(i1), x1)

        // The maximum value of this noise is 8*(3/4)^4 = 2.53125
        // A factor of 0.395 scales to fit exactly within [-1,1]
        return 0.395f * (n0 + n1)
    }

    // 2D Simplex noise
    fun noise(x: Float, y: Float): Float {
        val n0: Float
        val n1: Float
        val n2: Float   // Noise contributions from the three corners

        // Skew the input space to determine which simplex cell we're in
        val s = (x + y) * F2  // Hairy factor for 2D
        val xs = x + s
        val ys = y + s
        val i = fastFloor(xs)
        val j = fastFloor(ys)

        // Unskew the cell origin back to (x,y) space
        val t = (i + j).toFloat() * G2
        val xI = i - t
        val yI = j - t
        val x0 = x - xI  // The x,y distances from the cell origin
        val y0 = y - yI

        // For the 2D case, the simplex shape is an equilateral triangle.
        // Determine which simplex we are in.
        val i1: Int
        val j1: Int  // Offsets for second (middle) corner of simplex in (i,j) coords
        if (x0 > y0) {   // lower triangle, XY order: (0,0)->(1,0)->(1,1)
            i1 = 1
            j1 = 0
        } else {   // upper triangle, YX order: (0,0)->(0,1)->(1,1)
            i1 = 0
            j1 = 1
        }

        // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
        // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
        // c = (3-sqrt(3))/6

        val x1 = x0 - i1 + G2            // Offsets for middle corner in (x,y) unskewed coords
        val y1 = y0 - j1 + G2
        val x2 = x0 - 1.0f + 2.0f * G2   // Offsets for last corner in (x,y) unskewed coords
        val y2 = y0 - 1.0f + 2.0f * G2

        // Work out the hashed gradient indices of the three simplex corners
        val gi0 = hash(i + hash(j))
        val gi1 = hash(i + i1 + hash(j + j1))
        val gi2 = hash(i + 1 + hash(j + 1))

        // Calculate the contribution from the first corner
        var t0 = 0.5f - x0 * x0 - y0 * y0
        n0 = if (t0 < 0.0f) {
            0.0f
        } else {
            t0 *= t0
            t0 * t0 * grad(gi0, x0, y0)
        }

        // Calculate the contribution from the second corner
        var t1 = 0.5f - x1 * x1 - y1 * y1
        n1 = if (t1 < 0.0f) {
            0.0f
        } else {
            t1 *= t1
            t1 * t1 * grad(gi1, x1, y1)
        }

        // Calculate the contribution from the third corner
        var t2 = 0.5f - x2 * x2 - y2 * y2
        n2 = if (t2 < 0.0f) {
            0.0f
        } else {
            t2 *= t2
            t2 * t2 * grad(gi2, x2, y2)
        }

        // Add contributions from each corner to get the final noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 45.23065f * (n0 + n1 + n2)
    }

    // 3D Simplex noise
    fun noise(x: Float, y: Float, z: Float): Float {
        val n0: Float
        val n1: Float
        val n2: Float
        val n3: Float   // Noise contributions from the four corners

        val f3 = 1.0f / 3.0f
        val g3 = 1.0f / 6.0f

        val s = (x + y + z) * f3
        val i = fastFloor(x + s)
        val j = fastFloor(y + s)
        val k = fastFloor(z + s)

        val t = (i + j + k) * g3
        val xI = i - t
        val yI = j - t
        val zI = k - t

        val x0 = x - xI
        val y0 = y - yI
        val z0 = z - zI

        val i1: Int
        val j1: Int
        val k1: Int
        val i2: Int
        val j2: Int
        val k2: Int

        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1; j1 = 0; k1 = 0; i2 = 1; j2 = 1; k2 = 0
            } else if (x0 >= z0) {
                i1 = 1; j1 = 0; k1 = 0; i2 = 1; j2 = 0; k2 = 1
            } else {
                i1 = 0; j1 = 0; k1 = 1; i2 = 1; j2 = 0; k2 = 1
            }
        } else {
            if (y0 < z0) {
                i1 = 0; j1 = 0; k1 = 1; i2 = 0; j2 = 1; k2 = 1
            } else if (x0 < z0) {
                i1 = 0; j1 = 1; k1 = 0; i2 = 0; j2 = 1; k2 = 1
            } else {
                i1 = 0; j1 = 1; k1 = 0; i2 = 1; j2 = 1; k2 = 0
            }
        }

        val x1 = x0 - i1 + g3
        val y1 = y0 - j1 + g3
        val z1 = z0 - k1 + g3

        val x2 = x0 - i2 + 2.0f * g3
        val y2 = y0 - j2 + 2.0f * g3
        val z2 = z0 - k2 + 2.0f * g3

        val x3 = x0 - 1.0f + 3.0f * g3
        val y3 = y0 - 1.0f + 3.0f * g3
        val z3 = z0 - 1.0f + 3.0f * g3

        val gi0 = hash(i + hash(j + hash(k)))
        val gi1 = hash(i + i1 + hash(j + j1 + hash(k + k1)))
        val gi2 = hash(i + i2 + hash(j + j2 + hash(k + k2)))
        val gi3 = hash(i + 1 + hash(j + 1 + hash(k + 1)))

        var t0 = 0.6f - x0 * x0 - y0 * y0 - z0 * z0
        n0 = if (t0 < 0) {
            0.0f
        } else {
            t0 *= t0
            t0 * t0 * grad(gi0, x0, y0, z0)
        }

        var t1 = 0.6f - x1 * x1 - y1 * y1 - z1 * z1
        n1 = if (t1 < 0) {
            0.0f
        } else {
            t1 *= t1
            t1 * t1 * grad(gi1, x1, y1, z1)
        }

        var t2 = 0.6f - x2 * x2 - y2 * y2 - z2 * z2
        n2 = if (t2 < 0) {
            0.0f
        } else {
            t2 *= t2
            t2 * t2 * grad(gi2, x2, y2, z2)
        }

        var t3 = 0.6f - x3 * x3 - y3 * y3 - z3 * z3
        n3 = if (t3 < 0) {
            0.0f
        } else {
            t3 *= t3
            t3 * t3 * grad(gi3, x3, y3, z3)
        }

        return 32.0f * (n0 + n1 + n2 + n3)
    }

    // Fractal/Fractional Brownian Motion (fBm) noise summation
    fun fractal(octaves: Int, x: Float): Float {
        var output = 0.0f
        var denom = 0.0f
        var currentFrequency = frequency
        var currentAmplitude = amplitude

        for (i in 0..<octaves) {
            output += (currentAmplitude * noise(x * currentFrequency))
            denom += currentAmplitude

            currentFrequency *= lacunarity
            currentAmplitude *= persistence
        }

        return (output / denom)
    }

    fun fractal(octaves: Int, x: Float, y: Float): Float {
        var output = 0.0f
        var denom = 0.0f
        var currentFrequency = frequency
        var currentAmplitude = amplitude

        for (i in 0..<octaves) {
            output += (currentAmplitude * noise(x * currentFrequency, y * currentFrequency))
            denom += currentAmplitude

            currentFrequency *= lacunarity
            currentAmplitude *= persistence
        }

        return (output / denom)
    }

    fun fractal(octaves: Int, x: Float, y: Float, z: Float): Float {
        var output = 0.0f
        var denom = 0.0f
        var currentFrequency = frequency
        var currentAmplitude = amplitude

        for (i in 0..<octaves) {
            output += (currentAmplitude * noise(x * currentFrequency, y * currentFrequency, z * currentFrequency))
            denom += currentAmplitude

            currentFrequency *= lacunarity
            currentAmplitude *= persistence
        }

        return (output / denom)
    }
}