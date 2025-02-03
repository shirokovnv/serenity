package graphics.gui

import core.ecs.Component

interface GuiComponent : Component {
    fun onRenderGUI()
}