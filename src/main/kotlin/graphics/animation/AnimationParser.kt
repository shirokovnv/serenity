package graphics.animation

import core.math.Matrix4
import core.math.Quaternion
import core.math.Vector3
import org.lwjgl.BufferUtils
import org.lwjgl.assimp.*
import java.nio.ByteBuffer
import java.nio.IntBuffer
import kotlin.math.min

private const val vertexSize = AnimationBuffer.VERTEX_SIZE
private const val vertexSizeWoBones = AnimationBuffer.VERTEX_SIZE_WO_BONES
private const val maxNumMeshes = 10
private const val maxNumAnimations = 10
private const val maxNumBones = 200

class AnimationParser {
    fun parse(
        buffer: ByteBuffer,
        numMeshes: Int = maxNumMeshes,
        numAnimations: Int = maxNumAnimations
    ): AnimationModel {
        val scene = Assimp.aiImportFileFromMemory(
            buffer,
            Assimp.aiProcess_Triangulate
                    or Assimp.aiProcess_FlipUVs
                    or Assimp.aiProcess_GenSmoothNormals
                    or Assimp.aiProcess_JoinIdenticalVertices
                    or Assimp.aiProcess_CalcTangentSpace
                    or Assimp.aiProcess_LimitBoneWeights,
            null as ByteBuffer?
        ) ?: throw RuntimeException("Error parsing model: ${Assimp.aiGetErrorString()}")

        val animations = parseAnimations(scene, numAnimations)
        val meshes = parseMeshes(scene, numMeshes)

        val aiRoot = scene.mRootNode()!!
        val root = Node(aiRoot.mName().dataString(), convertAiMatrixToMatrix4(aiRoot.mTransformation()))
        parseNodes(aiRoot, root)

        val globalInverseTransform = convertAiMatrixToMatrix4(aiRoot.mTransformation()).invert()

        Assimp.aiReleaseImport(scene)

        return AnimationModel(
            root,
            globalInverseTransform,
            animations,
            meshes
        )
    }

    private fun parseNodes(aiNode: AINode, node: Node) {
        for (i in 0..<aiNode.mNumChildren()) {
            val aiChildPtr = aiNode.mChildren()!!.get(i)
            val aiChild = AINode.create(aiChildPtr)
            val child = Node(
                aiChild.mName().dataString(),
                convertAiMatrixToMatrix4(aiChild.mTransformation())
            )

            node.children.add(child)
            parseNodes(aiChild, child)
        }
    }

    private fun parseBones(mesh: AIMesh, vertexArray: FloatArray): Array<Bone> {
        val boneMap = HashMap<String, Int>()
        val boneIndexMap0 = HashMap<Int, Int>()
        val boneIndexMap1 = HashMap<Int, Int>()

        val numBones = mesh.mNumBones()
        if (numBones > maxNumBones) {
            throw IllegalStateException("Maximum number of bones is $maxNumBones")
        }

        println("NUM BONES: $numBones")

        for (b in 0..<numBones) {
            val bonePtr = mesh.mBones()!![b]
            val bone = AIBone.create(bonePtr)
            boneMap[bone.mName().dataString()] = b

            for (w in 0..<bone.mNumWeights()) {
                val weight = bone.mWeights()[w]
                val vertexIndex = weight.mVertexId()
                val findIndex = vertexIndex * vertexSize

                if (!boneIndexMap0.containsKey(vertexIndex)) {
                    vertexArray[findIndex + vertexSizeWoBones + 0] = b.toFloat()
                    vertexArray[findIndex + vertexSizeWoBones + 2] = weight.mWeight()
                    boneIndexMap0[vertexIndex] = 0
                } else if (boneIndexMap0[vertexIndex] == 0) {
                    vertexArray[findIndex + vertexSizeWoBones + 1] = b.toFloat()
                    vertexArray[findIndex + vertexSizeWoBones + 3] = weight.mWeight()
                    boneIndexMap0[vertexIndex] = 1
                } else if (!boneIndexMap1.containsKey(vertexIndex)) {
                    vertexArray[findIndex + vertexSizeWoBones + 4] = b.toFloat()
                    vertexArray[findIndex + vertexSizeWoBones + 6] = weight.mWeight()
                    boneIndexMap1[vertexIndex] = 0
                } else if (boneIndexMap1[vertexIndex] == 0) {
                    vertexArray[findIndex + vertexSizeWoBones + 5] = b.toFloat()
                    vertexArray[findIndex + vertexSizeWoBones + 7] = weight.mWeight()
                    boneIndexMap1[vertexIndex] = 1
                } else {
                    throw IllegalStateException("Max 4 bones per vertex")
                }
            }
        }

        val bones = Array(mesh.mNumBones()) { Bone() }

        for (b in 0..<mesh.mNumBones()) {
            val bonePtr = mesh.mBones()?.get(b)!!
            val bone = AIBone.create(bonePtr)

            bones[b].name = bone.mName().dataString()
            bones[b].offset = convertAiMatrixToMatrix4(bone.mOffsetMatrix())
        }
        return bones
    }

