package core.ecs

interface Activatable {
    fun isActive(): Boolean
    fun setActive(active: Boolean)
}