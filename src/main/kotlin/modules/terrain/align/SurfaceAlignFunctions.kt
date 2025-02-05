package modules.terrain.align

import core.math.Rect3d
import core.math.Vector3
import core.scene.volumes.BoxAABB
import modules.terrain.heightmap.Heightmap

fun alignBoxAABBToHeightmap(bounds: BoxAABB, heightmap: Heightmap) {
    val shape = bounds.shape()
    val yOffset = shape.size().y

    val height = heightmap.getInterpolatedHeight(shape.min.x, shape.min.z) * heightmap.worldScale().y
    val p0 = Vector3(shape.min.x, height, shape.min.z)
    val p1 = Vector3(shape.max.x, height + yOffset, shape.max.z)

    bounds.setShape(Rect3d(p0, p1))
}