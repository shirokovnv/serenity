package core.scene.navigation.obstacles

import core.scene.Object
import core.scene.volumes.BoxAABB

interface NavMeshObstacle {
    val objectRef: Object
    fun getObstacleBounds(): BoxAABB
}