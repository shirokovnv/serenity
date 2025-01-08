package core.scene

import core.di.ServiceLocator
import core.ecs.Entity
import core.math.Matrix4
import core.math.Rect3d
import core.math.Vector3
import core.scene.components.BoundingVolume
import core.scene.components.Transform

open class Object(private var parent: Object? = null) : Entity() {

    private val children = mutableListOf<Object>()

    companion object {
        val services = ServiceLocator()
    }

    init {
        addComponent(Transform())
        addComponent(
            BoundingVolume(
                Rect3d(
                    Vector3(0f, 0f, 0f),
                    Vector3(0f, 0f, 0f)
                )
            )
        )
    }

    fun parent(): Object? = parent

    fun setParent(parent: Object?) {
        this.parent = parent
    }

    fun addChild(child: Object) {
        children.add(child)
    }

    fun clearChildren() {
        children.clear()
    }

    fun localMatrix(): Matrix4 {
        return getComponent<Transform>()!!.matrix()
    }

    fun worldMatrix(): Matrix4 {
        return (parent?.worldMatrix()?.times(localMatrix())) ?: localMatrix()
    }
}