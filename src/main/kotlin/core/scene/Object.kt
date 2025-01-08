package core.scene

import core.di.ServiceLocator
import core.ecs.Activatable
import core.ecs.Entity
import core.math.Matrix4
import core.math.Rect3d
import core.math.Vector3

open class Object(private var parent: Object? = null) : Entity(), Activatable {

    private val children = mutableListOf<Object>()
    private var isActive: Boolean = true

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

    fun getChildren(): MutableList<Object> {
        return children.toMutableList()
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

    override fun isActive(): Boolean {
        return isActive
    }

    override fun setActive(active: Boolean) {
        this.isActive = active
    }
}