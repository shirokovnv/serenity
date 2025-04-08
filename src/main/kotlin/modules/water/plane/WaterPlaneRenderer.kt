package modules.water.plane

import core.ecs.Behaviour
import core.management.Resources
import core.math.Matrix4
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.PerspectiveCamera
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.ReflectionPass
import graphics.rendering.passes.RefractionPass
import graphics.rendering.passes.RenderPass
import modules.light.SUN_DISTANCE
import modules.light.SunLightManager
import org.lwjgl.opengl.GL43.*
import platform.services.filesystem.ImageLoader

class WaterPlaneRenderer(private val params: WaterPlaneParams) : Behaviour(), Renderer {

    private lateinit var buffer: WaterPlaneBuffer
    private lateinit var material: WaterPlaneMaterial
    private lateinit var shader: WaterPlaneShader

    private lateinit var dudvMap: Texture2d
    private lateinit var normalMap: Texture2d

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val sunLightManager: SunLightManager
        get() = Resources.get<SunLightManager>()!!

    private val localMatrix: Matrix4
        get() = Matrix4().identity()

    private val worldMatrix: Matrix4
        get() = owner()!!.getComponent<Transform>()!!.matrix()

    private val viewProjection: Matrix4
        get() = camera.viewProjection

    override fun create() {
        buffer = WaterPlaneBuffer()
        material = WaterPlaneMaterial()
        shader = WaterPlaneShader()
        shader bind material
        shader.setup()

        val imageLoader = Resources.get<ImageLoader>()!!
        dudvMap = Texture2d(imageLoader.loadImage("textures/water/plane/dudv.png"))
        dudvMap.bind()
        dudvMap.bilinearFilter()
        dudvMap.unbind()

        normalMap = Texture2d(imageLoader.loadImage("textures/water/plane/normals.png"))
        normalMap.bind()
        normalMap.bilinearFilter()
        normalMap.unbind()

        material.worldHeight = params.worldHeight
        material.dudvMap = dudvMap
        material.normalMap = normalMap
    }

    override fun update(deltaTime: Float) {
        material.cameraPosition = camera.position()
        material.lightPosition = sunLightManager.sunVector() * SUN_DISTANCE
        material.lightColor = sunLightManager.sunColor()
        material.near = (camera as PerspectiveCamera).zNear
        material.far = (camera as PerspectiveCamera).zFar

        material.moveFactor += params.waveSpeed
        material.moveFactor %= 1
    }

    override fun destroy() {
        shader.destroy()
        buffer.destroy()
        dudvMap.destroy()
        normalMap.destroy()
    }

    override fun render(pass: RenderPass) {
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_DEPTH_TEST)

        glDisable(GL_MULTISAMPLE)

        material.reflectionMap = ReflectionPass.fbo().getColorTexture()
        material.refractionMap = RefractionPass.fbo().getColorTexture()
        material.depthMap = RefractionPass.fbo().getDepthTexture()
        material.localMatrix = localMatrix
        material.worldMatrix = worldMatrix
        material.viewProjection = viewProjection

        shader.bind()
        shader.updateUniforms()
        buffer.draw()
        shader.unbind()

        glDisable(GL_BLEND)
        glEnable(GL_MULTISAMPLE)
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}