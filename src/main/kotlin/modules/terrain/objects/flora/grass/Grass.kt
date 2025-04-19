package modules.terrain.objects.flora.grass

import core.scene.Object

class Grass: Object() {
    init {
        addComponent(GrassBehaviour())
    }
}