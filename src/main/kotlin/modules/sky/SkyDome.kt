package modules.sky

import core.scene.Object

class SkyDome: Object() {
    init {
        addComponent(SkyDomeBehaviour())
    }
}