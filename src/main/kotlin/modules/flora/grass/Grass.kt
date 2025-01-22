package modules.flora.grass

import core.scene.Object

class Grass: Object() {
    init {
        addComponent(GrassBehaviour())
    }
}