package modules.sky

import core.ecs.Behaviour
import core.management.Resources
import core.math.Matrix4
import core.math.Vector2
import core.math.Vector3
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import modules.light.AtmosphereConstantsSsbo
import platform.services.filesystem.ImageLoader

class SkyDomeBehaviour(
    private val params: SkyDomeParams = SkyDomeParams(),
    private val enablePostProcessing: Boolean = true
) : Behaviour() {
    private lateinit var material: SkyDomeMaterial
    private lateinit var shader: SkyDomeShader
    private lateinit var buffer: SkyDomeBuffer
    private lateinit var cloudTexture: Texture2d

    private lateinit var ppMaterial: SkyDomePPMaterial
    private lateinit var ppShader: SkyDomePPShader

    private lateinit var renderer: SkyDomeRenderer
    private lateinit var ppRenderer: SkyDomePPRenderer

    private var rotationAngle: Float = 0f

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val worldViewProjection: Matrix4
        get() = camera.viewProjection * (owner()!! as Object).worldMatrix()

    private val atmosphereConstantsSsbo: AtmosphereConstantsSsbo
        get() = Resources.get<AtmosphereConstantsSsbo>()!!

    override fun create() {
        material = SkyDomeMaterial()
        shader = SkyDomeShader()
        shader bind material

        cloudTexture = Texture2d(Resources.get<ImageLoader>()!!.loadImage("textures/sky/clouds.jpg"))
        cloudTexture.bind()
        cloudTexture.bilinearFilter()

        material.cloudTexture = cloudTexture
        material.cloudAnimationOffset = Vector2(0f, 0f)

        shader.setup()

        val skyDomeMesh = SkyDomeMesh(params.numRows, params.numCols, params.radius)
        buffer = SkyDomeBuffer(skyDomeMesh)

        renderer = SkyDomeRenderer(buffer, material, shader, atmosphereConstantsSsbo)
        (owner() as Object).addComponent(renderer)

        if (enablePostProcessing) {
            ppMaterial = SkyDomePPMaterial()
            ppShader = SkyDomePPShader()
            ppShader bind ppMaterial
            ppShader.setup()

            ppRenderer = SkyDomePPRenderer(buffer, ppMaterial, ppShader, atmosphereConstantsSsbo)
            (owner() as Object).addComponent(ppRenderer)
        }

        println("SKY DOME RENDER INITIALIZED")
    }

    override fun update(deltaTime: Float) {
        rotationAngle += params.rotationSpeed
        owner()!!.getComponent<Transform>()!!.setTranslation(camera.position() - Vector3(0f, params.yOffset, 0f))

        material.cloudAnimationOffset.plusAssign(Vector2(deltaTime * params.rotationSpeed, 0f))
        material.worldViewProjection = worldViewProjection

        ppMaterial.worldViewProjection = worldViewProjection
    }

    override fun destroy() {
        cloudTexture.destroy()
        shader.destroy()
        if (enablePostProcessing) {
            ppShader.destroy()
        }
        buffer.destroy()
    }
}