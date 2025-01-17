package modules.flora.palm

import core.ecs.Behaviour
import core.math.Matrix4
import core.scene.Object
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.model.ModelLoader
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import platform.services.filesystem.TextFileLoader

class PalmRenderer: Behaviour(), Renderer {
    private lateinit var material: PalmMaterial
    private lateinit var shader: PalmShader
    private lateinit var buffers: MutableMap<String, PalmBuffer>

    private val viewProjection: Matrix4
        get() = Object.services.getService<Camera>()!!.viewProjection

    private val worldViewProjection: Matrix4
        get() = viewProjection * (owner()!! as Object).worldMatrix()

    override fun create() {
        val fileLoader = Object.services.getService<TextFileLoader>()!!
        val objLoader = ModelLoader()

        val palmObjSource = fileLoader.load("models/palm/PalmTree_1.obj")!!
        val palmMtlSource = fileLoader.load("models/palm/PalmTree_1.mtl")!!
        val palmData = objLoader.load(palmObjSource, palmMtlSource)
        buffers = mutableMapOf()

        palmData.forEach { (materialName, modelData) ->
            buffers[materialName] = PalmBuffer(modelData)
        }

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
        buffers.values.forEach { buffer -> buffer.draw() }
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}