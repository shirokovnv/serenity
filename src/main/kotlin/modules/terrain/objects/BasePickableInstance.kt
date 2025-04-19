package modules.terrain.objects

import core.events.Events
import core.management.Disposable
import core.math.Quaternion
import core.math.Vector3
import core.scene.picking.Picking
import core.scene.picking.PickingTargetEvent
import core.scene.raytracing.RayIntersectable
import core.scene.raytracing.RayIntersectionDetector
import core.scene.volumes.BoxAABBHierarchy

abstract class BasePickableInstance : BaseInstance(), Disposable, RayIntersectable {
    init {
        addComponent(BoxAABBHierarchy())
        addComponent(Picking())
        getComponent<Picking>()!!.pickingKey.instanceId = instanceId

        Events.subscribe<PickingTargetEvent, Any>(::onPickingTarget)
    }

    override fun dispose() {
        Events.unsubscribe<PickingTargetEvent, Any>(::onPickingTarget)
    }

    override fun intersectsWith(origin: Vector3, direction: Vector3): Vector3? {
        val worldMatrix = model.getInstance(instanceId)
        val triangleVertices = mutableListOf<Vector3>()
        model.getModelData().values.forEach { modelData ->
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

    private fun onPickingTarget(event: PickingTargetEvent, sender: Any) {
        if (event.target == this) {
            model.updateInstance(transform().matrix(), instanceId)
        }
    }
}