package core.scene.navigation.obstacles

import core.math.Rect3d
import core.scene.Object
import core.scene.volumes.BoxAABB

class Obstacle(
    private val obstacleBounds: Rect3d,
): Object(), NavMeshObstacle {
    override val objectRef: Object
        get() = this

    init {
        recalculateBounds()
    }

    override fun recalculateBounds() {
        bounds().setShape(obstacleBounds)
        bounds().transform(transform())
    }

    override fun getObstacleBounds(): BoxAABB {
        return bounds()
    }
}