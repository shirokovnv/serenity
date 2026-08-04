package modules.terrain.roam

import core.ecs.Behaviour
import core.management.Resources
import core.math.*
import core.scene.Object
import core.scene.camera.Camera
import core.scene.camera.Frustum
import core.scene.camera.PerspectiveCamera
import modules.terrain.roam.tri.refinement.ErrorDensityParams
import modules.terrain.roam.tri.refinement.RefinementFactory
import modules.terrain.roam.tri.refinement.RefinementType

class RoamTerrainBehaviour(private val config: RoamTerrainConfig) : Behaviour() {

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private lateinit var frustum: Frustum

    override fun create() {
        frustum = Frustum(camera as PerspectiveCamera)

        val patchConfig = RoamTerrainPatchConfig()
        patchConfig.worldScale = Vector3(config.worldScale)
        patchConfig.worldOffset = Vector3(config.worldOffset)
        patchConfig.refinement = RefinementFactory.create(RefinementType.DENSITY, ErrorDensityParams(), camera)

        val sceneObject = RoamTerrainPatchSo(patchConfig)

        (owner() as Object).addChild(sceneObject)

        println("TERRAIN ROAM INITIALIZED")
    }

    override fun update(deltaTime: Float) {
    }

    override fun destroy() {
    }
}