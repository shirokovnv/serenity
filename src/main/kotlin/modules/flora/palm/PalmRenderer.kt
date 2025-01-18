package modules.flora.palm

import core.ecs.Behaviour
import core.math.Matrix4
import core.math.Vector2
import core.math.Vector3
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.model.Model
import graphics.model.ModelMaterial
import graphics.model.ModelShader
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.terrain.heightmap.*
import platform.services.filesystem.ObjLoader
import kotlin.math.PI
import kotlin.random.Random

class PalmRenderer : Behaviour(), Renderer {
    lateinit var material: ModelMaterial
    private lateinit var shader: ModelShader
    private lateinit var models: MutableList<Model>

    private val viewProjection: Matrix4
        get() = Object.services.getService<Camera>()!!.viewProjection

    private val worldViewProjection: Matrix4
        get() = viewProjection * (owner()!! as Object).worldMatrix()

    override fun create() {
        val objLoader = Object.services.getService<ObjLoader>()!!

        val sampler = PoissonDiscSampler()
        val sampleRegionSize = Vector2(1600f, 1600f)
        val heightmap = Object.services.getService<Heightmap>()!!

        models = mutableListOf()
        val modelFiles = mapOf(
            "models/tree/PalmTree_1.obj" to "models/tree/PalmTree_1.mtl",
            "models/tree/PalmTree_2.obj" to "models/tree/PalmTree_2.mtl",

//            "models/tree/NormalTree_1.obj" to "models/tree/NormalTree_1.mtl",
//            "models/tree/NormalTree_2.obj" to "models/tree/NormalTree_2.mtl",
//
//            "models/tree/BirchTree_1.obj" to "models/tree/BirchTree_1.mtl",
//            "models/tree/BirchTree_2.obj" to "models/tree/BirchTree_2.mtl",
//
//            "models/tree/MapleTree_1.obj" to "models/tree/MapleTree_1.mtl",
//            "models/tree/MapleTree_2.obj" to "models/tree/MapleTree_2.mtl",
//
//            "models/tree/PineTree_1.obj" to "models/tree/PineTree_1.mtl",
//            "models/tree/PineTree_2.obj" to "models/tree/PineTree_2.mtl",
        )

        modelFiles.forEach{ (obj, mtl) ->
            val model = objLoader.load(obj, mtl)
            models.add(model)
        }

        val points = sampler.generatePoints(heightmap, PoissonDiscSamplerParams(
            35f,
            sampleRegionSize,
            30,
            0.3f,
            0.7f,
            0.5f
        ))

        println("NUM SAMPLING POINTS: ${points.size}")

        for (p in points) {
            val transform = Transform()
            val height = heightmap.getInterpolatedHeight(p.x, p.y) * heightmap.getWorldScale().y
            val position = Vector3(p.x, height, p.y)

            val angle = Random.nextFloat() * 2 * PI.toFloat()
            val rotation = Vector3(0f, angle, 0f)

            transform.setRotation(rotation)
            transform.setTranslation(position)
            transform.setScale(Vector3(10f))

            val randomModel = models.random()
            randomModel.addInstance(transform.matrix())
        }

        models.forEach{
            it.createBuffers()
            it.setupTextureFilters()
        }

        material = ModelMaterial()
        shader = ModelShader()
        shader bind material
        shader.setup()

        println("PALM RENDER BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
        material.worldViewProjection = worldViewProjection
    }

    override fun destroy() {
    }

    override fun render(pass: RenderPass) {
        shader.bind()
        models.forEach {model ->
            val mtlNames = model.getMaterialNames()
            mtlNames.forEach{ mtlName ->
                material.mtlData = model.getMtlDataByName(mtlName)
                material.isInstanced = model.isInstanced()
                shader.updateUniforms()
                model.drawByMaterial(mtlName)
            }
        }

        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}