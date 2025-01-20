package graphics.model

import core.math.Vector3

class Vertex(val index: Int, val position: Vector3) {

    companion object {
        const val NO_INDEX = -1
    }

    var textureIndex: Int = NO_INDEX
    var normalIndex: Int = NO_INDEX
    var duplicateVertex: Vertex? = null
    val length: Float
        get() = position.length()

    fun isSet(): Boolean = textureIndex != NO_INDEX && normalIndex != NO_INDEX

    fun hasSameTextureAndNormal(textureIndexOther: Int, normalIndexOther: Int): Boolean =
        textureIndexOther == textureIndex && normalIndexOther == normalIndex
}