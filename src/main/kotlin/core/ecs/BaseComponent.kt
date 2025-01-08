package core.ecs

open class BaseComponent : Component {
    private var owner: Entity? = null

    override fun owner(): Entity? {
        return owner
    }

    override fun setOwner(owner: Entity?) {
        this.owner = owner
    }
}