package core.ecs

abstract class Behaviour : Component() {
    abstract fun create()
    abstract fun update(deltaTime: Float)
    abstract fun destroy()
}