    private fun parseMeshes(scene: AIScene, limit: Int): MutableList<Mesh> {
        val numMeshes = min(scene.mNumMeshes(), limit)
        val meshes = mutableListOf<Mesh>()

        println("NUM MESHES: $numMeshes")

        for (i in 0..<numMeshes) {
            val meshPtr = scene.mMeshes()!!.get(i)
            val mesh = AIMesh.create(meshPtr)

            val meshName = "${mesh.mName().dataString()}/$i"

            println("Mesh name: $meshName")

            val vertexArray = FloatArray(mesh.mNumVertices() * vertexSize)
            var index = 0

            for (v in 0..<mesh.mNumVertices()) {
                val position = mesh.mVertices()[v]
                val normal = mesh.mNormals()!![v]
                val tangent = mesh.mTangents()!![v]
                val texCoord = mesh.mTextureCoords(0)!![v]

                vertexArray[index++] = position.x()
                vertexArray[index++] = position.y()
                vertexArray[index++] = position.z()

                vertexArray[index++] = texCoord.x()
                vertexArray[index++] = texCoord.y()

                vertexArray[index++] = normal.x()
                vertexArray[index++] = normal.y()
                vertexArray[index++] = normal.z()

                vertexArray[index++] = tangent.x()
                vertexArray[index++] = tangent.y()
                vertexArray[index++] = tangent.z()

                vertexArray[index++] = 0f
                vertexArray[index++] = 0f
                vertexArray[index++] = 0f
                vertexArray[index++] = 0f

                vertexArray[index++] = 0f
                vertexArray[index++] = 0f
                vertexArray[index++] = 0f
                vertexArray[index++] = 0f
            }

            val indices: IntBuffer = BufferUtils.createIntBuffer(mesh.mNumFaces() * mesh.mFaces()[0].mNumIndices())

            for (f in 0..<mesh.mNumFaces()) {
                val face = mesh.mFaces()[f]
                for (ind in 0..<face.mNumIndices()) {
                    indices.put(face.mIndices()[ind])
                }
            }
            indices.flip()

            val bones = parseBones(mesh, vertexArray)

            val vertices = BufferUtils.createFloatBuffer(vertexArray.size)
            for (v in vertexArray.indices) {
                vertices.put(vertexArray[v])
            }
            vertices.flip()

            meshes.add(Mesh(meshName, vertices, indices, bones))
        }

        return meshes
    }

    private fun parseAnimations(scene: AIScene, limit: Int): MutableList<Animation> {
        val numAnimations = min(scene.mNumAnimations(), limit)
        val animations = mutableListOf<Animation>()

        println("NUM ANIMATIONS: $numAnimations")

        for (i in 0..<numAnimations) {
            val animationPointer = scene.mAnimations()!!.get(i)
            val aiAnim = AIAnimation.create(animationPointer)
            val animationName = "${aiAnim.mName().dataString()}/$i"
            val duration = aiAnim.mDuration()
            val ticksPerSecond = aiAnim.mTicksPerSecond()

            println("Animation name: $animationName")
            println("Duration: $duration")
            println("Ticks per second: $ticksPerSecond")

            val channels = mutableListOf<AnimationNode>()
            for (j in 0..<aiAnim.mNumChannels()) {
                val channelPointer = aiAnim.mChannels()!!.get(j)
                val channel = AINodeAnim.create(channelPointer)
                val nodeName = channel.mNodeName().dataString()
                println("Node name: $nodeName")
                val positionKeys = mutableListOf<AnimationKey<Vector3>>()
                for (k in 0..<channel.mNumPositionKeys()) {
                    val key = channel.mPositionKeys()!!.get(k)
                    val position = Vector3(key.mValue().x(), key.mValue().y(), key.mValue().z())
                    positionKeys.add(AnimationKey(key.mTime(), position))
                }

                val rotationKeys = mutableListOf<AnimationKey<Quaternion>>()
                for (k in 0..<channel.mNumRotationKeys()) {
                    val key = channel.mRotationKeys()!!.get(k)
                    val rotation = Quaternion(key.mValue().x(), key.mValue().y(), key.mValue().z(), key.mValue().w())
                    rotationKeys.add(AnimationKey(key.mTime(), rotation))
                }

                val scalingKeys = mutableListOf<AnimationKey<Vector3>>()
                for (k in 0..<channel.mNumScalingKeys()) {
                    val key = channel.mScalingKeys()!!.get(k)
                    val scale = Vector3(key.mValue().x(), key.mValue().y(), key.mValue().z())
                    scalingKeys.add(AnimationKey(key.mTime(), scale))
                }

                channels.add(AnimationNode(nodeName, positionKeys, rotationKeys, scalingKeys))
            }
            animations.add(Animation(animationName, duration, ticksPerSecond, channels))
        }

        return animations
    }

    private fun convertAiMatrixToMatrix4(matrix: AIMatrix4x4): Matrix4 {
        val m = Matrix4().identity()
        m[0, 0] = matrix.a1()
        m[0, 1] = matrix.a2()
        m[0, 2] = matrix.a3()
        m[0, 3] = matrix.a4()
        m[1, 0] = matrix.b1()
        m[1, 1] = matrix.b2()
        m[1, 2] = matrix.b3()
        m[1, 3] = matrix.b4()
        m[2, 0] = matrix.c1()
        m[2, 1] = matrix.c2()
        m[2, 2] = matrix.c3()
        m[2, 3] = matrix.c4()
        m[3, 0] = matrix.d1()
        m[3, 1] = matrix.d2()
        m[3, 2] = matrix.d3()
        m[3, 3] = matrix.d4()
        return m
    }
}