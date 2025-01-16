package modules.sky

import core.ecs.Behaviour
import core.math.Matrix4
import core.math.Vector2
import core.math.Vector3
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.light.AtmosphereConstantsSsbo
import platform.services.filesystem.ImageLoader

class SkyDomeRenderer(private val params: SkyDomeParams = SkyDomeParams()) : Behaviour(), Renderer {
    private lateinit var material: SkyDomeMaterial
    private lateinit var shader: SkyDomeShader
    private lateinit var buffer: SkyDomeBuffer

    private var rotationAngle: Float = 0f

    private val camera: Camera
        get() = Object.services.getService<Camera>()!!

    private val worldViewProjection: Matrix4
        get() = camera.viewProjection * (owner()!! as Object).worldMatrix()

    private val atmosphereConstantsSsbo: AtmosphereConstantsSsbo
        get() = Object.services.getService<AtmosphereConstantsSsbo>()!!

    override fun create() {
        material = SkyDomeMaterial()
        shader = SkyDomeShader()
        shader bind material

        val cloudTexture = Texture2d(Object.services.getService<ImageLoader>()!!.loadImage("textures/sky/clouds.jpg"))
        cloudTexture.bind()
        cloudTexture.bilinearFilter()

        material.worldViewProjection = worldViewProjection
        material.cloudTexture = cloudTexture
        material.cloudAnimationOffset = Vector2(0f, 0f)

        shader.setup()

        val skyDomeMesh = SkyDomeMesh(params.numRows, params.numCols, params.radius)
        buffer = SkyDomeBuffer(skyDomeMesh)

        println("SKY DOME RENDER INITIALIZED")
    }

    override fun update(deltaTime: Float) {
        rotationAngle += params.rotationSpeed
        owner()!!.getComponent<Transform>()!!.setTranslation(camera.position() - Vector3(0f, params.yOffset, 0f))

        material.cloudAnimationOffset.plusAssign(Vector2(deltaTime * params.rotationSpeed, 0f))
        material.worldViewProjection = worldViewProjection
    }

    override fun destroy() {
    }

    override fun render(pass: RenderPass) {
        shader.bind()
        shader.updateUniforms()
        atmosphereConstantsSsbo.setBindingPoint(0)
        atmosphereConstantsSsbo.bind()
        buffer.draw()
        atmosphereConstantsSsbo.unbind()
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}