package modules.terrain.roam.gizmos

import core.ecs.BaseComponent
import core.management.Disposable
import core.management.Resources
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.rendering.Colors
import graphics.rendering.Drawable
import graphics.rendering.gizmos.TriangleBuffer
import graphics.rendering.gizmos.TriangleDrawer
import graphics.rendering.gizmos.TriangleMaterial
import graphics.rendering.gizmos.TriangleShader
import modules.terrain.roam.RoamTerrainPatch

class RoamPatchRootDrawer(private val patch: RoamTerrainPatch) : BaseComponent(), Drawable, Disposable {

    private val bufferA = TriangleBuffer(patch.baseTriangles().first.geometry.worldVertices)
    private val bufferB = TriangleBuffer(patch.baseTriangles().second.geometry.worldVertices)

    private val materialA = TriangleMaterial()
    private val materialB = TriangleMaterial()

    private val shaderA = TriangleShader()
    private val shaderB = TriangleShader()

    private val drawerA = TriangleDrawer(bufferA, shaderA, materialA)
    private val drawerB = TriangleDrawer(bufferB, shaderB, materialB)

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    init {
        shaderA bind materialA
        shaderA.setup()

        shaderB bind materialB
        shaderB.setup()

        materialA.color = Colors.Green.toVector3()
        materialB.color = Colors.Yellow.toVector3()
    }

    override fun dispose() {
        bufferA.destroy()
        bufferB.destroy()
        shaderA.destroy()
        shaderB.destroy()
    }

    override fun draw() {
        materialA.viewProjection = camera.viewProjection
        materialB.viewProjection = camera.viewProjection

        drawerA.draw()
        drawerB.draw()
    }
}