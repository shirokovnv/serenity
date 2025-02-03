package modules.terrain.tiled

import graphics.gui.GuiBehaviour
import graphics.gui.GuiWindow
import imgui.ImGui

class TiledTerrainGui(private val material: TiledTerrainMaterial): GuiBehaviour() {
    private val minLodRange = FloatArray(1) { TiledTerrainMaterial.DEFAULT_MIN_LOD }
    private val maxLodRange = FloatArray(1) { TiledTerrainMaterial.DEFAULT_MAX_LOD }
    private val minDistanceRange = FloatArray(1) { TiledTerrainMaterial.DEFAULT_MIN_DISTANCE }
    private val maxDistanceRange = FloatArray(1) { TiledTerrainMaterial.DEFAULT_MAX_DISTANCE }
    private val tbnRange = FloatArray(1) { TiledTerrainMaterial.DEFAULT_TBN_RANGE }

    override fun guiWindow(): GuiWindow {
        return GuiWindow.GridWindow("Terrain")
    }

    override fun update(deltaTime: Float) {
        material.minLOD = minLodRange[0]
        material.maxLOD = maxLodRange[0]
        material.minDistance = minDistanceRange[0]
        material.maxDistance = maxDistanceRange[0]
    }

    override fun onRenderGUI() {
        ImGui.sliderFloat("Min LOD", minLodRange,
            TiledTerrainMaterial.DEFAULT_MIN_LOD,
            (TiledTerrainMaterial.DEFAULT_MAX_LOD + TiledTerrainMaterial.DEFAULT_MIN_LOD) / 2
        )

        ImGui.sliderFloat("Max LOD", maxLodRange,
            (TiledTerrainMaterial.DEFAULT_MAX_LOD + TiledTerrainMaterial.DEFAULT_MIN_LOD) / 2,
            TiledTerrainMaterial.DEFAULT_MAX_LOD
        )

        ImGui.separator()

        ImGui.sliderFloat("Min distance", minDistanceRange,
            TiledTerrainMaterial.DEFAULT_MIN_DISTANCE,
            (TiledTerrainMaterial.DEFAULT_MAX_DISTANCE + TiledTerrainMaterial.DEFAULT_MIN_DISTANCE) / 2
        )

        ImGui.sliderFloat("Max distance", maxDistanceRange,
            (TiledTerrainMaterial.DEFAULT_MAX_DISTANCE + TiledTerrainMaterial.DEFAULT_MIN_DISTANCE) / 2,
            TiledTerrainMaterial.DEFAULT_MAX_DISTANCE
        )

        ImGui.separator()

        ImGui.sliderFloat("TBN Range", tbnRange,
            TiledTerrainMaterial.DEFAULT_TBN_RANGE / 2,
            TiledTerrainMaterial.DEFAULT_TBN_RANGE * 2
        )
    }
}