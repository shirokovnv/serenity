package modules.sky.box

import core.ecs.Behaviour
import core.management.Resources
import core.math.Matrix4
import core.scene.Object
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.assets.texture.CubemapTexture
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import platform.services.filesystem.ImageLoader

class SkyBoxBehaviour : Behaviour(), Renderer {

    private lateinit var buffer: SkyBoxBuffer
    private lateinit var cubemapTexture: CubemapTexture
    private lateinit var material: SkyBoxMaterial
    private lateinit var shader: SkyBoxShader

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val worldViewProjection: Matrix4
        get() {
            val view = camera.view

            view[0, 3] = 0.0f
            view[1, 3] = 0.0f
            view[2, 3] = 0.0f

            return camera.projection * view * (owner()!! as Object).worldMatrix()
        }

    override fun create() {
        val imageLoader = Resources.get<ImageLoader>()!!

        cubemapTexture = imageLoader.loadCubeImage(
            "textures/sky/right.jpg",
            "textures/sky/left.jpg",
            "textures/sky/top.jpg",
            "textures/sky/bottom.jpg",
            "textures/sky/back.jpg",
            "textures/sky/front.jpg"
        )

        buffer = SkyBoxBuffer()
        material = SkyBoxMaterial()
        shader = SkyBoxShader()
        shader bind material
        shader.setup()

        material.cubemapTexture = cubemapTexture

        println("SKYBOX RENDER BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
        material.worldViewProjection = worldViewProjection
    }

    override fun destroy() {
        cubemapTexture.dispose()
        shader.destroy()
        buffer.destroy()
    }

    override fun render(pass: RenderPass) {
        shader.bind()
        shader.updateUniforms()
        buffer.draw()
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}