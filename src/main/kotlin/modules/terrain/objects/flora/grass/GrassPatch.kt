package modules.terrain.objects.flora.grass

import core.math.Rect3d
import core.math.Vector3
import core.scene.Object
import core.scene.Transform
import core.scene.picking.Picking
import core.scene.volumes.BoxAABB
import modules.terrain.align.alignBoxAABBToHeightmap
import modules.terrain.heightmap.Heightmap

class GrassPatch(
    private val heightmap: Heightmap,
    minPoint: Vector3,
    maxPoint: Vector3
): Object() {

    private var patchBounds = Rect3d(minPoint, maxPoint)

    init {
        addComponent(Picking())
    }

    override fun recalculateBounds() {
        getComponent<BoxAABB>()!!.setShape(patchBounds)
        getComponent<BoxAABB>()!!.transform(getComponent<Transform>()!!)

        alignBoxAABBToHeightmap(getComponent<BoxAABB>()!!, heightmap)
    }
}