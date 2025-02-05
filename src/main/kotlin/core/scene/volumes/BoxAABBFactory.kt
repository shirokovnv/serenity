package core.scene.volumes

import core.math.Rect3d
import core.math.Vector3
import kotlin.math.max
import kotlin.math.min

object BoxAABBFactory {
    fun fromVertices(vertices: FloatArray, vertexStep: Int = 3, indices: IntArray? = null): BoxAABB {
        if (vertices.isEmpty()) {
            throw IllegalArgumentException("Vertices is empty.")
        }

        val initial = Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE) to Vector3(
            Float.MIN_VALUE,
            Float.MIN_VALUE,
            Float.MIN_VALUE
        )
        val (minValues, maxValues) = if (indices == null || indices.isEmpty()) {
            vertices.asSequence()
                .chunked(vertexStep)
                .fold(initial) { (minValues, maxValues), it ->
                    val x = it[0]
                    val y = it[1]
                    val z = it[2]
                    Vector3(min(minValues.x, x), min(minValues.y, y), min(minValues.z, z)) to Vector3(
                        max(
                            maxValues.x,
                            x
                        ), max(maxValues.y, y), max(maxValues.z, z)
                    )
                }
        } else {
            indices.fold(initial) { (minValues, maxValues), index ->
                val offset = index * vertexStep
                val x = vertices[offset]
                val y = vertices[offset + 1]
                val z = vertices[offset + 2]
                Vector3(min(minValues.x, x), min(minValues.y, y), min(minValues.z, z)) to Vector3(
                    max(maxValues.x, x),
                    max(maxValues.y, y),
                    max(maxValues.z, z)
                )
            }
        }

        return BoxAABB(
            Rect3d(
                Vector3(minValues.x, minValues.y, minValues.z),
                Vector3(maxValues.x, maxValues.y, maxValues.z)
            )
        )
    }
}