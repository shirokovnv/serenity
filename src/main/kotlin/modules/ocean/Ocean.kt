package modules.ocean

import core.scene.Object

class Ocean: Object() {

    init {

        val oceanParams = OceanParams(
            512,
            256,
            10.0f,
            45.0f,
            10.0f,
        0.5f
        )

        addComponent(OceanRenderer(oceanParams))
    }
}