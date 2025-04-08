package modules.water.plane

import core.ecs.Behaviour
import core.management.Resources
import core.math.Matrix4
import core.scene.Transform
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass

class WaterPlaneRenderer(private val worldHeight: Float = 0.0f) : Behaviour(), Renderer {

    private lateinit var buffer: WaterPlaneBuffer
    private lateinit var material: WaterPlaneMaterial
    private lateinit var shader: WaterPlaneShader

    private val camera: Camera
        get() = Resources.get<Camera>()!!

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

        material.worldHeight = worldHeight
    }

    override fun update(deltaTime: Float) {

    }

    override fun destroy() {
        shader.destroy()
        buffer.destroy()
    }

    override fun render(pass: RenderPass) {
        material.localMatrix = localMatrix
        material.worldMatrix = worldMatrix
        material.viewProjection = viewProjection

        shader.bind()
        shader.updateUniforms()
        buffer.draw()
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}