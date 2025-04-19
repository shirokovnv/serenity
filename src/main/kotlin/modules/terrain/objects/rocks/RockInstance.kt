package modules.terrain.objects.rocks

import core.management.Disposable
import core.scene.Object
import core.scene.Transform
import core.scene.navigation.obstacles.NavMeshObstacle
import core.scene.volumes.BoxAABB
import core.scene.volumes.BoxAABBHierarchy
import graphics.model.Model
import modules.terrain.objects.BaseInstance

class RockInstance(
    rockModel: Model,
    instanceId: Int
) : BaseInstance(rockModel, instanceId), NavMeshObstacle, Disposable {
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

    override fun dispose() {
    }
}