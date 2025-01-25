package modules.flora.grass

import core.ecs.Behaviour
import core.management.Resources
import core.math.*
import core.scene.BoxAABB
import core.scene.camera.Camera
import core.scene.spatial.LinearQuadTree
import core.scene.spatial.SpatialPartitioningInterface
import graphics.assets.surface.bind
import graphics.assets.texture.TextureChannel
import graphics.model.Model
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.terrain.heightmap.Blendmap
import modules.terrain.heightmap.Heightmap
import org.lwjgl.glfw.GLFW.glfwGetTime
import org.lwjgl.opengl.GL43.*
import platform.services.FrameCounter
import platform.services.filesystem.ObjLoader

class GrassBehaviour : Behaviour(), Renderer {
    private lateinit var grassModel: Model
    private lateinit var material: GrassPatchMaterial
    private lateinit var shader: GrassPatchShader
    private lateinit var grassPatchParams: GrassPatchParams
    private lateinit var grassGenerator: GrassGenerator

    private lateinit var quadTree: SpatialPartitioningInterface

    private var blendMapReady: Boolean = false

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val heightmap: Heightmap
        get() = Resources.get<Heightmap>()!!

    override fun create() {
        val objLoader = Resources.get<ObjLoader>()!!
        grassModel = objLoader.load("models/grass/grass.obj")

        material = GrassPatchMaterial()
        shader = GrassPatchShader()
        shader bind material
        shader.setup()

        grassPatchParams = GrassPatchParams(0.08f, 3.0f, 2, 100.0f)
        grassGenerator = GrassGenerator()
        grassGenerator.generateInstances(grassModel, grassPatchParams.spacing)
        grassModel.createBuffers()
        grassModel.setupTextureFilters()

        quadTree = LinearQuadTree()
        (quadTree as LinearQuadTree).create(
            Rect3d(Vector3(0f, 0f, 0f), heightmap.worldScale()),
            9
        )

        println("GRASS RENDER BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
        if (!blendMapReady) {
            val blendmap = Resources.get<Blendmap>()

            if (blendmap != null) {
                val patches = grassGenerator.generatePatches(heightmap, blendmap, grassPatchParams, TextureChannel.R)
                patches.forEach { grassPatch ->
                    quadTree.insert(grassPatch)
                }

                println("NUM GRASS PATCHES: ${patches.size}")

                blendMapReady = true
            }
        }
    }

    override fun destroy() {
        grassModel.dispose()
        shader.destroy()
    }

    override fun render(pass: RenderPass) {
        material.viewMatrix = camera.view
        material.projMatrix = camera.projection
        material.time = glfwGetTime().toFloat()

        shader.bind()

        val cameraPosition = camera.position()
        val searchVolume = BoxAABB(
            Rect3d(
                Vector3(
                    cameraPosition.x - grassPatchParams.viewRange,
                    cameraPosition.y - grassPatchParams.viewRange,
                    cameraPosition.z - grassPatchParams.viewRange
                ),
                Vector3(
                    cameraPosition.x + grassPatchParams.viewRange,
                    cameraPosition.y + grassPatchParams.viewRange,
                    cameraPosition.z + grassPatchParams.viewRange
                )
            )
        )

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        quadTree
            .buildSearchResults(searchVolume)
            .filterIsInstance<GrassPatch>()
            .forEach { grassPatch ->
                material.worldMatrix = grassPatch.worldMatrix()
                shader.updateUniforms()
                grassModel.draw()
            }

        glDisable(GL_BLEND)

        shader.unbind()

        println("fps: ${Resources.get<FrameCounter>()!!.fps()}")
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}