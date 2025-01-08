package core.ecs

open class BaseComponent : Component {
    private var owner: Entity? = null
    private var isActive: Boolean = true

    override fun owner(): Entity? {
        return owner
    }

    override fun setOwner(owner: Entity?) {
        this.owner = owner
    }

    override fun isActive(): Boolean {
        return isActive
    }

    override fun setActive(active: Boolean) {
        this.isActive = active
    }
}