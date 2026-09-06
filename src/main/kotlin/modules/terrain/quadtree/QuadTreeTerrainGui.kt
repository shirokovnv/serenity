package modules.terrain.quadtree

import graphics.gui.GuiBehaviour
import graphics.gui.GuiWindow
import imgui.ImGui
import imgui.ImVec2
import org.lwjgl.opengl.GL43

class QuadTreeTerrainGui(
    private val terrain: QuadTreeTerrainSystem,
    private val terrConfig: QuadTreeTerrainConfig,
    private val lodConfig: QuadTreeLoDConfig,
    private val topViewTextureId: Int
    ): GuiBehaviour() {
    private val distMultiplier = FloatArray(1) { QuadTreeLoDConfig.DEFAULT_DISTANCE_MULTIPLIER }
    private val tessFactor = IntArray(1) { QuadTreeLoDConfig.DEFAULT_TESS_FACTOR }

    override fun guiWindow(): GuiWindow {
        return GuiWindow.GridWindow("QuadTree Terrain")
    }

    override fun update(deltaTime: Float) {
        lodConfig.tessFactor = tessFactor[0]

        if (lodConfig.distanceMultiplier != distMultiplier[0]) {
            lodConfig.distanceMultiplier = distMultiplier[0]
            terrain.calculateLodRanges()
        }
    }

    override fun onRenderGUI() {
        ImGui.sliderFloat("Distance Multiplier", distMultiplier,
            QuadTreeLoDConfig.DEFAULT_MIN_DISTANCE_MULTIPLIER,
            QuadTreeLoDConfig.DEFAULT_MAX_DISTANCE_MULTIPLIER
        )

        ImGui.separator()

        ImGui.sliderInt("Tessellation Factor", tessFactor,
            QuadTreeLoDConfig.DEFAULT_MIN_TESS_FACTOR,
            QuadTreeLoDConfig.DEFAULT_MAX_TESS_FACTOR,
        )

        ImGui.separator()

        ImGui.text("Leaf count: ${terrain.root().countLeaves()}")
        ImGui.text("Cache size: ${terrain.cacheSize()}")

        ImGui.separator()

        GL43.glBindTexture(GL43.GL_TEXTURE_2D, topViewTextureId)

        ImGui.image(topViewTextureId.toLong(), ImVec2(256f, 256f))

        GL43.glBindTexture(GL43.GL_TEXTURE_2D, 0)
    }
}