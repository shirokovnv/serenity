package modules.flora.palm

import core.scene.Object

class Palm: Object() {
    init {
        addComponent(PalmBehaviour())
    }
}