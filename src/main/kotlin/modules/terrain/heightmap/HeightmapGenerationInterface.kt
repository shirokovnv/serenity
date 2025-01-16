package modules.terrain.heightmap

import java.nio.FloatBuffer

interface HeightmapGenerationInterface<TParams: HeightmapGenerationParams> {
    fun generate(width: Int, height: Int, params: TParams): FloatBuffer
}