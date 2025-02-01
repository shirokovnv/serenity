package graphics.animation

import core.math.Matrix4
import graphics.animation.Bone
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(
    val name: String,
    val vertices: FloatBuffer,
    val indices: IntBuffer,
    val bones: Array<Bone>
) {
    var boneTransforms: Array<Matrix4> = Array(bones.size) { Matrix4().identity() }

    fun findBone(name: String): Bone? {
        for (bone in bones) {
            if (bone.name == name) {
                return bone
            }
        }
        return null
    }
}