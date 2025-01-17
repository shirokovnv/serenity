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
import modules.terrain.heightmap.Heightmap
import modules.terrain.heightmap.PoissonDiscSampler
import modules.terrain.heightmap.PoissonDiscSamplerParams
import platform.services.filesystem.ObjLoader

class PalmRenderer : Behaviour(), Renderer {
    lateinit var material: ModelMaterial
    private lateinit var shader: ModelShader
    private lateinit var palmModel: Model

    private val viewProjection: Matrix4
        get() = Object.services.getService<Camera>()!!.viewProjection

    private val worldViewProjection: Matrix4
        get() = viewProjection * (owner()!! as Object).worldMatrix()

    override fun create() {
        val objLoader = Object.services.getService<ObjLoader>()!!
        palmModel = objLoader.load("models/palm/PalmTree_1.obj", "models/palm/PalmTree_1.mtl")

        val sampler = PoissonDiscSampler()
        val sampleRegionSize = Vector2(1600f, 1600f)
        val heightmap = Object.services.getService<Heightmap>()!!

        val points = sampler.generatePoints(heightmap, PoissonDiscSamplerParams(
            50f,
            sampleRegionSize,
            30,
            0.3f,
            0.7f,
            0.3f
        ))

        for (p in points) {
            val transform = Transform()
            val height = heightmap.getInterpolatedHeight(p.x, p.y) * heightmap.getWorldScale().y
            val position = Vector3(p.x, height, p.y)
            transform.setTranslation(position)
            transform.setScale(Vector3(10f))
            palmModel.addInstance(transform.matrix())
        }
        palmModel.createBuffers()
        palmModel.setupTextureFilters()

        material = ModelMaterial()
        shader = ModelShader()
        shader bind material
        shader.setup()

        println("PALM RENDER BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
        material.worldViewProjection = worldViewProjection
        material.isInstanced = palmModel.isInstanced()
    }

    override fun destroy() {
    }

    override fun render(pass: RenderPass) {
        val materialNames = palmModel.getMaterialNames()

        shader.bind()
        materialNames.forEach { materialName ->
            material.mtlData = palmModel.getMtlDataByName(materialName)
            shader.updateUniforms()
            palmModel.drawByMaterial(materialName)
        }
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}