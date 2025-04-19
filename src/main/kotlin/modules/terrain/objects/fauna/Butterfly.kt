package modules.terrain.objects.fauna

import core.scene.Object

class Butterfly: Object() {
    init {
        addComponent(ButterflyBehaviour())
    }
}