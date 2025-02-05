package modules.flora.grass

import core.math.Rect3d
import core.math.Vector3
import core.scene.Object
import core.scene.Transform
import core.scene.volumes.BoxAABB

class GrassPatch(minPoint: Vector3, maxPoint: Vector3): Object() {

    private var patchBounds = Rect3d(minPoint, maxPoint)

    override fun recalculateBounds() {
        getComponent<BoxAABB>()!!.setShape(patchBounds)
        getComponent<BoxAABB>()!!.transform(getComponent<Transform>()!!)
    }
}