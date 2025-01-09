package core.scene

import core.math.Shape

interface Volumetric<TShape: Shape> {
    fun setShape(newShape: TShape)
    fun shape(): TShape
}