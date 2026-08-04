package modules.terrain.roam

import core.scene.Object
import graphics.assets.texture.TextureFactory
import modules.terrain.heightmap.Heightmap
import modules.terrain.roam.buffers.PatchBufferType

class RoamTerrainPatchSo(
    private val config: RoamTerrainPatchConfig
) : Object() {
    private var patch: RoamTerrainPatch
    private var heightmap: Heightmap

    init {
        transform().setScale(config.worldScale)
        transform().setTranslation(config.worldOffset)

        heightmap = createHeightmap()
        patch = RoamTerrainPatch(
            config,
            heightmap,
            transform(),
            PatchBufferType.PATCH_VERTEX_BUFFER
        )

        addComponent(patch)
        addComponent(RoamTerrainPatchBehaviour(config))
    }

    private fun createHeightmap() : Heightmap {
        val heightmap =  Heightmap(
            TextureFactory.fromPerlinNoise(
                1024,
                1024,
                0.007f,
                5,
                1.0f,
                0.46f
            ),
            config.worldScale,
            config.worldOffset
        )
        heightmap.texture().bind()
        heightmap.texture().bilinearFilter()
        heightmap.texture().unbind()

        return heightmap
    }
}