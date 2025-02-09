package modules.terrain.heightmap

import org.lwjgl.BufferUtils
import java.nio.FloatBuffer

class PlainGenerator : HeightmapGenerationInterface<PlainParams> {
    override fun generate(width: Int, height: Int, params: PlainParams): FloatBuffer {
        val bufferOfHeights = BufferUtils.createFloatBuffer(width * height * 4)
        for (x in 0..<width) {
            for (y in 0..<height) {
                bufferOfHeights.put(params.yOffset)
                bufferOfHeights.put(params.yOffset)
                bufferOfHeights.put(params.yOffset)
                bufferOfHeights.put(1.0f)
            }
        }
        bufferOfHeights.flip()

        return bufferOfHeights
    }
}