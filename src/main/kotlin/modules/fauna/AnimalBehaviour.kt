package modules.fauna

import core.management.Resources
import core.math.Matrix4
import core.math.Vector3
import core.math.createLookAtMatrix
import core.scene.behaviour.FrameUpdateBehaviour
import core.scene.camera.Camera
import core.scene.camera.OrthographicCamera
import core.scene.navigation.steering.SteeringAgent
import graphics.animation.AnimationModel
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import graphics.rendering.passes.ShadowPass
import modules.light.SunLightManager
import modules.terrain.heightmap.Heightmap
import platform.services.filesystem.AssimpLoader

class AnimalBehaviour(
    private val agent: SteeringAgent,
    private val heightmap: Heightmap,
    private val material: AnimalMaterial,
    private val shader: AnimalShader
) : FrameUpdateBehaviour(), Renderer {

    companion object {
        private val zeroVector = Vector3(0f)
    }

    private lateinit var animal: AnimationModel
    private var animationSpeed: Float = 0.01f

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val model: Matrix4
        get() = agent.objectRef.transform().matrix()

    private val lightView: Matrix4
        get() = Resources.get<SunLightManager>()!!.calculateLightViewMatrix()

    private val orthoProjection: Matrix4
        get() = Resources.get<OrthographicCamera>()!!.projection

    override fun create() {
        val dirName = "animations/"
        val modelFiles = listOf(
            "Alpaca.fbx",
            "Deer.fbx",
            "Donkey.fbx",
            "Fox.fbx",
            "Horse.fbx",
            "Stag.fbx",
            "Wolf.fbx",
        )

        val loader = Resources.get<AssimpLoader>()!!
        animal = loader.load("${dirName}${modelFiles.random()}")

        agent.objectRef.transform().setScale(Vector3(0.025f))

        val animation = animal.findAnimationByToken("Gallop")
        if (animation != null) {
            animal.setCurrentAnimationByName(animation.name)
        }
    }

    override fun destroy() {
        animal.dispose()
    }

    override fun onUpdate(deltaTime: Float) {
        animal.update(animationSpeed)
    }

    override fun render(pass: RenderPass) {
        material.isShadowPass = (pass == ShadowPass)

        when (pass) {
            ShadowPass -> {
                material.view = lightView
                material.projection = orthoProjection
            }

            NormalPass -> {
                material.view = camera.view
                material.projection = camera.projection
            }
        }

        val up = heightmap.getInterpolatedNormal(agent.position.x, agent.position.z)
        material.model = model * createLookAtMatrix(zeroVector, agent.velocity, up)

        shader.bind()
        animal.meshes().forEach { mesh ->
            animal.setCurrentMeshByName(mesh.name)
            material.mtlData = animal.getMaterialByIndex(mesh.mtlIndex)
            material.boneTransforms = mesh.boneTransforms.toMutableList()
            shader.updateUniforms()
            animal.drawCurrent()
        }
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass || pass == ShadowPass
    }
}