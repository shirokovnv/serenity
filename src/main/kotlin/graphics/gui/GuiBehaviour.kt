package graphics.gui

import core.ecs.Behaviour
import core.management.Resources

abstract class GuiBehaviour: Behaviour(), GuiComponent {
    protected open fun guiWindow(): GuiWindow {
        return GuiWindow.GridWindow("Default GUI")
    }

    override fun create() {
        val guiWindow = guiWindow()

        guiWindow.components.add(this)
        Resources.get<GuiWrapper>()!!.addOrUpdateWindow(guiWindow)
    }

    override fun destroy() {
        Resources.get<GuiWrapper>()!!.removeWindow(guiWindow())
    }
}