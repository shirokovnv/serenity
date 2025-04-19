package modules.terrain.objects

import core.scene.Object
import graphics.model.Model

abstract class BaseInstance : Object() {
    abstract val model: Model
    abstract val instanceId: Int
}