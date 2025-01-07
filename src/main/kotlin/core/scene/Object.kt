package core.scene

import core.ecs.Entity
import core.math.Matrix4

open class Object(private var parent: Object? = null) : Entity() {

    private val children = mutableListOf<Object>()

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
        return if (parent != null)
            parent!!.getWorldMatrix() * getLocalMatrix()
        else getLocalMatrix()
    }
}