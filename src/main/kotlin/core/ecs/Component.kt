package core.ecs

interface Component : Activatable {
    fun owner(): Entity?
    fun setOwner(owner: Entity?)
}