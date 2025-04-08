package modules.water.plane

import core.scene.Object

class WaterPlane(worldHeight: Float = 0.0f) : Object() {
    init {
        addComponent(WaterPlaneRenderer(worldHeight))
    }
}