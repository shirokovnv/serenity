package modules.terrain.objects.rocks

import core.events.Events
import core.management.Resources
import core.math.Matrix4
import core.math.Vector2
import core.math.Vector3
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.Frustum
import core.scene.camera.OrthographicCamera
import core.scene.camera.PerspectiveCamera
import core.scene.volumes.BoxAABBFactory
import core.scene.volumes.BoxAABBHierarchy
import graphics.assets.surface.bind
import graphics.model.Model
import graphics.model.ModelMaterial
import graphics.model.ModelRenderer
import graphics.model.ModelShader
import graphics.rendering.Colors
import graphics.rendering.gizmos.BoxAABBDrawer
import graphics.rendering.gizmos.DrawGizmosEvent
import graphics.rendering.gizmos.MeshDrawer
import modules.light.SunLightManager
import modules.terrain.heightmap.HeightAndSlopeBasedValidator
import modules.terrain.heightmap.Heightmap
import modules.terrain.objects.BaseBehaviour
import modules.terrain.objects.ObjectProviders
import modules.terrain.objects.flora.trees.TreeSamplingContainer
import modules.terrain.sampling.PoissonDiscSampler
import modules.terrain.sampling.PoissonDiscSamplerParams
import platform.services.filesystem.ObjLoader
import kotlin.math.PI
import kotlin.random.Random

class RockSetBehaviour : BaseBehaviour() {

    private lateinit var models: MutableList<Model>
    private lateinit var material: ModelMaterial
    private lateinit var shader: ModelShader
    private lateinit var renderer: ModelRenderer
    override lateinit var frustum: Frustum

    private val viewProjectionProvider: Matrix4
        get() = Resources.get<Camera>()!!.viewProjection

    private val lightViewProvider: Matrix4
        get() = Resources.get<SunLightManager>()!!.calculateLightViewMatrix()

    private val orthoProjectionProvider: Matrix4
        get() = Resources.get<OrthographicCamera>()!!.projection

    override fun create() {
        val objLoader = Resources.get<ObjLoader>()!!

        val modelFiles = mapOf(
            "models/rock/Rock_1.obj" to "models/rock/Rock_1.mtl",
            "models/rock/Rock_2.obj" to "models/rock/Rock_2.mtl",
        )

        models = mutableListOf()
        modelFiles.forEach { (obj, mtl) ->
            val model = objLoader.load(obj, mtl)
            models.add(model)
        }

        val sampler = PoissonDiscSampler()
        val heightmap = Resources.get<Heightmap>()!!
        val sampleRegionSize = Vector2(heightmap.worldScale().x, heightmap.worldScale().z)
        val samplingParams = PoissonDiscSamplerParams(50f, sampleRegionSize, 30)
        val validator = HeightAndSlopeBasedValidator(heightmap, 0.2f, 1.0f, 0.4f)

        val initialPoints = sampler.generatePoints(
            samplingParams,
            validator
        )
        val samplingContainer = RockSamplingContainer(initialPoints, samplingParams.radius / 2, samplingParams.radius)

        // TODO: ensure tree set initialized first
        samplingContainer.reducePointsByObstacles(
            Resources.get<TreeSamplingContainer>()!!
        )
        Resources.put<RockSamplingContainer>(samplingContainer)

        val points = samplingContainer.points

        println("NUM ROCK SAMPLING POINTS: ${initialPoints.size}")

        for (p in points) {
            val transform = Transform()
            val height = heightmap.getInterpolatedHeight(p.x, p.y) * heightmap.worldScale().y
            val position = Vector3(p.x, height, p.y)

            val angle = Random.nextFloat() * 2 * PI.toFloat()
            val rotation = Vector3(0f, angle, 0f)

            transform.set(rotation, Vector3(5f), position)

            val randomModel = models.random()
            val instanceId = randomModel.addInstance(transform.matrix())

            val rockInstance = RockInstance(randomModel, instanceId)
            rockInstance.transform().set(
                transform.rotation(),
                transform.scale(),
                transform.translation()
            )
            rockInstance.addComponent(BoxAABBDrawer(Colors.Cyan))
            (owner() as Object).addChild(rockInstance)

            randomModel.getModelData().values.forEach { modelData ->
                val bounds = BoxAABBFactory.fromVertices(modelData.vertices, 3, modelData.indices)
                rockInstance.getComponent<BoxAABBHierarchy>()!!.add(bounds)
            }
            rockInstance.recalculateBounds()
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
            ObjectProviders.clipPlanes,
            { viewProjectionProvider },
            { orthoProjectionProvider },
            { lightViewProvider }
        )

        (owner() as Object).addComponent(renderer)

        (owner() as Object).addComponent(
            MeshDrawer(
                Resources.get<Camera>()!!,
                { meshVertices },
                Colors.Magenta,
            )
        )

        frustum = Frustum(Resources.get<Camera>()!! as PerspectiveCamera)

        Events.subscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        println("ROCK RENDER BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {

    }

    override fun destroy() {
        Events.unsubscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        (owner() as Object)
            .getChildren()
            .filterIsInstance<RockInstance>()
            .forEach { rockInstance ->
                rockInstance.getComponent<BoxAABBDrawer>()?.dispose()
                rockInstance.dispose()
            }

        shader.destroy()

        models.forEach { model ->
            model.destroyBuffers()
            model.destroyTextures()
        }
    }
}