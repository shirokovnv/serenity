package core.scene.components

import core.math.Shape

interface Volumetric {
    fun setShape(newShape: Shape)
    fun shape(): Shape
}