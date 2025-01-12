package graphics.assets.surface

import core.ecs.BaseComponent

abstract class BaseMaterial<Self: BaseMaterial<Self, T>, T: BaseShader<T, Self>> : BaseComponent() {
    abstract fun setShader(shader: T?)
    abstract fun getShader(): T?
}

infix fun <Self : BaseMaterial<Self, T>, T : BaseShader<T, Self>> Self.bind(shader: T): Self {
    this.setShader(shader)
    shader.setMaterial(this)
    return this
}