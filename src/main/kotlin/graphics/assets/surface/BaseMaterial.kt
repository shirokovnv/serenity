package graphics.assets.surface

import core.ecs.BaseComponent

abstract class BaseMaterial<Self: BaseMaterial<Self, P, T>, P: MaterialParams, T: BaseShader<T, Self, P>> : BaseComponent() {
    abstract fun setShader(shader: T?)
    abstract fun getShader(): T?
    abstract fun setParams(params: P)
    abstract fun getParams(): P
}

infix fun <Self : BaseMaterial<Self, P, T>, P: MaterialParams, T : BaseShader<T, Self, P>> Self.bind(shader: T): Self {
    this.setShader(shader)
    shader.setMaterial(this)
    return this
}