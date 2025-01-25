package modules.ocean

import core.scene.Object

class Ocean(params: OceanParams, stretchToHorizon: Boolean): Object() {

    init {
        addComponent(OceanRenderer(params, stretchToHorizon))
    }
}