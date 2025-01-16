package modules.flora.palm

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial

class PalmMaterial: BaseMaterial<PalmMaterial, PalmShader>() {
    lateinit var worldViewProjection: Matrix4
}