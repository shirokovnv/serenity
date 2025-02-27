package modules.fauna

import core.management.Resources
import core.math.Matrix4
import core.scene.Object
import core.scene.behaviour.FrameUpdateBehaviour
import core.scene.camera.Camera
import graphics.animation.AnimationModel
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import platform.services.filesystem.AssimpLoader
import platform.services.filesystem.ImageLoader

class ButterflyBehaviour : FrameUpdateBehaviour(), Renderer {
    private lateinit var animationModel: AnimationModel
    private lateinit var material: ButterflyMaterial
    private lateinit var shader: ButterflyShader
    private lateinit var diffuseTexture: Texture2d

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val model: Matrix4
        get() = (owner() as Object).localMatrix()

    private val animationSpeed = 0.003f

    override fun create() {
        val assimpLoader = Resources.get<AssimpLoader>()!!
        animationModel = assimpLoader.load("animations/Butterfly.fbx")
        animationModel.setCurrentMeshByName("Cylinder.000/0")
        animationModel.setCurrentAnimationByName("Armature.002|ArmatureAction.002/2")

        material = ButterflyMaterial()
        shader = ButterflyShader()
        shader bind material
        shader.setup()

        val imageLoader = Resources.get<ImageLoader>()!!
        diffuseTexture = Texture2d(imageLoader.loadImage("animations/UVButterflies.png"))
        diffuseTexture.bind()
        diffuseTexture.bilinearFilter()

        material.diffuseTexture = diffuseTexture

        println("BUTTERFLY RENDER BEHAVIOUR INITIALIZED")
    }

    override fun onUpdate(deltaTime: Float) {
        material.model = model
        material.view = camera.view
        material.projection = camera.projection

        animationModel.update(animationSpeed)
    }

    override fun destroy() {
        diffuseTexture.destroy()
        animationModel.dispose()
    }

    override fun render(pass: RenderPass) {
        shader.bind()

        animationModel.meshes().forEach { mesh ->
            animationModel.setCurrentMeshByName(mesh.name)
            material.boneTransforms = mesh.boneTransforms.toMutableList()
            shader.updateUniforms()
            animationModel.drawCurrent()
        }

        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}