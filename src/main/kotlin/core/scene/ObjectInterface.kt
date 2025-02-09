package core.scene

import core.scene.volumes.BoxAABB

interface ObjectInterface {
    fun bounds(): BoxAABB
    fun transform(): Transform
}