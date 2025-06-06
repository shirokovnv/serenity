package modules.terrain.objects.flora.trees

import core.scene.Object

class TreeSet(private val enablePostProcessing: Boolean): Object() {
    init {
        addComponent(TreeSetBehaviour(enablePostProcessing))
    }
}