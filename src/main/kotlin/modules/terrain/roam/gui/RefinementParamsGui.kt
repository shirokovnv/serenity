package modules.terrain.roam.gui

import core.ecs.BaseComponent
import graphics.gui.GuiComponent
import modules.terrain.roam.tri.refinement.RefinementParams

abstract class RefinementParamsGui : BaseComponent(), GuiComponent {
    abstract val params: RefinementParams

    abstract fun update()
}