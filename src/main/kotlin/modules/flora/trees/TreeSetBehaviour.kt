package modules.flora.trees

import core.ecs.Behaviour
import core.management.Resources
import core.math.Matrix4
import core.math.Vector2
import core.math.Vector3
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.OrthographicCamera
import graphics.assets.surface.bind
import graphics.model.Model
import graphics.model.ModelMaterial
import graphics.model.ModelRenderer
import graphics.model.ModelShader
import modules.light.SunLightManager
import modules.terrain.heightmap.Heightmap
import modules.terrain.heightmap.PoissonDiscSampler
import modules.terrain.heightmap.PoissonDiscSamplerParams
import platform.services.filesystem.ObjLoader
import kotlin.math.PI
import kotlin.random.Random

class TreeSetBehaviour : Behaviour() {
    lateinit var material: ModelMaterial
    private lateinit var shader: ModelShader
    private lateinit var models: MutableList<Model>
    private lateinit var renderer: ModelRenderer

    private val viewProjectionProvider: Matrix4
        get() = Resources.get<Camera>()!!.viewProjection

    private val lightViewProvider: Matrix4
        get() = Resources.get<SunLightManager>()!!.calculateLightViewMatrix()

    private val orthoProjectionProvider: Matrix4
        get() = Resources.get<OrthographicCamera>()!!.projection

    override fun create() {
        val objLoader = Resources.get<ObjLoader>()!!

        val sampler = PoissonDiscSampler()
        val heightmap = Resources.get<Heightmap>()!!
        val sampleRegionSize = Vector2(heightmap.worldScale().x, heightmap.worldScale().z)

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

        modelFiles.forEach { (obj, mtl) ->
            val model = objLoader.load(obj, mtl)
            models.add(model)
        }

        val points = sampler.generatePoints(
            heightmap, PoissonDiscSamplerParams(
                50f,
                sampleRegionSize,
                30,
                0.2f,
                0.9f,
                0.3f
            )
        )

        println("NUM SAMPLING POINTS: ${points.size}")

        for (p in points) {
            val transform = Transform()
            val height = heightmap.getInterpolatedHeight(p.x, p.y) * heightmap.worldScale().y
            val position = Vector3(p.x, height, p.y)

            val angle = Random.nextFloat() * 2 * PI.toFloat()
            val rotation = Vector3(0f, angle, 0f)

            transform.setRotation(rotation)
            transform.setTranslation(position)
            transform.setScale(Vector3(10f))

            val randomModel = models.random()
            randomModel.addInstance(transform.matrix())
        }

        models.forEach {
            it.createBuffers()
            it.setupTextureFilters()
        }

        material = ModelMaterial()
        shader = ModelShader()
        shader bind material
        shader.setup()

        renderer = ModelRenderer(
            models,
            material,
            shader,
            { viewProjectionProvider },
            { orthoProjectionProvider },
            { lightViewProvider }
        )

        (owner() as Object).addComponent(renderer)

        println("PALM RENDER BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
    }

    override fun destroy() {
    }
}