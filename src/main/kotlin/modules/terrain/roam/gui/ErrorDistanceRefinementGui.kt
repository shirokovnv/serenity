package modules.terrain.roam.gui

import imgui.ImGui
import modules.terrain.roam.tri.refinement.ErrorDistanceParams

class ErrorDistanceRefinementGui(override val params: ErrorDistanceParams) : RefinementParamsGui() {
    private val errorScale = FloatArray(1) { params.errorScale }
    private val errorLimit = FloatArray(1) { params.errorLimit }
    private val splitThreshold = FloatArray(1) { params.splitThreshold }
    private val mergeThreshold = FloatArray(1) { params.mergeThreshold }

    override fun update() {
        params.errorScale = errorScale[0]
        params.errorLimit = errorLimit[0]
        params.splitThreshold = splitThreshold[0]
        params.mergeThreshold = mergeThreshold[0]
    }

    override fun onRenderGUI() {
        ImGui.sliderFloat(
            "Error scale", errorScale,
            ErrorDistanceParams.MIN_ERROR_SCALE,
            ErrorDistanceParams.MAX_ERROR_SCALE
        )
        ImGui.separator()

        ImGui.sliderFloat(
            "Error limit", errorLimit,
            ErrorDistanceParams.MIN_ERROR_LIMIT,
            ErrorDistanceParams.MAX_ERROR_LIMIT,
            "%.4f"
        )
        ImGui.separator()

        ImGui.sliderFloat(
            "Split threshold", splitThreshold,
            ErrorDistanceParams.MIN_SPLIT_THRESHOLD,
            ErrorDistanceParams.MAX_SPLIT_THRESHOLD
        )
        ImGui.separator()

        ImGui.sliderFloat(
            "Merge threshold", mergeThreshold,
            ErrorDistanceParams.MIN_MERGE_THRESHOLD,
            ErrorDistanceParams.MAX_MERGE_THRESHOLD
        )
        ImGui.separator()
    }
}