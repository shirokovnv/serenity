package graphics.animation

import core.management.Disposable
import core.math.Matrix4
import core.math.Quaternion
import core.math.Vector3
import core.math.helpers.lerp
import core.math.helpers.slerp
import graphics.assets.texture.Texture2d
import graphics.rendering.Drawable

class AnimationModel(
    private val root: Node,
    private val globalInverseTransform: Matrix4,
    private val animations: List<Animation>,
    private val meshes: List<Mesh>
) : Drawable, Disposable {
    private var animationTime: Float = 0f
    private var currentAnimation: Animation? = null
    private var currentMesh: Mesh? = null

    private var diffuseTexture: Texture2d? = null
    private var buffers = mutableMapOf<String, AnimationBuffer>()

    companion object {
        private val identity = Matrix4().identity()
    }

    init {
        meshes.forEach { mesh ->
            buffers[mesh.name] = AnimationBuffer(mesh.vertices, mesh.indices)
        }
    }

    override fun dispose() {
        buffers.values.forEach { it.destroy() }
    }

    override fun draw() {
        buffers.values.forEach { it.draw() }
    }

    fun drawCurrent() {
        buffers[currentMesh?.name]?.draw()
    }

    fun diffuseTexture(): Texture2d? = diffuseTexture

    fun setDiffuseTexture(texture2d: Texture2d?) {
        diffuseTexture = texture2d
    }

    fun currentMesh(): Mesh? {
        return currentMesh
    }

    fun currentAnimation(): Animation? {
        return currentAnimation
    }

    fun setCurrentAnimationByName(name: String) {
        currentAnimation = animations.firstOrNull { it.name == name }
        animationTime = 0f
    }

    fun setCurrentMeshByName(name: String) {
        currentMesh = meshes.firstOrNull { it.name == name }
    }

    fun getAnimationByName(name: String): Animation? {
        return animations.firstOrNull { it.name == name }
    }

    fun getMeshByName(name: String): Mesh? {
        return meshes.firstOrNull { it.name == name }
    }

    fun update(deltaTime: Float) {
        if (currentAnimation == null || currentMesh == null) {
            return
        }

        animationTime += deltaTime

        if (animationTime > currentAnimation!!.duration.toFloat()) {
            animationTime %= currentAnimation!!.duration.toFloat()
            // animationTime = 0f
        }

        boneTransforms(currentAnimation!!, currentMesh!!, animationTime)
    }

    private fun boneTransforms(animation: Animation, mesh: Mesh, timeInSeconds: Float) {
        val ticksPerSecond = if (animation.ticksPerSecond != 0.0) {
            animation.ticksPerSecond.toFloat()
        } else {
            25.0f
        }

        val timeInTicks = timeInSeconds * ticksPerSecond
        val animationTime = timeInTicks % animation.duration.toFloat()

        readNodeHierarchy(animationTime, root, animation, mesh, identity)

        for (i in mesh.bones.indices) {
            mesh.boneTransforms[i] = mesh.bones[i].transform
        }
    }

    private fun readNodeHierarchy(
        time: Float,
        node: Node,
        animation: Animation,
        mesh: Mesh,
        parentTransform: Matrix4
    ) {
        var nodeTransform = node.transform
        val nodeAnim = findNodeAnim(animation, node.name)

        if (nodeAnim != null) {
            val scaling = interpolateScaling(nodeAnim, time)
            val rotation = interpolateRotation(nodeAnim, time)
            val position = interpolatePosition(nodeAnim, time)

            nodeTransform = calculateTransform(rotation, scaling, position)
        }

        val globalTransform = parentTransform * nodeTransform

        val bone: Bone? = mesh.findBone(node.name)
        if (bone != null) {
            bone.transform = globalInverseTransform * globalTransform * bone.offset
        }

        for (i in 0..<node.children.size) {
            readNodeHierarchy(time, node.children[i], animation, mesh, globalTransform)
        }
    }

    private fun findScaling(animationTime: Float, animNode: AnimationNode): Int {
        for (i in 0..<animNode.scalingKeys.size - 1) {
            if (animationTime < animNode.scalingKeys[i + 1].time) {
                return i
            }
        }
        return 0
    }

    private fun findPosition(animationTime: Float, animNode: AnimationNode): Int {
        for (i in 0..<animNode.positionKeys.size - 1) {
            if (animationTime < animNode.positionKeys[i + 1].time) {
                return i
            }
        }
        return 0
    }

    private fun findRotation(animationTime: Float, animNode: AnimationNode): Int {
        for (i in 0..<animNode.rotationKeys.size - 1) {
            if (animationTime < animNode.rotationKeys[i + 1].time) {
                return i
            }
        }
        return 0
    }

    private fun findNodeAnim(animation: Animation, nodeName: String): AnimationNode? {
        for (i in 0..<animation.channels.size) {
            if (animation.channels[i].name == nodeName) {
                return animation.channels[i]
            }
        }
        return null
    }

    private fun interpolatePosition(animNode: AnimationNode, animationTime: Float): Vector3 {
        if (animNode.positionKeys.size == 1) {
            return animNode.positionKeys[0].value
        }

        val positionIndex = findPosition(animationTime, animNode)
        val nextPositionIndex = positionIndex + 1

        val deltaTime =
            (animNode.positionKeys[nextPositionIndex].time - animNode.positionKeys[positionIndex].time).toFloat()

        val factor =
            (animationTime - animNode.positionKeys[positionIndex].time.toFloat()) / deltaTime

        val start = animNode.positionKeys[positionIndex].value
        val end = animNode.positionKeys[nextPositionIndex].value

        return lerp(start, end, factor)
    }

    private fun interpolateRotation(animNode: AnimationNode, animationTime: Float): Quaternion {
        if (animNode.rotationKeys.size == 1) {
            return animNode.rotationKeys[0].value
        }

        val rotationIndex = findRotation(animationTime, animNode)
        val nextRotationIndex = rotationIndex + 1

        val deltaTime =
            (animNode.rotationKeys[nextRotationIndex].time - animNode.rotationKeys[rotationIndex].time).toFloat()

        val factor =
            (animationTime - animNode.rotationKeys[rotationIndex].time.toFloat()) / deltaTime

        val startRotationQ = animNode.rotationKeys[rotationIndex].value
        val endRotationQ = animNode.rotationKeys[nextRotationIndex].value

        return slerp(startRotationQ, endRotationQ, factor, true)
    }

    private fun interpolateScaling(animNode: AnimationNode, animationTime: Float): Vector3 {
        if (animNode.scalingKeys.size == 1) {
            return animNode.scalingKeys[0].value
        }

        val scalingIndex = findScaling(animationTime, animNode)
        val nextScalingIndex = scalingIndex + 1

        val deltaTime =
            (animNode.scalingKeys[nextScalingIndex].time - animNode.scalingKeys[scalingIndex].time).toFloat()

        val factor =
            (animationTime - animNode.scalingKeys[scalingIndex].time.toFloat()) / deltaTime

        val start = animNode.scalingKeys[scalingIndex].value
        val end = animNode.scalingKeys[nextScalingIndex].value

        return lerp(start, end, factor)
    }

    private fun calculateTransform(rotation: Quaternion, scale: Vector3, position: Vector3): Matrix4 {
        val matrix = Matrix4().identity()

        val x = rotation.x
        val y = rotation.y
        val z = rotation.z
        val w = rotation.w

        val x2 = x * x
        val y2 = y * y
        val z2 = z * z
        val xy = x * y
        val xz = x * z
        val xw = x * w
        val yz = y * z
        val yw = y * w
        val zw = z * w

        matrix[0, 0] = (1 - 2 * (y2 + z2)) * scale.x
        matrix[0, 1] = 2 * (xy - zw)
        matrix[0, 2] = 2 * (xz + yw)
        matrix[1, 0] = 2 * (xy + zw)
        matrix[1, 1] = (1 - 2 * (x2 + z2)) * scale.y
        matrix[1, 2] = 2 * (yz - xw)
        matrix[2, 0] = 2 * (xz - yw)
        matrix[2, 1] = 2 * (yz + xw)
        matrix[2, 2] = (1 - 2 * (x2 + y2)) * scale.z

        matrix[0, 3] = position.x
        matrix[1, 3] = position.y
        matrix[2, 3] = position.z

        return matrix
    }
}