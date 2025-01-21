package modules.light.flare

import core.math.Vector2
import graphics.assets.texture.Texture2d

class LensFlareTexture(private val texture2d: Texture2d, private val scale: Float) {
    private var screenPosition: Vector2 = Vector2(0f, 0f)

    fun setScreenPosition(screenPosition: Vector2) {
        this.screenPosition = screenPosition
    }

    fun getScreenPosition(): Vector2 = screenPosition

    fun getTexture(): Texture2d = texture2d

    fun getScale(): Float = scale
}