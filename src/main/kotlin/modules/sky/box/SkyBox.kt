package modules.sky.box

import core.scene.Object

class SkyBox : Object() {
    init {
        addComponent(SkyBoxBehaviour())
    }
}