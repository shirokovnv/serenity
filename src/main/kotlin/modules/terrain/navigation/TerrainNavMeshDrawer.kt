package modules.terrain.navigation

import core.ecs.BaseComponent
import core.management.Disposable
import core.math.Matrix4
import graphics.assets.surface.bind
import graphics.rendering.Drawable
import org.lwjgl.opengl.GL43

class TerrainNavMeshDrawer(
    private val navMesh: TerrainNavMesh,
    private val viewProjectionProvider: () -> Matrix4
) : BaseComponent(), Drawable, Disposable {

    private var buffer = TerrainNavMeshBuffer(navMesh.getMesh())
    private var material = TerrainNavMeshMaterial()
    private var shader = TerrainNavMeshShader()

    init {
        shader bind material
        shader.setup()
    }

    override fun draw() {
        material.viewProjection = viewProjectionProvider()
        material.opacity = navMesh.opacity()

        GL43.glEnable(GL43.GL_BLEND)
        GL43.glBlendFunc(GL43.GL_SRC_ALPHA, GL43.GL_ONE_MINUS_SRC_ALPHA)

        shader.bind()
        shader.updateUniforms()
        buffer.draw()
        shader.unbind()

        GL43.glDisable(GL43.GL_BLEND)
    }

    override fun dispose() {
        shader.destroy()
        buffer.destroy()
    }
}