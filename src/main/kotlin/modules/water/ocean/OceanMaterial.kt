package modules.water.ocean

import core.math.Matrix4
import core.math.Vector2
import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d
import kotlin.properties.Delegates

class OceanMaterial : BaseMaterial<OceanMaterial, OceanShader>() {
    companion object {
        const val MIN_CHOPPINESS = 0.1f
        const val MAX_CHOPPINESS = 2.0f
        const val MIN_AMPLITUDE = 0.1f
        const val MAX_AMPLITUDE = 50.0f
        const val MIN_WIND_MAGNITUDE = 0.5f
        const val MAX_WIND_MAGNITUDE = 50.0f
    }

    lateinit var model: Matrix4
    lateinit var view: Matrix4
    lateinit var projection: Matrix4
    lateinit var displacementMap: Texture2d
    lateinit var normalMap: Texture2d
    lateinit var wind: Vector2
    lateinit var offsetPosition: Vector2

    var fftResolution by Delegates.notNull<Int>()
    var oceanSize by Delegates.notNull<Int>()
    var amplitude by Delegates.notNull<Float>()
    var choppiness by Delegates.notNull<Float>()
    var time by Delegates.notNull<Float>()
    var count by Delegates.notNull<Int>()
    var stride by Delegates.notNull<Int>()
}