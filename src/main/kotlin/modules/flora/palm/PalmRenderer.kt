package modules.flora.palm

import core.ecs.Behaviour
import core.math.Matrix4
import core.math.Vector3
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.model.Model
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import platform.services.filesystem.ObjLoader
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class PalmRenderer : Behaviour(), Renderer {
    private lateinit var material: PalmMaterial
    private lateinit var shader: PalmShader
    private lateinit var palmModel: Model

    private val viewProjection: Matrix4
        get() = Object.services.getService<Camera>()!!.viewProjection

    private val worldViewProjection: Matrix4
        get() = viewProjection * (owner()!! as Object).worldMatrix()

    override fun create() {
        val objLoader = Object.services.getService<ObjLoader>()!!
        palmModel = objLoader.load("models/palm/PalmTree_1.obj", "models/palm/PalmTree_1.mtl")

        for (i in 0..100) {
            val transform = Transform()
            val angle = Random.nextFloat() * 2 * PI.toFloat()
            val direction = Vector3(cos(angle) * i * 10f, 0f, sin(angle) * i * 10f)
            transform.setTranslation(direction)
            palmModel.addInstance(transform.matrix())
        }
        palmModel.createBuffers()

        material = PalmMaterial()
        shader = PalmShader()
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
        shader.updateUniforms()
        palmModel.draw()
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}