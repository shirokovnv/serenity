package modules.terrain.objects.flora.trees

import core.ecs.Behaviour
import core.events.Events
import core.management.Resources
import core.math.*
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.Frustum
import core.scene.camera.OrthographicCamera
import core.scene.camera.PerspectiveCamera
import core.scene.volumes.BoxAABB
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
import graphics.rendering.passes.ReflectionPass
import graphics.rendering.passes.RefractionPass
import graphics.rendering.passes.RenderPass
import modules.light.SunLightManager
import modules.terrain.heightmap.HeightAndSlopeBasedValidator
import modules.terrain.heightmap.Heightmap
import modules.terrain.sampling.PoissonDiscSampler
import modules.terrain.sampling.PoissonDiscSamplerParams
import modules.water.plane.WaterPlaneConstants
import platform.services.filesystem.ObjLoader
import kotlin.math.PI
import kotlin.random.Random

class TreeSetBehaviour(private val enablePostProcessing: Boolean = true) : Behaviour() {
    private lateinit var material: ModelMaterial
    private lateinit var ppMaterial: TreeSetPPMaterial
    private lateinit var shader: ModelShader
    private lateinit var ppShader: TreeSetPPShader
    private lateinit var models: MutableList<Model>
    private lateinit var renderer: ModelRenderer
    private lateinit var ppRenderer: TreeSetPPRenderer
    private lateinit var frustum: Frustum

    private val meshVertices = mutableListOf<Vector3>()

    private val viewProjectionProvider: Matrix4
        get() = Resources.get<Camera>()!!.viewProjection

    private val lightViewProvider: Matrix4
        get() = Resources.get<SunLightManager>()!!.calculateLightViewMatrix()

    private val orthoProjectionProvider: Matrix4
        get() = Resources.get<OrthographicCamera>()!!.projection

    private val clipPlanes: Map<RenderPass, Quaternion>
        get() {
            return mapOf(
                RefractionPass to Quaternion(0f, -1f, 0f, WaterPlaneConstants.DEFAULT_WORLD_HEIGHT),
                ReflectionPass to Quaternion(0f, 1f, 0f, -WaterPlaneConstants.DEFAULT_WORLD_HEIGHT + 1.0f)
            )
        }

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

        val samplingParams = PoissonDiscSamplerParams(75f, sampleRegionSize, 30)
        val validator = HeightAndSlopeBasedValidator(heightmap, 0.2f, 0.9f, 0.3f)

        val points = sampler.generatePoints(
            samplingParams,
            validator
        )
        val treeSamplingContainer = TreeSamplingContainer(points, samplingParams.radius / 2, samplingParams.radius)
        Resources.put<TreeSamplingContainer>(treeSamplingContainer)

        println("NUM TREE SAMPLING POINTS: ${points.size}")

        for (p in points) {
            val transform = Transform()
            val height = heightmap.getInterpolatedHeight(p.x, p.y) * heightmap.worldScale().y
            val position = Vector3(p.x, height, p.y)

            val angle = Random.nextFloat() * 2 * PI.toFloat()
            val rotation = Vector3(0f, angle, 0f)

            transform.set(rotation, Vector3(10f), position)

            val randomModel = models.random()
            val instanceId = randomModel.addInstance(transform.matrix())

            val treeInstance = TreeInstance(randomModel, instanceId)
            treeInstance.transform().set(transform)
            treeInstance.addComponent(BoxAABBDrawer(Colors.Green))
            (owner() as Object).addChild(treeInstance)

            randomModel.getModelData().values.forEach { modelData ->
                val bounds = BoxAABBFactory.fromVertices(modelData.vertices, 3, modelData.indices)
                treeInstance.getComponent<BoxAABBHierarchy>()!!.add(bounds)
            }
            treeInstance.recalculateBounds()
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
            clipPlanes,
            { viewProjectionProvider },
            { orthoProjectionProvider },
            { lightViewProvider }
        )

        (owner() as Object).addComponent(renderer)

        if (enablePostProcessing) {
            ppMaterial = TreeSetPPMaterial(material)
            ppShader = TreeSetPPShader()
            ppShader bind ppMaterial
            ppShader.setup()

            ppRenderer = TreeSetPPRenderer(models, ppMaterial, ppShader) { viewProjectionProvider }

            (owner() as Object).addComponent(ppRenderer)
        }

        (owner() as Object).addComponent(
            MeshDrawer(
                Resources.get<Camera>()!!,
                { meshVertices },
                Colors.Yellow,
            )
        )

        Events.subscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        frustum = Frustum(Resources.get<Camera>()!! as PerspectiveCamera)

        println("PALM RENDER BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
    }

    override fun destroy() {
        Events.unsubscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        (owner() as Object)
            .getChildren()
            .filterIsInstance<TreeInstance>()
            .forEach { treeInstance ->
                treeInstance.getComponent<BoxAABBDrawer>()?.dispose()
                treeInstance.dispose()
            }

        shader.destroy()
        if (enablePostProcessing) {
            ppShader.destroy()
        }
        models.forEach { model ->
            model.destroyBuffers()
            model.destroyTextures()
        }
    }

    private fun onDrawGizmos(event: DrawGizmosEvent, sender: Any) {
        frustum.recalculateSearchVolume()
        meshVertices.clear()

        (owner() as Object)
            .getChildren()
            .filterIsInstance<TreeInstance>()
            .filter { treeInstance ->
                IntersectionDetector.intersects(
                    frustum.searchVolume().shape(),
                    treeInstance.getComponent<BoxAABB>()!!.shape()
                )
            }
            .forEach { treeInstance ->
                treeInstance.getComponent<BoxAABBDrawer>()?.draw()
                collectMeshVertices(treeInstance)
            }

        owner()!!.getComponent<MeshDrawer>()!!.draw()
    }

    private fun collectMeshVertices(instance: TreeInstance) {
        val model = instance.getTreeModel()
        val worldMatrix = model.getInstance(instance.getInstanceId())

        model.getModelData().values.forEach { modelData ->
            for (i in modelData.indices) {
                val offset = i * 3

                val originalVertex = Vector3(
                    modelData.vertices[offset],
                    modelData.vertices[offset + 1],
                    modelData.vertices[offset + 2]
                )
                val worldVertex = (worldMatrix * Quaternion(originalVertex, 1.0f)).xyz()
                meshVertices.add(worldVertex)
            }
        }
    }
}