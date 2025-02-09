package modules.terrain.heightmap

import core.math.Vector2
import core.math.Vector3
import core.scene.raytracing.getPointOnRay

private const val MAX_BINARY_SEARCH_DEPTH = 200

// TODO: If terrain is too hilly, this method can return improper results depending on view angle
fun binarySearch(
    heightmap: Heightmap,
    rayOrigin: Vector3,
    rayDirection: Vector3,
    range: Vector2,
    depth: Int = 0
): Vector3? {
    val half = range.x + (range.y - range.x) / 2f

    if (depth >= MAX_BINARY_SEARCH_DEPTH) {
        val endPoint = getPointOnRay(rayOrigin, rayDirection, half)

        if (endPoint.x < heightmap.worldOffset().x ||
            endPoint.z < heightmap.worldOffset().z ||
            endPoint.x > heightmap.worldOffset().x + heightmap.worldScale().x ||
            endPoint.z > heightmap.worldOffset().z + heightmap.worldScale().z
        ) {
            return null
        }

        return endPoint
    }

    if (intersectionInRange(heightmap, rayOrigin, rayDirection, Vector2(range.x, half))) {
        return binarySearch(heightmap, rayOrigin, rayDirection, Vector2(range.x, half), depth + 1)
    }

    if (intersectionInRange(heightmap, rayOrigin, rayDirection, Vector2(half, range.y))) {
        return binarySearch(heightmap, rayOrigin, rayDirection, Vector2(half, range.y), depth + 1)
    }

    return null
}

fun intersectionInRange(heightmap: Heightmap, rayOrigin: Vector3, rayDirection: Vector3, range: Vector2): Boolean {
    val startPoint = getPointOnRay(rayOrigin, rayDirection, range.x)
    val endPoint = getPointOnRay(rayOrigin, rayDirection, range.y)

    return !isUnderGround(heightmap, startPoint) && isUnderGround(heightmap, endPoint)
}

fun isUnderGround(heightmap: Heightmap, point: Vector3): Boolean {
    val height = heightmap.getInterpolatedHeight(point.x, point.z) * heightmap.worldScale().y
    return point.y < height
}