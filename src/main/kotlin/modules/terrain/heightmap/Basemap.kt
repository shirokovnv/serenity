package modules.terrain.heightmap

import core.management.Disposable
import core.math.Vector2
import core.math.Vector3
import graphics.assets.texture.Texture2d
import java.nio.FloatBuffer

abstract class Basemap(
    protected var mapTexture: Texture2d,
    protected val worldScale: Vector3,
    protected val worldOffset: Vector3
) : Disposable {
    protected var mapData: FloatBuffer

    init {
        mapData = createMapDataBuffer()
    }

    protected val width
        get() = mapTexture.getWidth()

    protected val height
        get() = mapTexture.getHeight()

    protected fun worldToTexture(worldX: Float, worldY: Float): Vector2 {
        val scaledX = (worldX - worldOffset.x) / worldScale.x
        val scaledY = (worldY - worldOffset.z) / worldScale.z

        val textureX = (scaledX * width).coerceIn(0f, (width - 1).toFloat())
        val textureY = (scaledY * height).coerceIn(0f, (height - 1).toFloat())

        return Vector2(textureX, textureY)
    }

    protected fun textureToWorld(textureX: Float, textureY: Float): Vector2 {
        val normalizedX = textureX / width
        val normalizedZ = textureY / height

        val scaledX = normalizedX * worldScale.x
        val scaledZ = normalizedZ * worldScale.z

        val worldX = scaledX + worldOffset.x
        val worldZ = scaledZ + worldOffset.z

        return Vector2(worldX, worldZ)
    }

    fun texture(): Texture2d = mapTexture

    fun worldScale(): Vector3 = worldScale

    fun worldOffset(): Vector3 = worldOffset

    override fun dispose() {
        mapTexture.destroy()
    }

    abstract fun createMapDataBuffer(): FloatBuffer
}