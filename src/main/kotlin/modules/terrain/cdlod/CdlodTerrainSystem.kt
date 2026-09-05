package modules.terrain.cdlod

import core.math.IntersectionDetector
import core.math.Sphere
import core.math.Vector2
import core.math.Vector3
import core.scene.camera.Camera
import core.scene.camera.Frustum
import core.scene.spatial.QuadTreeCache
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class CdlodTerrainSystem(private val config: CdlodTerrainConfig) {
    private var root: CdlodTerrainSection
    private var sectionsToDraw = mutableListOf<CdlodTerrainSection>()
    private var lodRanges: FloatArray = FloatArray(config.maxLod)
    private val quadTreeCache = QuadTreeCache(1000, 30000L)

    private val scaleXZ: Float
        get() = max(config.worldScale.x, config.worldScale.z)

    init {
        calculateLodRanges()
        root = CdlodTerrainSection(
            config,
            Vector2(0.0f, 0.0f),
            0,
            lodRanges,
            quadTreeCache
        )
    }

    fun lodRanges() = lodRanges
    fun calculateLodRanges() {
        val minQuadDiagonal = (sqrt(2.0) * (1.0f / 2.0.pow((config.maxLod - 3).toDouble()))).toFloat()
        val minLodDistance = minQuadDiagonal * scaleXZ * config.distanceMultiplier

        for (i in 0..<config.maxLod) {
            val lodRange = minLodDistance * 2.0.pow(i.toDouble()).toFloat()
            lodRanges[i] = lodRange
        }
    }

    fun update(camera: Camera, frustum: Frustum) {
        sectionsToDraw.clear()
        collectSectionsToDraw(
            root,
            config.maxLod - 1,
            camera.position(),
            frustum
        )
    }

    fun prepareRenderData(): List<CdlodInstanceData> {
        return sectionsToDraw.map { section ->
            CdlodInstanceData(
                section.topLeft,
                Vector3(section.edgeLength(), 1.0f, section.edgeLength()),
                section.lod.toFloat(),
                Vector3(section.bounds().shape().min),
                Vector3(section.bounds().shape().max),
            )
        }.toList()
    }

    private fun collectSectionsToDraw(
        section: CdlodTerrainSection,
        lodLevel: Int,
        cameraPos: Vector3,
        frustum: Frustum
    ): Boolean {
        // If _treeDepth is greater than _lodLevels traverse down tree.
        if (lodLevel > config.maxLod) {
            section.split()
            section.children().forEach { child ->
                collectSectionsToDraw(
                    child as CdlodTerrainSection,
                    lodLevel - 1,
                    cameraPos,
                    frustum
                )
            }

            return true
        }

        section.lod = lodLevel

        val radius = lodRanges[lodLevel]
        if (!IntersectionDetector.intersects(section.bounds().shape(), Sphere(cameraPos, radius))) {
            return false
        }

        if (!frustum.checkRect3dInFrustum(section.bounds().shape())) {
            return true
        }

        if (lodLevel == 0) {
            // Always add LOD0 within range.
            section.lod = 0
            sectionsToDraw.add(section)

            return true
        } else {
            val innerRadius = lodRanges[lodLevel - 1]

            if (!IntersectionDetector.intersects(section.bounds().shape(), Sphere(cameraPos, innerRadius))) {
                // We now know this node is only covering one lodrange.
                // Add node to draw list.
                section.lod = lodLevel
                sectionsToDraw.add(section)

            } else {
                // If node is within LOD and also within range of LOD - 1
                // we add children of node that only covers LOD and skip
                // children that covers LOD - 1
                section.split()
                section.children().forEach { child ->
                    (child as CdlodTerrainSection).lod = lodLevel - 1

                    if (!collectSectionsToDraw(
                            child,
                            lodLevel - 1,
                            cameraPos,
                            frustum
                        )
                    ) {
                        // Add child to draw list that doesn't cover LOD - 1
                        sectionsToDraw.add(child)
                    }
                }
            }
        }
        return true
    }
}