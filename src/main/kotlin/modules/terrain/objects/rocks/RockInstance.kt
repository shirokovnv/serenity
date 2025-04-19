package modules.terrain.objects.rocks

import core.scene.Object
import core.scene.Transform
import core.scene.navigation.obstacles.NavMeshObstacle
import core.scene.volumes.BoxAABB
import core.scene.volumes.BoxAABBHierarchy
import graphics.model.Model
import modules.terrain.objects.BasePickableInstance

class RockInstance(
    override val model: Model,
    override val instanceId: Int
) : BasePickableInstance(), NavMeshObstacle {
    override val objectRef: Object
        get() = this

    init {
        addComponent(BoxAABBHierarchy())
    }

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