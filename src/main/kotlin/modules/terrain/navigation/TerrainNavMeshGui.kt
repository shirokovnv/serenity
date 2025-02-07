package modules.terrain.navigation

import core.events.Events
import core.scene.navigation.path.PathResult
import graphics.gui.GuiBehaviour
import graphics.gui.GuiWindow
import imgui.ImGui

class TerrainNavMeshGui(private val agent: TerrainNavMeshAgent) : GuiBehaviour() {

    companion object {
        private const val MIN_SLOPE = 0.1f
        private const val MAX_SLOPE = 1.0f
        private const val MIN_RADIUS = 1.0f
        private const val MAX_RADIUS = 50.0f
        private const val MIN_STEP_SIZE = 1f
        private const val MAX_STEP_SIZE = 25f
    }

    private val radiusRange = FloatArray(1) { agent.radius }
    private val slopeRange = FloatArray(1) { agent.maxSlope }
    private val stepRange = FloatArray(1) { agent.stepSize }

    private var pathResult: PathResult? = null

    override fun guiWindow(): GuiWindow {
        return GuiWindow.GridWindow("Navigation")
    }

    override fun create() {
        super.create()

        Events.subscribe<CalcTerrainPathEvent, Any>(::onCalcTerrainPath)
    }

    override fun destroy() {
        super.destroy()

        Events.unsubscribe<CalcTerrainPathEvent, Any>(::onCalcTerrainPath)
    }

    override fun update(deltaTime: Float) {
        agent.radius = radiusRange[0]
        agent.maxSlope = slopeRange[0]
        agent.stepSize = stepRange[0]
    }

    override fun onRenderGUI() {
        ImGui.sliderFloat("radius", radiusRange,
            MIN_RADIUS,
            MAX_RADIUS
        )

        ImGui.sliderFloat("slope", slopeRange,
            MIN_SLOPE,
            MAX_SLOPE
        )

        ImGui.sliderFloat("stepSize", stepRange,
            MIN_STEP_SIZE,
            MAX_STEP_SIZE
        )

        if (pathResult != null) {
            ImGui.separator()
            ImGui.text("Current path: (${pathResult?.path?.size}) ${pathResult?.status}")
            ImGui.text("${pathResult?.path?.first()}")
            ImGui.text("${pathResult?.path?.last()}")
        }
    }

    private fun onCalcTerrainPath(event: CalcTerrainPathEvent, sender: Any) {
        pathResult = event.pathResult
    }
}