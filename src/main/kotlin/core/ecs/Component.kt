package core.ecs

abstract class Component {
    private var owner: Entity? = null
    fun owner(): Entity? = owner
    fun setOwner(owner: Entity?) {
        this.owner = owner
    }
}