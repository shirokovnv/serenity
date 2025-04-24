package core.scene.voxelization

class VoxelGrid(val resolution: Int) {

    private var data: FloatArray

    init {
        require(resolution > 0) { "Resolution must be positive integer." }
        data = FloatArray(resolution * resolution * resolution)
    }

    fun read(x: Int, y: Int, z: Int): Float {
        checkOutOfBounds(x, y, z)
        return data[x + resolution * (y + resolution * z)]
    }

    fun write(x: Int, y: Int, z: Int, value: Float) {
        checkOutOfBounds(x, y, z)
        data[x + resolution * (y + resolution * z)] = value
    }

    private fun checkOutOfBounds(x: Int, y: Int, z: Int) {
        require(x in 0..<resolution && y in 0..<resolution && z in 0..<resolution) {
            "Coordinates (x=$x, y=$y, z=$z) are out of bounds (resolution=$resolution)"
        }
    }
}