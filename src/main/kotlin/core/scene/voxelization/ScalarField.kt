package core.scene.voxelization

typealias DensityProvider = (x: Float, y: Float, z: Float) -> Float

class ScalarField(
    private val resolution: Int,
    private val densityProvider: DensityProvider
) {

    fun generate(): VoxelGrid {
        val voxelGrid = VoxelGrid(resolution)

        for (x in 0..<voxelGrid.resolution) {
            for (y in 0..<voxelGrid.resolution) {
                for (z in 0..<voxelGrid.resolution) {
                    val density = densityProvider(
                        x.toFloat() / resolution.toFloat(),
                        y.toFloat() / resolution.toFloat(),
                        z.toFloat() / resolution.toFloat()
                    )

                    voxelGrid.write(x, y, z, density)
                }
            }
        }

        return voxelGrid
    }
}