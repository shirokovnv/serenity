package modules.fauna

import core.ecs.Behaviour
import core.management.Resources
import core.math.Matrix4
import core.scene.Object
import core.scene.camera.Camera
import graphics.animation.AnimationModel
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import platform.services.filesystem.AssimpLoader
import platform.services.filesystem.ImageLoader

class ButterflyBehaviour : Behaviour(), Renderer {
    private lateinit var animationModel: AnimationModel
    private lateinit var material: ButterflyMaterial
    private lateinit var shader: ButterflyShader
    private lateinit var diffuseTexture: Texture2d

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val model: Matrix4
        get() = (owner() as Object).worldMatrix()

    private val animationSpeed = 0.003f

    override fun create() {
        val assimpLoader = Resources.get<AssimpLoader>()!!
        animationModel = assimpLoader.load("animations/Butterfly_Fly.fbx")
        animationModel.setCurrentMeshByName("Cylinder.000/0")
        animationModel.setCurrentAnimationByName("Armature.002|Alien2 FBX.001Action.003/0")

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

    override fun update(deltaTime: Float) {
        material.model = model
        material.view = camera.view
        material.projection = camera.projection

        animationModel.update(deltaTime + animationSpeed)

        material.boneTransforms.clear()
        material.boneTransforms = animationModel.currentMesh()!!.boneTransforms.toMutableList()
    }

    override fun destroy() {
        diffuseTexture.destroy()
        animationModel.dispose()
    }

    override fun render(pass: RenderPass) {
        shader.bind()
        shader.updateUniforms()
        animationModel.draw()
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}