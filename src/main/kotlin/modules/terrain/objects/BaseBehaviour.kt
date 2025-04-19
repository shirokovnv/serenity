package modules.terrain.objects

import core.ecs.Behaviour
import core.math.IntersectionDetector
import core.math.Quaternion
import core.math.Vector3
import core.scene.Object
import core.scene.camera.Frustum
import core.scene.volumes.BoxAABB
import graphics.rendering.gizmos.BoxAABBDrawer
import graphics.rendering.gizmos.DrawGizmosEvent
import graphics.rendering.gizmos.MeshDrawer

abstract class BaseBehaviour : Behaviour() {
    protected val meshVertices = mutableListOf<Vector3>()
    protected abstract var frustum: Frustum

    protected fun onDrawGizmos(event: DrawGizmosEvent, sender: Any) {
        frustum.recalculateSearchVolume()
        meshVertices.clear()

        (owner() as Object)
            .getChildren()
            .filterIsInstance<BaseInstance>()
            .filter { instance ->
                val boxAABB = instance.getComponent<BoxAABB>()

                if (boxAABB != null) {
                    IntersectionDetector.intersects(
                        frustum.searchVolume().shape(),
                        boxAABB.shape()
                    )
                } else {
                    false
                }
            }
            .forEach { treeInstance ->
                treeInstance.getComponent<BoxAABBDrawer>()?.draw()
                collectMeshVertices(treeInstance)
            }

        owner()?.getComponent<MeshDrawer>()?.draw()
    }

    protected fun collectMeshVertices(instance: BaseInstance) {
        val model = instance.model
        val worldMatrix = model.getInstance(instance.instanceId)

        model.getModelData().values.forEach { modelData ->
            for (i in modelData.indices) {
                val offset = i * 3

                val originalVertex = Vector3(
                    modelData.vertices[offset],
                    modelData.vertices[offset + 1],
                    modelData.vertices[offset + 2]
                )
                val worldVertex = (worldMatrix * Quaternion(originalVertex, 1.0f)).xyz()
                meshVertices.add(worldVertex)
            }
        }
    }
}