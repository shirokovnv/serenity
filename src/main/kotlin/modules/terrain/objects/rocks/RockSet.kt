package modules.terrain.objects.rocks

import core.scene.Object

class RockSet : Object() {
    init {
        addComponent(RockSetBehaviour())
    }
}