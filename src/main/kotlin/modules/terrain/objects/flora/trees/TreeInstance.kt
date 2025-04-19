package modules.terrain.objects.flora.trees

import core.events.Events
import core.management.Disposable
import core.math.Quaternion
import core.math.Vector3
import core.scene.Object
import core.scene.Transform
import core.scene.navigation.obstacles.NavMeshObstacle
import core.scene.picking.Picking
import core.scene.picking.PickingTargetEvent
import core.scene.raytracing.RayIntersectable
import core.scene.raytracing.RayIntersectionDetector
import core.scene.volumes.BoxAABB
import core.scene.volumes.BoxAABBHierarchy
import graphics.model.Model

class TreeInstance(
    private val treeModel: Model,
    private val instanceId: Int
) : Object(), Disposable, NavMeshObstacle, RayIntersectable {

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

    override fun intersectsWith(origin: Vector3, direction: Vector3): Vector3? {
        val worldMatrix = treeModel.getInstance(instanceId)
        val triangleVertices = mutableListOf<Vector3>()
        treeModel.getModelData().values.forEach { modelData ->
            for (i in modelData.indices) {
                val offset = i * 3
                val originalVertex = Vector3(
                    modelData.vertices[offset],
                    modelData.vertices[offset + 1],
                    modelData.vertices[offset + 2]
                )
                val worldVertex = (worldMatrix * Quaternion(originalVertex, 1.0f)).xyz()

                triangleVertices.add(worldVertex)
            }
        }

        val intersection = RayIntersectionDetector.rayIntersects(
                origin,
                direction,
                triangleVertices
            )

        if (intersection != null) {
            return (intersection.first + intersection.second + intersection.third) / 3.0f
        }

        return null
    }

    fun getTreeModel(): Model = treeModel
    fun getInstanceId(): Int = instanceId
}