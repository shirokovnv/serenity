package modules.terrain.cdlod

import graphics.gui.GuiBehaviour
import graphics.gui.GuiWindow
import imgui.ImGui
import imgui.type.ImInt

class CdlodTerrainGui(
    private val config: CdlodTerrainConfig
) : GuiBehaviour() {
    private val distMultiplier = FloatArray(1) { CdlodTerrainConfig.DEFAULT_DISTANCE_MULTIPLIER }
    private val resChange = ImInt(CdlodTerrainConfig.RESOLUTIONS.indexOf(config.resolution))
    private val resOptions = CdlodTerrainConfig.RESOLUTIONS.map { it.toString() }.toTypedArray()

    override fun guiWindow(): GuiWindow {
        return GuiWindow.GridWindow("CDLOD Terrain")
    }

    override fun update(deltaTime: Float) {
        if (config.distanceMultiplier != distMultiplier[0]) {
            config.distanceMultiplier = distMultiplier[0]
        }

        if (config.resolution != CdlodTerrainConfig.RESOLUTIONS[resChange.get()]) {
            config.resolution = CdlodTerrainConfig.RESOLUTIONS[resChange.get()]
        }
    }

    override fun onRenderGUI() {
        ImGui.sliderFloat(
            "Distance Multiplier", distMultiplier,
            CdlodTerrainConfig.DEFAULT_MIN_DISTANCE_MULTIPLIER,
            CdlodTerrainConfig.DEFAULT_MAX_DISTANCE_MULTIPLIER
        )
        ImGui.separator()

        ImGui.combo("Resolution", resChange, resOptions)
        ImGui.separator()
    }
}