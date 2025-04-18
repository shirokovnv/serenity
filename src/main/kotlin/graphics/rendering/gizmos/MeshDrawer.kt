package graphics.rendering.gizmos

import core.ecs.BaseComponent
import core.management.Disposable
import core.math.Vector3
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.rendering.Color
import graphics.rendering.ColorGenerator
import graphics.rendering.Drawable

class MeshDrawer(
    private val camera: Camera,
    private val vertexProvider: () -> List<Vector3>,
    private var color: Color? = null,
    private val capacity: Int = 3000
) : BaseComponent(), Drawable, Disposable {
    private lateinit var buffer: MeshBatchBuffer
    private lateinit var material: MeshMaterial
    private lateinit var shader: MeshShader

    init {
        require(capacity % 3 == 0)

        buffer = MeshBatchBuffer(capacity)
        material = MeshMaterial()
        shader = MeshShader()
        shader bind material
        shader.setup()

        material.color = color?.toVector3() ?: ColorGenerator.fromUUID(owner()!!.id).toVector3()
    }

    override fun draw() {
        material.viewProjection = camera.viewProjection

        shader.bind()
        shader.updateUniforms()
        vertexProvider().chunked(capacity).forEach { chunk ->
            buffer.uploadData(chunk)
            buffer.draw()
        }
        shader.unbind()
    }

    override fun dispose() {
        buffer.destroy()
        shader.destroy()
    }
}