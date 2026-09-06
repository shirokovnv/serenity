package modules.terrain.roam.gui

import core.scene.camera.Camera
import graphics.assets.buffer.Fbo
import graphics.gui.GuiBehaviour
import graphics.gui.GuiWindow
import imgui.ImGui
import imgui.ImVec2
import imgui.type.ImBoolean
import imgui.type.ImInt
import modules.terrain.roam.RoamTerrainPatchConfig
import modules.terrain.roam.RoamTerrainPatchMetrics
import modules.terrain.roam.tri.refinement.*
import org.lwjgl.opengl.GL43

class RoamTerrainPatchGui(
    private val config: RoamTerrainPatchConfig,
    private val metrics: RoamTerrainPatchMetrics,
    private val camera: Camera,
    private val topViewFbo: Fbo? = null
) : GuiBehaviour() {
    private val perFrameUpdate = IntArray(1) { config.perFrameUpdate }
    private val parallelOps = ImBoolean(config.parallelOps)
    private val lod = IntArray(1) { config.refinement.params().maxLOD }
    private val cullDistThreshold = FloatArray(1) { config.refinement.params().cullDistThreshold }
    private val splits = IntArray(1) { config.maxSplits }
    private val merges = IntArray(1) { config.maxMerges }
    private var refChange = ImInt(config.refinement.type.ordinal)
    private val refLabels = RefinementType.entries.map { it.name }.toTypedArray()

    private fun defaultRefinementParams(type: RefinementType): RefinementParams {
        if (type == config.refinement.type) {
            return config.refinement.params()
        }

        return when (type) {
            RefinementType.DISTANCE -> ErrorDistanceParams()
            RefinementType.DENSITY -> ErrorDensityParams()
        }
    }

    private val refinementGui = mapOf(
        RefinementType.DISTANCE to ErrorDistanceRefinementGui(
            defaultRefinementParams(RefinementType.DISTANCE) as ErrorDistanceParams
        ),
        RefinementType.DENSITY to ErrorDensityRefinementGui(
            defaultRefinementParams(RefinementType.DENSITY) as ErrorDensityParams
        )
    )

    override fun guiWindow(): GuiWindow {
        return GuiWindow.GridWindow("Roam Terrain")
    }

    override fun update(deltaTime: Float) {
        config.perFrameUpdate = perFrameUpdate[0]
        config.parallelOps = parallelOps.get()
        config.refinement.params().maxLOD = lod[0]
        config.refinement.params().cullDistThreshold = cullDistThreshold[0]
        config.maxSplits = splits[0]
        config.maxMerges = merges[0]

        refinementGui[config.refinement.type]!!.update()

        if (config.refinement.type.ordinal != refChange.get()) {
            val type = RefinementType.entries[refChange.get()]
            val params = refinementGui[type]!!.params
            params.maxLOD = config.refinement.params().maxLOD
            params.cullDistThreshold = config.refinement.params().cullDistThreshold

            val refinement =
                RefinementFactory.create(
                    type,
                    params,
                    camera
                )
            config.refinement = refinement
        }
    }

    override fun onRenderGUI() {
        ImGui.sliderInt(
            "Per frame update", perFrameUpdate,
            RoamTerrainPatchConfig.MIN_FRAME_UPDATE,
            RoamTerrainPatchConfig.MAX_FRAME_UPDATE,
        )
        ImGui.separator()

        ImGui.checkbox("Parallel ops", parallelOps)
        ImGui.separator()

        ImGui.sliderInt(
            "Max LOD", lod,
            RefinementParams.MIN_LOD,
            RefinementParams.MAX_LOD,
        )
        ImGui.separator()

        ImGui.sliderFloat(
            "Cull distance threshold", cullDistThreshold,
            RefinementParams.MIN_CULL_DIST_THRESHOLD,
            RefinementParams.MAX_CULL_DIST_THRESHOLD,
        )
        ImGui.separator()

        ImGui.sliderInt(
            "Max splits", splits,
            RoamTerrainPatchConfig.MIN_SPLITS,
            RoamTerrainPatchConfig.MAX_SPLITS,
        )
        ImGui.separator()

        ImGui.sliderInt(
            "Max merges", merges,
            RoamTerrainPatchConfig.MIN_MERGES,
            RoamTerrainPatchConfig.MAX_MERGES,
        )
        ImGui.separator()

        ImGui.combo("Refinement type", refChange, refLabels)

        ImGui.text("Params:")
        refinementGui[config.refinement.type]!!.onRenderGUI()

        ImGui.text("Time to get splitting list: ${metrics.timeToGetSplittingList} ms")
        ImGui.text("Split loop total: ${metrics.splitLoopTotalMs} ms")
        ImGui.text("Split work (only split() calls): ${metrics.splitWorkOnlyCallsMs} ms")
        ImGui.text("Num splits executed: ${metrics.numSplitsExecuted}")
        ImGui.separator()

        ImGui.text("Time to get merging list: ${metrics.timeToGetMergingList} ms")
        ImGui.text("Merge loop total: ${metrics.mergeLoopTotalMs} ms")
        ImGui.text("Merge work (only merge() calls): ${metrics.mergeWorkOnlyCallsMs} ms")
        ImGui.text("Num merges executed: ${metrics.numMergesExecuted}")
        ImGui.separator()

        ImGui.text("Triangulation time: ${metrics.triangulationTimeMs} ms")
        ImGui.text("Mesh rebuild time: ${metrics.meshRebuildTime} ms")
        ImGui.text("Num triangles: ${metrics.numTriangles}")
        ImGui.text("Update time: ${metrics.updateTimeMs} ms")
        ImGui.text("Draw time: ${metrics.drawTimeMs} ms")
        ImGui.separator()

        renderTopView()
    }

    private fun renderTopView() {
        if (topViewFbo != null) {
            val topViewTexture = topViewFbo.getColorTexture()

            GL43.glBindTexture(GL43.GL_TEXTURE_2D, topViewTexture.getId())

            ImGui.image(
                topViewTexture.getId().toLong(),
                ImVec2(topViewTexture.getWidth().toFloat(), topViewTexture.getHeight().toFloat())
            )

            GL43.glBindTexture(GL43.GL_TEXTURE_2D, 0)
        }
    }
}