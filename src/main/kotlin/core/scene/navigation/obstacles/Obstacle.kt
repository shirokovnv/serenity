package core.scene.navigation.obstacles

import core.math.Rect2d
import core.math.Rect3d
import core.math.Vector3
import core.scene.Object
import core.scene.volumes.BoxAABB

class Obstacle(
    private val obstacleBoundsInWorldSpace: Rect2d
): Object(), NavMeshObstacle {
    override val objectRef: Object
        get() = this

    init {
        recalculateBounds()
    }

    override fun recalculateBounds() {
        bounds().setShape(
            Rect3d(
                Vector3(obstacleBoundsInWorldSpace.min.x, 0f, obstacleBoundsInWorldSpace.min.y),
                Vector3(obstacleBoundsInWorldSpace.max.x, 0f, obstacleBoundsInWorldSpace.max.y)
            )
        )
        bounds().transform(transform())
    }

    override fun getObstacleBounds(): BoxAABB {
        return bounds()
    }
}