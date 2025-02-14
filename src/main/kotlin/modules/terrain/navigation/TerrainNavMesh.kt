package modules.terrain.navigation

import core.math.Rect2d
import core.math.Rect3d
import core.math.Vector2
import core.math.Vector3
import core.scene.navigation.NavGrid
import core.scene.navigation.NavMesh
import core.scene.navigation.obstacles.NavMeshObstacle
import core.scene.navigation.obstacles.Obstacle
import core.scene.volumes.BoxAABB
import graphics.geometry.Mesh3d
import graphics.rendering.Color
import graphics.rendering.Colors
import modules.terrain.heightmap.Heightmap

class TerrainNavMesh(
    private val heightmap: Heightmap,
    private val gridSize: Float,
    private val maxSlope: Float,
    obstaclesCollection: List<NavMeshObstacle>,
    private val walkableAreaColor: Color = Colors.LightBlue,
    private val unwalkableAreaColor: Color = Colors.LightRed,
    private val opacity: Float = 0.25f
) : NavMesh() {

    companion object {
        private val upVector = Vector3(0f, 1f, 0f)
    }

    private val width = heightmap.worldScale().x
    private val height = heightmap.worldScale().z

    private val widthQuads = (width / gridSize).toInt()
    private val heightQuads = (height / gridSize).toInt()

    private val offsetXZ = Vector2(heightmap.worldOffset().x, heightmap.worldOffset().z)
    private val scaleXZ = Vector2(heightmap.worldScale().x, heightmap.worldScale().z)

    private val terrainBounds = Rect2d(Vector2(offsetXZ), offsetXZ + scaleXZ)
    private val dimensions = Vector2(widthQuads.toFloat(), heightQuads.toFloat())
    private var navGrid: NavGrid = NavGrid(terrainBounds, dimensions)

    private lateinit var mesh: Mesh3d

    init {
        obstacles.addAll(obstaclesCollection)
    }

    fun walkableAreaColor(): Color = walkableAreaColor
    fun unwalkableAreaColor(): Color = unwalkableAreaColor
    fun opacity(): Float = opacity
    fun grid(): NavGrid = navGrid

    fun getHeightmap(): Heightmap = heightmap
    fun getMesh(): Mesh3d = mesh

    override fun bake() {
        obstacles.forEach {
            navGrid.insert(it.objectRef)
        }

        agents.forEach {
            navGrid.insert(it.objectRef)
        }

        val numVertices = (widthQuads + 1) * (heightQuads + 1)
        val numIndices = widthQuads * heightQuads * 6

        val vertices = Array(numVertices) { Vector3(0f) }
        val colors = Array(numVertices) { Vector3(0f) }
        val indices = IntArray(numIndices)

        var vIndex = 0
        for (z in 0..heightQuads) {
            for (x in 0..widthQuads) {
                val worldX = x * gridSize + heightmap.worldOffset().x
                val worldZ = z * gridSize + heightmap.worldOffset().z
                val worldY = heightmap.getInterpolatedHeight(
                    worldX,
                    worldZ
                ) * heightmap.worldScale().y + heightmap.worldOffset().y

                vertices[vIndex++] = Vector3(worldX, worldY, worldZ)
            }
        }

        var iIndex = 0
        for (z in 0..<heightQuads) {
            for (x in 0..<widthQuads) {
                val topLeft = z * (widthQuads + 1) + x
                val topRight = topLeft + 1
                val bottomLeft = topLeft + widthQuads + 1
                val bottomRight = bottomLeft + 1

                // Triangle 1
                indices[iIndex++] = topLeft
                indices[iIndex++] = bottomLeft
                indices[iIndex++] = topRight

                // Triangle 2
                indices[iIndex++] = topRight
                indices[iIndex++] = bottomLeft
                indices[iIndex++] = bottomRight
            }
        }

        var cIndex = 0
        for (z in 0..heightQuads) {
            for (x in 0..widthQuads) {
                val wx0 = (x - 1) * gridSize + heightmap.worldOffset().x
                val wz0 = (z - 1) * gridSize + heightmap.worldOffset().z
                val wx = x * gridSize + heightmap.worldOffset().x
                val wz = z * gridSize + heightmap.worldOffset().z
                val wx1 = (x + 1) * gridSize + heightmap.worldOffset().x
                val wz1 = (z + 1) * gridSize + heightmap.worldOffset().z

                val color = constrainMeshByCellProperties(x, z, wx0, wz0, wx, wz, wx1, wz1)

                colors[cIndex++] = color.toVector3()
            }
        }

        mesh = Mesh3d()

        mesh.setVertices(vertices.toList())
        mesh.setIndices(indices.toList())
        mesh.setColors(colors.toList())
    }

    private fun ensureCellIsWalkable(cell: Rect2d, maxSlope: Float): Boolean {
        val cellMin = cell.min
        val cellMax = cell.max
        val center = cell.center

        val p0 = heightmap.getInterpolatedNormal(cellMin.x, cellMin.y)
        val p1 = heightmap.getInterpolatedNormal(cellMin.x, cellMax.y)
        val p2 = heightmap.getInterpolatedNormal(cellMax.x, cellMin.y)
        val p3 = heightmap.getInterpolatedNormal(cellMax.x, cellMax.y)
        val p4 = heightmap.getInterpolatedNormal(center.x, center.y)

        val dp0 = p0.dot(upVector)
        val dp1 = p1.dot(upVector)
        val dp2 = p2.dot(upVector)
        val dp3 = p3.dot(upVector)
        val dp4 = p4.dot(upVector)

        return !(dp0 < maxSlope || dp1 < maxSlope || dp2 < maxSlope || dp3 < maxSlope || dp4 < maxSlope)
    }

    private fun constrainMeshByCellProperties(
        x: Int,
        z: Int,
        wx0: Float,
        wz0: Float,
        wx: Float,
        wz: Float,
        wx1: Float,
        wz1: Float
    ): Color {
        val cellMin = Vector2(wx0, wz0)
        val cellMax = Vector2(wx1, wz1)
        val cellBounds = Rect2d(cellMin, cellMax)

        val normal = heightmap.getInterpolatedNormal(wx, wz)
        var color = if (normal.dot(upVector) < maxSlope) unwalkableAreaColor else walkableAreaColor

        val h0 = heightmap.getInterpolatedHeight(wx0, wz0)
        val h1 = heightmap.getInterpolatedHeight(wx1, wz1)
        val h2 = heightmap.getInterpolatedHeight(wx0, wz1)
        val h3 = heightmap.getInterpolatedHeight(wx1, wz0)

        val minHeight = minOf(h0, h1, h2, h3) * heightmap.worldScale().y
        val maxHeight = maxOf(h0, h1, h2, h3) * heightmap.worldScale().y

        if (x > 0 && z > 0 && x < widthQuads && z < heightQuads && !ensureCellIsWalkable(cellBounds, maxSlope)) {
            val obstacleBounds = Rect3d(
                Vector3(cellMin.x, minHeight, cellMin.y),
                Vector3(cellMax.x, maxHeight, cellMax.y)
            )
            val obstacle = Obstacle(obstacleBounds)
            navGrid.insert(obstacle)
            color = unwalkableAreaColor
        }

        val searchResults = navGrid.buildSearchResults(
            BoxAABB(
                Rect3d(
                    Vector3(cellMin.x, 0f, cellMin.y),
                    Vector3(cellMax.x, 0f, cellMax.y)
                )
            )
        )

        if (searchResults.isNotEmpty()) {
            color = unwalkableAreaColor
        }

        return color
    }
}