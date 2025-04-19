package modules.terrain.objects.flora.trees

import core.scene.Object
import core.scene.Transform
import core.scene.navigation.obstacles.NavMeshObstacle
import core.scene.volumes.BoxAABB
import core.scene.volumes.BoxAABBHierarchy
import graphics.model.Model
import modules.terrain.objects.BasePickableInstance

class TreeInstance(
    override val model: Model,
    override val instanceId: Int
) : BasePickableInstance(), NavMeshObstacle {

    override val objectRef: Object
        get() = this

    override fun recalculateBounds() {
        getComponent<BoxAABB>()!!.setShape(
            getComponent<BoxAABBHierarchy>()!!.outerBounds().shape()
        )
        getComponent<BoxAABB>()!!.transform(getComponent<Transform>()!!)
    }

    override fun getObstacleBounds(): BoxAABB {
        return getComponent<BoxAABBHierarchy>()?.minInnerBounds() ?: bounds()
    }
}