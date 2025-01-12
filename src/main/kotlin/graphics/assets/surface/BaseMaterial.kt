package graphics.assets.surface

import core.ecs.BaseComponent

abstract class BaseMaterial<Self: BaseMaterial<Self, T>, T: BaseShader<T, Self>> : BaseComponent() {
    protected var materialShader: T? = null

    fun setShader(shader: T?) {
        this.materialShader = shader
    }
    fun getShader(): T? {
        return materialShader
    }
}

infix fun <Self : BaseMaterial<Self, T>, T : BaseShader<T, Self>> Self.bind(shader: T): Self {
    this.setShader(shader)
    shader.setMaterial(this)
    return this
}