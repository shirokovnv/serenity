package modules.ocean

import core.ecs.Behaviour
import core.management.Resources
import core.math.Matrix4
import core.math.Vector2
import core.math.Vector3
import core.math.extensions.toRadians
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.Frustum
import core.scene.camera.PerspectiveCamera
import graphics.assets.texture.Texture2d
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.ocean.shaders.OceanFftShader
import org.lwjgl.glfw.GLFW.glfwGetTime
import org.lwjgl.opengl.*
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class OceanRenderer(
    private val params: OceanParams,
    private val stretchToHorizon: Boolean = false
) : Behaviour(), Renderer {
    companion object {
        private const val WORK_GROUP_DIM = 16
    }

    private lateinit var material: OceanMaterial
    private lateinit var camera: Camera
    private lateinit var frustum: Frustum
    private lateinit var oceanBuffer: OceanBuffer
    private lateinit var texInitializer: OceanTextureInitializer
    private lateinit var shaderInitializer: OceanShaderInitializer

    private var isFirstFrame: Boolean = true

    private val model: Matrix4
        get() = (owner()!! as Object).localMatrix()

    private val view: Matrix4
        get() = camera.view

    private val projection: Matrix4
        get() = camera.projection

    private val oceanScale: Vector3
        get() = owner()!!.getComponent<Transform>()!!.scale()

    override fun create() {
        camera = Resources.get<Camera>()!!
        frustum = Frustum(camera as PerspectiveCamera)
        material = OceanMaterial()

        texInitializer = OceanTextureInitializer(params.fftResolution)
        shaderInitializer = OceanShaderInitializer(material)

        material.view = view
        material.model = model
        material.projection = projection
        material.displacementMap = texInitializer.dispTex
        material.normalMap = texInitializer.normalTex
        material.fftResolution = params.fftResolution
        material.oceanSize = params.meshResolution
        material.amplitude = params.amplitude
        material.choppiness = params.choppiness
        material.wind = Vector2(
            params.windMagnitude * cos(params.windAngle.toRadians()),
            params.windMagnitude * sin(params.windAngle.toRadians())
        )

        val oceanMesh = OceanMesh(params.meshResolution, (params.meshResolution / params.fftResolution).toFloat())
        oceanBuffer = OceanBuffer(oceanMesh)

        println("OCEAN RENDER BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
        material.view = view
        material.time = glfwGetTime().toFloat()

        (owner()!! as Object).getComponent<Transform>()!!.setTranslation(Vector3(0f, 60f, 0f))
        material.model = model

//        (owner()!! as Object).getComponent<Transform>()!!.setTranslation(
//            Vector3(camera.position().x, 0f, camera.position().z)
//        )
//
//        material.model = model
    }

    override fun destroy() {
    }

    override fun render(pass: RenderPass) {
        if (isFirstFrame) {
            renderInitialSpectrum()

            isFirstFrame = false
        } else {
            renderSpectrumUpdate()
            renderOrientation(texInitializer.dyTex, texInitializer.spectrumT)

            renderFft(shaderInitializer.fftColumnsShader, OceanTextureType.DY)
            renderFft(shaderInitializer.fftRowsShader, OceanTextureType.DY)

            renderFft(shaderInitializer.fftRowsShader, OceanTextureType.DX)
            renderFft(shaderInitializer.fftColumnsShader, OceanTextureType.DX)

            renderFft(shaderInitializer.fftRowsShader, OceanTextureType.DZ)
            renderFft(shaderInitializer.fftColumnsShader, OceanTextureType.DZ)

            renderNormals()

            renderMesh()
        }
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }

    private fun renderInitialSpectrum() {
        shaderInitializer.spectrum0Shader.bind()
        shaderInitializer.spectrum0Shader.updateUniforms()

        // DRAW ??
        GL42.glBindImageTexture(0, texInitializer.spectrum0.getId(), 0, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_RGBA32F)
        GL42.glBindImageTexture(1, texInitializer.gaussTex.getId(), 0, false, 0, GL15.GL_READ_ONLY, GL43.GL_RGBA32F)

        GL43.glDispatchCompute(params.fftResolution / WORK_GROUP_DIM, params.fftResolution / WORK_GROUP_DIM, 1)
        GL11.glFinish()

        shaderInitializer.spectrum0Shader.unbind()
    }

    private fun renderSpectrumUpdate() {
        shaderInitializer.spectrumTShader.bind()
        shaderInitializer.spectrumTShader.updateUniforms()

        // DRAW ??
        GL42.glBindImageTexture(0, texInitializer.spectrum0.getId(), 0, false, 0, GL15.GL_READ_ONLY, GL30.GL_RGBA32F)
        GL42.glBindImageTexture(1, texInitializer.dyTex.getId(), 0, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_RGBA32F)
        GL42.glBindImageTexture(2, texInitializer.dxTex.getId(), 0, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_RGBA32F)
        GL42.glBindImageTexture(3, texInitializer.dzTex.getId(), 0, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_RGBA32F)

        GL43.glDispatchCompute(params.fftResolution / WORK_GROUP_DIM, params.fftResolution / WORK_GROUP_DIM, 1)
        GL11.glFinish()

        shaderInitializer.spectrumTShader.unbind()
    }

    private fun renderOrientation(inTex: Texture2d, outTex: Texture2d) {
        shaderInitializer.orientationShader.bind()
        shaderInitializer.orientationShader.updateUniforms()

        // DRAW ??
        GL42.glBindImageTexture(0, inTex.getId(), 0, false, 0, GL15.GL_READ_ONLY, GL30.GL_RGBA32F)
        GL42.glBindImageTexture(1, outTex.getId(), 0, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_RGBA32F)

        GL43.glDispatchCompute(params.fftResolution / WORK_GROUP_DIM, params.fftResolution / WORK_GROUP_DIM, 1)
        GL11.glFinish()

        shaderInitializer.orientationShader.unbind()
    }

    private fun renderFft(shader: OceanFftShader, texType: OceanTextureType) {
        shader.bind()

        var swapTemp = false
        var stride = 1
        var count: Int = params.fftResolution
        while (count >= 1) {

            // DRAW ??
            GL42.glBindImageTexture(
                0,
                texInitializer.getTexture(texType).getId(),
                0,
                false,
                0,
                GL15.GL_READ_ONLY,
                GL30.GL_RGBA32F
            )
            GL42.glBindImageTexture(1, texInitializer.tempTex.getId(), 0, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_RGBA32F)

            material.stride = stride
            material.count = count
            shader.updateUniforms()

            GL43.glDispatchCompute(params.fftResolution, params.fftResolution / 2, 1)
            GL43.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT)

            texInitializer.swapWithTemp(texType)

            stride = stride shl 1
            count = count shr 1

            swapTemp = !swapTemp
        }

        if (swapTemp) {
            texInitializer.swapWithTemp(texType)
        }

        GL43.glFinish()
        shader.unbind()
    }

    private fun renderNormals() {
        shaderInitializer.normalShader.bind()
        shaderInitializer.normalShader.updateUniforms()

        // DRAW ??
        GL42.glBindImageTexture(0, texInitializer.dyTex.getId(), 0, false, 0, GL15.GL_READ_ONLY, GL30.GL_RGBA32F)
        GL42.glBindImageTexture(1, texInitializer.dxTex.getId(), 0, false, 0, GL15.GL_READ_ONLY, GL30.GL_RGBA32F)
        GL42.glBindImageTexture(2, texInitializer.dzTex.getId(), 0, false, 0, GL15.GL_READ_ONLY, GL30.GL_RGBA32F)
        GL42.glBindImageTexture(3, texInitializer.normalTex.getId(), 0, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_RGBA32F)
        GL42.glBindImageTexture(4, texInitializer.dispTex.getId(), 0, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_RGBA32F)

        GL43.glDispatchCompute(params.fftResolution / WORK_GROUP_DIM, params.fftResolution / WORK_GROUP_DIM, 1)
        GL11.glFinish()

        shaderInitializer.normalShader.unbind()
    }

    private fun renderMesh() {
        var x0 = 0
        var x1 = 1
        var z0 = 0
        var z1 = 1

        if (stretchToHorizon) {
            frustum.recalculateSearchVolume()
            val searchVolume = frustum.searchVolume().shape()

            x0 = floor(searchVolume.min.x / oceanScale.x).toInt()
            x1 = ceil(searchVolume.max.x / oceanScale.x).toInt()
            z0 = floor(searchVolume.min.z / oceanScale.z).toInt()
            z1 = ceil(searchVolume.max.z / oceanScale.z).toInt()
        }

        shaderInitializer.meshShader.bind()
        for (x in x0..<x1) {
            for (z in z0..<z1) {
                material.offsetPosition = Vector2(x * oceanScale.x, z * oceanScale.z)
                shaderInitializer.meshShader.updateUniforms()
                oceanBuffer.draw()
            }
        }
        shaderInitializer.meshShader.unbind()
    }
}