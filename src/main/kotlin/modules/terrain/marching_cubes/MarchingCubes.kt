package modules.terrain.marching_cubes

import core.math.Rect3d
import core.math.Vector3
import core.scene.Object

class MarchingCubes : Object() {
    init {
        addComponent(MarchingCubesBehaviour())
    }

    override fun recalculateBounds() {
        val resolution = getComponent<MarchingCubesBehaviour>()!!.getResolution()
        val shape = Rect3d(Vector3(0f), Vector3(resolution.toFloat()))
        bounds().setShape(shape)
        bounds().transform(transform())
    }
}