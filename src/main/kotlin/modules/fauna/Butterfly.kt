package modules.fauna

import core.scene.Object

class Butterfly: Object() {
    init {
        addComponent(ButterflyBehaviour())
    }
}