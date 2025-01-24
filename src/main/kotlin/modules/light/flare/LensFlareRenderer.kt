package modules.light.flare

import core.ecs.Behaviour
import core.management.Resources
import core.math.Quaternion
import core.math.Vector2
import core.math.Vector3
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.rendering.Query
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import graphics.rendering.viewport.ViewportInterface
import modules.light.SUN_DISTANCE
import modules.light.SunLightManager
import modules.light.defaultSunScreenPositionProvider
import org.lwjgl.opengl.GL43.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class LensFlareRenderer(
    private val flareTexturePack: MutableList<LensFlareTexture>,
    private val flareBuffer: LensFlareBuffer,
    private val spacing: Float
): Behaviour(), Renderer {

    private lateinit var material: LensFlareMaterial
    private lateinit var shader: LensFlareShader
    private lateinit var query: Query

    private val viewPort: ViewportInterface
        get() = Resources.get<ViewportInterface>()!!

    private val centerOfTheScreen = Vector2(0.5f, 0.5f)
    private val testQuadWidth = 0.07f
    private val testQuadHeight: Float
        get() = testQuadWidth * viewPort.getWidth() / viewPort.getHeight()

    private val totalSamples: Float
        get() = (testQuadWidth * viewPort.getWidth() * 0.5f).pow(2.0f) * 4f

    private var coverage: Float = 0f

    private val sunIntensity: Float
        get() = Resources.get<SunLightManager>()!!.sunIntensity()

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val sunLightManager: SunLightManager
        get() = Resources.get<SunLightManager>()!!

    private val sunWorldPosition: Vector3
        get() = sunLightManager.sunVector() * SUN_DISTANCE

    private val sunScreenPosition: Vector2?
        get() = defaultSunScreenPositionProvider()

    override fun create() {
        material = LensFlareMaterial()
        shader = LensFlareShader()
        shader bind material
        shader.setup()

        query = Query(GL_SAMPLES_PASSED)
    }

    override fun update(deltaTime: Float) {
    }

    override fun destroy() {
        query.destroy()
    }

    override fun render(pass: RenderPass) {

        if (sunScreenPosition == null) {
            return
        }

        val sunToCenter = centerOfTheScreen - sunScreenPosition!!
        val brightness = calculateFlareIntensity() * sunIntensity
        calculateFlarePositions(sunToCenter, sunScreenPosition!!)

        if (brightness > 0.0f) {
            material.transform = Quaternion(
                sunScreenPosition!!.x,
                sunScreenPosition!!.y,
                testQuadWidth,
                testQuadHeight
            )
            material.brightness = brightness

            glDisable(GL_MULTISAMPLE)

            shader.bind()
            shader.updateUniforms()
            renderWhenOcclusionTestFails()

            // enable additive blending
            glEnable(GL_BLEND)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE)

            // depth-testing = false
            glDisable(GL_DEPTH_TEST)

            if (coverage > 0) {
                for (flare in flareTexturePack) {
                    material.activeFlare = flare
                    val xScale: Float = flare.getScale()
                    val yScale: Float = xScale * viewPort.getWidth() / viewPort.getHeight()
                    val flarePosition: Vector2 = flare.getScreenPosition()
                    material.transform = Quaternion(
                        flarePosition.x,
                        flarePosition.y,
                        xScale,
                        yScale
                    )
                    shader.updateUniforms()
                    flareBuffer.draw()
                }
            }

            shader.unbind()

            // disable blending
            glDisable(GL_BLEND)

            // enable depth-testing
            glEnable(GL_DEPTH_TEST)
        }

        coverage = 0f

        material.activeFlare = null
        material.brightness = 0f
        material.transform = Quaternion(0f, 0f, 0f, 0f)
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }

    private fun renderWhenOcclusionTestFails() {
        if (query.isResultReady) {
            val visibleSamples: Int = query.result
            coverage = min((visibleSamples / totalSamples), 1.0f)
        }
        if (!query.isInUse()) {
            glColorMask(false, false, false, false)
            glDepthMask(false)
            query.start()
            glEnable(GL_DEPTH_TEST)

            flareBuffer.draw()

            query.end()
            glColorMask(true, true, true, true)
            glDepthMask(true)
        }
    }

    private fun calculateFlarePositions(sunToCenter: Vector2, sunScreenPosition: Vector2) {
        for (i in flareTexturePack.indices) {
            var direction = Vector2(sunToCenter)
            direction = direction * (i * spacing)
            val flarePosition: Vector2 = sunScreenPosition + direction
            flareTexturePack[i].setScreenPosition(flarePosition)
        }
    }

    private fun calculateFlareIntensity(): Float {
        val sunDirection = (sunWorldPosition - camera.position()).normalize()

        return max(0f, camera.forward().dot(sunDirection))
    }
}