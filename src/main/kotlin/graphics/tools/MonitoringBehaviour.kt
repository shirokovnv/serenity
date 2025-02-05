package graphics.tools

import core.management.Resources
import core.scene.Object
import core.scene.TraversalOrder
import core.scene.picking.PickingContainer
import core.scene.traverse
import graphics.gui.GuiBehaviour
import graphics.gui.GuiWindow
import imgui.ImGui
import platform.services.FrameCounter

class MonitoringBehaviour: GuiBehaviour() {
    override fun guiWindow(): GuiWindow = GuiWindow.GridWindow("System Monitor")

    private val fps: Int
        get() = Resources.get<FrameCounter>()!!.fps()

    private val countSceneObjects: Int
        get() {
            val root = (owner() as Object).getRoot()
            var counter = 0
            traverse(root, { counter++ }, TraversalOrder.BREADTH_FIRST)

            return counter
        }

    private val countPickingObjects: Int
        get() = PickingContainer.count()

    override fun update(deltaTime: Float) {
    }

    override fun onRenderGUI() {
        ImGui.text("FPS: $fps")
        ImGui.text("Num objects in scene graph: $countSceneObjects")
        ImGui.text("Num picking objects: $countPickingObjects")
    }
}