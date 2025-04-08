package modules.water.plane

import core.scene.Object

class WaterPlane(params: WaterPlaneParams) : Object() {
    init {
        addComponent(WaterPlaneRenderer(params))
    }
}