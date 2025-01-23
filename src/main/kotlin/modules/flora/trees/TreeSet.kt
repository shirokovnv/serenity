package modules.flora.trees

import core.scene.Object

class TreeSet: Object() {
    init {
        addComponent(TreeSetBehaviour())
    }
}