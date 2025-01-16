package graphics.assets.buffer

import graphics.assets.Asset

abstract class BufferSsbo: Asset {
    protected var bindingPoint: Int = 0

    fun bindingPoint(): Int = bindingPoint

    fun setBindingPoint(bindingPoint: Int): BufferSsbo {
        this.bindingPoint = bindingPoint
        return this
    }
}