package modules.flora.trees

import core.events.Events
import core.management.Disposable
import core.scene.Object
import core.scene.Transform
import core.scene.navigation.obstacles.NavMeshObstacle
import core.scene.picking.PickingTargetEvent
import core.scene.picking.Picking
import core.scene.volumes.BoxAABB
import core.scene.volumes.BoxAABBHierarchy
import graphics.model.Model

class TreeInstance(
    private val treeModel: Model,
    private val instanceId: Int
) : Object(), Disposable, NavMeshObstacle {

    override val objectRef: Object
        get() = this

    init {
        addComponent(BoxAABBHierarchy())
        addComponent(Picking())
        getComponent<Picking>()!!.pickingKey.instanceId = instanceId

        Events.subscribe<PickingTargetEvent, Any>(::onPickingTarget)
    }

    override fun recalculateBounds() {
        getComponent<BoxAABB>()!!.setShape(
            getComponent<BoxAABBHierarchy>()!!.outerBounds().shape()
        )
        getComponent<BoxAABB>()!!.transform(getComponent<Transform>()!!)
    }

    private fun onPickingTarget(event: PickingTargetEvent, sender: Any) {
        if (event.target == this) {
            treeModel.updateInstance(transform().matrix(), instanceId)
        }
    }

    override fun dispose() {
        Events.unsubscribe<PickingTargetEvent, Any>(::onPickingTarget)
    }

    override fun getObstacleBounds(): BoxAABB {
        return getComponent<BoxAABBHierarchy>()?.minInnerBounds() ?: bounds()
    }
}