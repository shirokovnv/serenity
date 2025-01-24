package modules.sky

import core.scene.Object

class SkyDome(params: SkyDomeParams, enablePostProcessing: Boolean): Object() {
    init {
        addComponent(SkyDomeBehaviour(params, enablePostProcessing))
    }
}