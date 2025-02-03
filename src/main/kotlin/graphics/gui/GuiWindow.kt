package graphics.gui

import imgui.ImVec2

sealed class GuiWindow {
    abstract val name: String
    abstract val components: MutableList<GuiComponent>

    data class GridWindow(
        override val name: String,
        override val components: MutableList<GuiComponent> = mutableListOf()
    ): GuiWindow()

    data class FreeWindow(
        override val name: String,
        val position: ImVec2,
        val size: ImVec2,
        override val components: MutableList<GuiComponent> = mutableListOf()
    ): GuiWindow()
}