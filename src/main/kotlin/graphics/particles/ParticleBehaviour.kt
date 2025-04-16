package graphics.particles

import core.management.Resources
import core.math.Matrix4
import core.math.Quaternion
import core.math.Vector3
import core.scene.Transform
import core.scene.behaviour.FrameUpdateBehaviour
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import graphics.particles.emitters.SphericalEmitter
import graphics.particles.interfaces.ParticleEmitterInterface
import graphics.particles.interfaces.ParticleUpdateStrategyInterface
import graphics.particles.strategies.WithGravityStrategy
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import org.lwjgl.opengl.GL43
import platform.services.filesystem.ImageLoader

class ParticleBehaviour : FrameUpdateBehaviour(), Renderer {
    private lateinit var buffer: ParticleBatchBuffer
    private lateinit var shader: ParticleShader
    private lateinit var material: ParticleMaterial
    private lateinit var texture: Texture2d

    private lateinit var emitter: ParticleEmitterInterface
    private lateinit var updateStrategy: ParticleUpdateStrategyInterface
    private lateinit var particleSystem: ParticleSystem

    private val particleProps = ParticleProps(
        Vector3(0f),
        Vector3(0f),
        Vector3(1.0f, 1.0f, 1.0f),
        Quaternion(254 / 255.0f, 212 / 255.0f, 123 / 255.0f, 1.0f),
        Quaternion(254 / 255.0f, 109 / 255.0f, 41 / 255.0f, 1.0f),
        5.0f,
        0.0f,
        0.3f,
        100.0f
    )

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val transform: Transform
        get() = owner()!!.getComponent<Transform>()!!

    private val model: Matrix4
        get() = transform.matrix()

    override fun create() {
        emitter = SphericalEmitter()

        updateStrategy = WithGravityStrategy(0.01f)
        particleSystem = ParticleSystem(emitter, updateStrategy)

        buffer = ParticleBatchBuffer(1000)
        material = ParticleMaterial()
        shader = ParticleShader()
        shader bind material
        shader.setup()

        val imageLoader = Resources.get<ImageLoader>()!!

        texture = Texture2d(imageLoader.loadImage("textures/particles/particleAtlas.png"))
        texture.bind()
        texture.bilinearFilter()
        texture.unbind()
    }

    override fun onUpdate(deltaTime: Float) {
        material.model = model
        material.view = camera.view
        material.projection = camera.projection
        material.texture = texture
        material.textureNumRows = 4

        particleSystem.emit(particleProps)

        particleSystem.onUpdate(0.05f)
    }

    override fun destroy() {
        shader.destroy()
        buffer.destroy()
        texture.destroy()
    }

    override fun render(pass: RenderPass) {
        buffer.uploadData(particleSystem.activeParticles())

        GL43.glEnable(GL43.GL_BLEND)
        GL43.glBlendFunc(GL43.GL_SRC_ALPHA, GL43.GL_ONE)
        GL43.glDepthMask(false)

        shader.bind()
        shader.updateUniforms()
        buffer.draw()
        shader.unbind()

        GL43.glDisable(GL43.GL_BLEND)
        GL43.glDepthMask(true)
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}