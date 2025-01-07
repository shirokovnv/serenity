package core.scene

import core.di.ServiceLocator
import core.ecs.Entity
import core.math.Matrix4
import core.scene.components.Transform

open class Object(private var parent: Object? = null) : Entity() {

    private val children = mutableListOf<Object>()

    companion object {
        val services = ServiceLocator()
    }

    init {
        addComponent(Transform())
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

    fun getLocalMatrix(): Matrix4 {
        return getComponent<Transform>()!!.matrix()
    }

    fun getWorldMatrix(): Matrix4 {
        return (parent?.getWorldMatrix()?.times(getLocalMatrix())) ?: getLocalMatrix()
    }
}