package core.scene

import core.math.Shape

interface Volumetric {
    fun setShape(newShape: Shape)
    fun shape(): Shape
}