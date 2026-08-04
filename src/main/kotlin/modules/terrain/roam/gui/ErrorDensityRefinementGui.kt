package modules.terrain.roam.gui

import imgui.ImGui
import modules.terrain.roam.tri.refinement.ErrorDensityParams

class ErrorDensityRefinementGui(override val params: ErrorDensityParams) : RefinementParamsGui() {
    private val density = FloatArray(1) { params.density }

    override fun update() {
        params.density = density[0]
    }

    override fun onRenderGUI() {
        ImGui.sliderFloat(
            "Triangle density", density,
            ErrorDensityParams.MIN_DENSITY,
            ErrorDensityParams.MAX_DENSITY
        )
        ImGui.separator()
    }
}