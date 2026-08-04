package modules.terrain.roam.tri

import core.math.Vector2

typealias TriLocalVerticesProvider = () -> Array<Vector2>

fun canonicalTriBaseVerticesProvider(): Array<Vector2> {
    return arrayOf(
        Vector2(0.0f, 0.0f),
        Vector2(0.0f, 1.0f),
        Vector2(1.0f, 0.0f)
    )
}

fun canonicalTriBaseMirrorVerticesProvider(): Array<Vector2> {
    return arrayOf(
        Vector2(1.0f, 1.0f),
        Vector2(1.0f, 0.0f),
        Vector2(0.0f, 1.0f)
    )
}

fun fromParentVerticesProvider(parentLocalVertices: Array<Vector2>, isLeftChild: Boolean): Array<Vector2> {
    val localMedian = (parentLocalVertices[1] + parentLocalVertices[2]) * 0.5f
    val restVertices =
        if (isLeftChild)
            arrayOf(parentLocalVertices[2], parentLocalVertices[0])
        else
            arrayOf(parentLocalVertices[0], parentLocalVertices[1])

    return arrayOf(
        localMedian,
        restVertices[0],
        restVertices[1]
    )
}