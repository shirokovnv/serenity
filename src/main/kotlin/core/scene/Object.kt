package core.scene

import core.ecs.Activatable
import core.ecs.Entity
import core.math.Matrix4
import core.math.Rect3d
import core.math.Vector3
import core.scene.ObjectFlag.Companion.Active
import core.scene.ObjectFlag.Companion.None
import core.scene.ObjectFlag.Companion.or
import core.scene.volumes.BoxAABB

open class Object(private var parent: Object? = null) : Entity(), Activatable {

    private val children = mutableListOf<Object>()
    private var flags: ObjectFlag = None

    init {
        addComponent(Transform())
        addComponent(
            BoxAABB(
                Rect3d(
                    Vector3(0f, 0f, 0f),
                    Vector3(0f, 0f, 0f)
                )
            )
        )
        setFlags(Active)
    }

    fun parent(): Object? = parent

    fun setParent(parent: Object?) {
        this.parent = parent
    }

    fun addChild(child: Object) {
        child.setParent(this)
        children.add(child)
    }

    fun removeChild(child: Object) {
        if (children.remove(child)) {
            child.setParent(null)
        }
    }

    fun getChildren(): MutableList<Object> {
        return children.toMutableList()
    }

    fun clearChildren() {
        children.forEach { child -> child.setParent(null) }
        children.clear()
    }

    fun localMatrix(): Matrix4 {
        return getComponent<Transform>()!!.matrix()
    }

    fun worldMatrix(): Matrix4 {
        return (parent?.worldMatrix()?.times(localMatrix())) ?: localMatrix()
    }

    override fun isActive(): Boolean {
        return hasAnyFlags(Active)
    }

    override fun setActive(active: Boolean) {
        setFlags(Active)
    }

    fun setFlags(targetFlag: ObjectFlag) {
        this.flags = flags or targetFlag
    }

    fun getFlags(): ObjectFlag {
        return flags
    }

    fun clearFlags() {
        this.flags = None
    }

    fun hasAnyFlags(targetFlag: ObjectFlag): Boolean {
        return this.flags.value and targetFlag.value != None.value
    }

    fun hasExactFlags(targetFlag: ObjectFlag): Boolean {
        return this.flags.value and targetFlag.value == targetFlag.value
    }

    fun getRoot(): Object {
        return parent?.getRoot() ?: this
    }

    fun transform(): Transform {
        return getComponent<Transform>()!!
    }

    open fun recalculateBounds() {
    }
}