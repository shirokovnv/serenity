package graphics.assets.buffer

import graphics.assets.Asset

abstract class Ssbo: Asset {
    protected var bindingPoint: Int = 0

    fun bindingPoint(): Int = bindingPoint

    fun setBindingPoint(bindingPoint: Int): Ssbo {
        this.bindingPoint = bindingPoint
        return this
    }
}