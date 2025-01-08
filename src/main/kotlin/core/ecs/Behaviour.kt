package core.ecs

abstract class Behaviour : BaseComponent() {
    abstract fun create()
    abstract fun update(deltaTime: Float)
    abstract fun destroy()
}