package core.ecs

interface Component {
    fun owner(): Entity?
    fun setOwner(owner: Entity?)
}