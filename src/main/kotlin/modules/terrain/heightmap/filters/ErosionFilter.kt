package modules.terrain.heightmap.filters

import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

class ErosionFilter(
    private val gravity: Int = 4,
    private val maxDropletLifeTime: Int = 30,
    private val numIterations: Int = 100000,
    private val sedimentCapacityFactor: Float = 4.0f,
    private val minSedimentCapacity: Float = 0.01f,
    private val erodeSpeed: Float = 0.3f,
    private val depositSpeed: Float = 0.3f,
    private val evaporateSpeed: Float = 0.01f,
    private val inertia: Float = 0.05f
) : HeightmapFilterInterface {
    private lateinit var erosionBrushIndices: Array<IntArray>
    private lateinit var erosionBrushWeights: Array<FloatArray>

    private val radius: Int = 3
    private val initialSpeed = 1.0f
    private val initialWaterVolume = 1.0f

    private data class HeightAndGradient(
        val height: Float,
        val gradientX: Float,
        val gradientY: Float
    )

    override fun filter(map: FloatArray, size: Int): FloatArray {
        require(map.size == size * size)
        initializeBrushIndices(size, radius)

        erode(map, size, numIterations)

        for (i in 0..<size) {
            for (j in 0..<size) {
                require(!map[i * size + j].isNaN())
            }
        }

        return map
    }

    private fun erode(
        map: FloatArray,
        mapSize: Int,
        numIterations: Int = 1
    ) {
        for (iteration in 0..<numIterations) {
            // Create water droplet at random point on map
            var posX = Random.nextFloat() * (mapSize - 1)
            var posY = Random.nextFloat() * (mapSize - 1)
            var dirX = 0f
            var dirY = 0f
            var speed = initialSpeed
            var water = initialWaterVolume
            var sediment = 0f

            for (lifetime in 0..<maxDropletLifeTime) {
                val nodeX = posX.toInt()
                val nodeY = posY.toInt()
                val dropletIndex = nodeY * mapSize + nodeX

                // Calculate droplet's offset inside the cell (0,0) = at NW node, (1,1) = at SE node
                val cellOffsetX = posX - nodeX
                val cellOffsetY = posY - nodeY

                // Calculate droplet's height and direction of flow with bilinear interpolation of surrounding heights
                val heightAndGradient = calculateHeightAndGradient(map, mapSize, posX, posY)

                // Update the droplet's direction and position (move position 1 unit regardless of speed)
                dirX = dirX * inertia - heightAndGradient.gradientX * (1f - inertia)
                dirY = dirY * inertia - heightAndGradient.gradientY * (1f - inertia)

                // Normalize direction
                val len = sqrt(dirX * dirX + dirY * dirY)
                if (len != 0f) {
                    dirX /= len
                    dirY /= len
                }
                posX += dirX
                posY += dirY

                // Stop simulating droplet if it's not moving or has flowed over edge of map
                if ((dirX == 0f && dirY == 0f) ||
                    posX < 0f || posX >= mapSize - 1 ||
                    posY < 0f || posY >= mapSize - 1
                ) {
                    break
                }

                // Find the droplet's new height and calculate the deltaHeight
                val newHeight = calculateHeightAndGradient(map, mapSize, posX, posY).height
                val deltaHeight = newHeight - heightAndGradient.height

                // Calculate the droplet's sediment capacity (higher when moving fast down a slope and contains lots of water)
                val sedimentCapacity = kotlin.math.max(
                    -deltaHeight * speed * water * sedimentCapacityFactor,
                    minSedimentCapacity
                )

                // If carrying more sediment than capacity, or if flowing uphill:
                if (sediment > sedimentCapacity || deltaHeight > 0f) {
                    // If moving uphill (deltaHeight > 0) try fill up to the current height, otherwise deposit a fraction of the excess sediment
                    var amountToDeposit = if (deltaHeight > 0f) {
                        min(deltaHeight, sediment)
                    } else {
                        (sediment - sedimentCapacity) * depositSpeed
                    }

                    if (amountToDeposit.isNaN()) {
                        amountToDeposit = 0.0f
                    }

                    sediment -= amountToDeposit

                    // Add the sediment to the four nodes of the current cell using bilinear interpolation
                    // Deposition is not distributed over a radius (like erosion) so that it can fill small pits
                    map[dropletIndex] += amountToDeposit * (1f - cellOffsetX) * (1f - cellOffsetY)
                    map[dropletIndex + 1] += amountToDeposit * cellOffsetX * (1f - cellOffsetY)
                    map[dropletIndex + mapSize] += amountToDeposit * (1f - cellOffsetX) * cellOffsetY
                    map[dropletIndex + mapSize + 1] += amountToDeposit * cellOffsetX * cellOffsetY

                } else {
                    // Erode a fraction of the droplet's current carry capacity.
                    // Clamp the erosion to the change in height so that it doesn't dig a hole in the terrain behind the droplet
                    val amountToErode = kotlin.math.min(
                        (sedimentCapacity - sediment) * erodeSpeed,
                        -deltaHeight
                    )

                    // Use erosion brush to erode from all nodes inside the droplet's erosion radius
                    val brushIndices = erosionBrushIndices[dropletIndex]
                    val brushWeights = erosionBrushWeights[dropletIndex]

                    for (i in brushIndices.indices) {
                        val nodeIndex = brushIndices[i]
                        val weightedErodeAmount = amountToErode * brushWeights[i]

                        val deltaSediment = if (map[nodeIndex] < weightedErodeAmount) {
                            map[nodeIndex]
                        } else {
                            weightedErodeAmount
                        }
                        if (!deltaSediment.isNaN()) {
                            map[nodeIndex] -= deltaSediment
                        }

                        sediment += deltaSediment
                    }
                }

                // Update droplet's speed and water content
                speed = sqrt(speed * speed + deltaHeight * gravity)
                water *= (1f - evaporateSpeed)
            }

        }
    }

    private fun calculateHeightAndGradient(
        nodes: FloatArray,
        mapSize: Int,
        posX: Float,
        posY: Float
    ): HeightAndGradient {
        val coordX = posX.toInt()
        val coordY = posY.toInt()

        // Calculate droplet's offset inside the cell (0,0) = at NW node, (1,1) = at SE node
        val x = posX - coordX
        val y = posY - coordY

        // Calculate heights of the four nodes of the droplet's cell
        val nodeIndexNW = coordY * mapSize + coordX
        val heightNW = nodes[nodeIndexNW]
        val heightNE = nodes[nodeIndexNW + 1]
        val heightSW = nodes[nodeIndexNW + mapSize]
        val heightSE = nodes[nodeIndexNW + mapSize + 1]

        // Calculate droplet's direction of flow with bilinear interpolation of height difference along the edges
        val gradientX = (heightNE - heightNW) * (1f - y) + (heightSE - heightSW) * y
        val gradientY = (heightSW - heightNW) * (1f - x) + (heightSE - heightNE) * x

        // Calculate height with bilinear interpolation of the heights of the nodes of the cell
        val height = heightNW * (1f - x) * (1f - y) +
                heightNE * x * (1f - y) +
                heightSW * (1f - x) * y +
                heightSE * x * y

        return HeightAndGradient(height, gradientX, gradientY)
    }

    private fun initializeBrushIndices(mapSize: Int, radius: Int) {
        val totalCells = mapSize * mapSize
        erosionBrushIndices = Array(totalCells) { IntArray(0) }
        erosionBrushWeights = Array(totalCells) { FloatArray(0) }

        val maxEntries = (radius * 2 + 1) * (radius * 2 + 1)
        val xOffsets = IntArray(maxEntries)
        val yOffsets = IntArray(maxEntries)
        val weights = FloatArray(maxEntries)

        for (i in 0..<totalCells) {
            val centreX = i % mapSize
            val centreY = i / mapSize

            var weightSum = 0f
            var addIndex = 0

            val nearEdge = (centreY <= radius || centreY >= mapSize - radius - 1 ||
                    centreX <= radius || centreX >= mapSize - radius - 1)

            if (nearEdge) {
                weightSum = 0f
                addIndex = 0
                val radiusSq = radius * radius

                for (dy in -radius..radius) {
                    for (dx in -radius..radius) {
                        val sqrDst = dx * dx + dy * dy
                        if (sqrDst < radiusSq) {
                            val coordX = centreX + dx
                            val coordY = centreY + dy

                            if (coordX in 0..<mapSize && coordY in 0..<mapSize) {
                                val weight = 1f - sqrt(sqrDst.toFloat()) / radius
                                weightSum += weight
                                weights[addIndex] = weight
                                xOffsets[addIndex] = dx
                                yOffsets[addIndex] = dy
                                addIndex++
                            }
                        }
                    }
                }
            }

            val numEntries = addIndex
            if (numEntries == 0) {
                erosionBrushIndices[i] = IntArray(0)
                erosionBrushWeights[i] = FloatArray(0)
            } else {
                val indices = IntArray(numEntries)
                val normalizedWeights = FloatArray(numEntries)

                for (j in 0..<numEntries) {
                    val coordX = xOffsets[j] + centreX
                    val coordY = yOffsets[j] + centreY
                    indices[j] = coordY * mapSize + coordX
                    normalizedWeights[j] = if (weightSum > 0f) weights[j] / weightSum else 0f
                }

                erosionBrushIndices[i] = indices
                erosionBrushWeights[i] = normalizedWeights
            }
        }
    }
}