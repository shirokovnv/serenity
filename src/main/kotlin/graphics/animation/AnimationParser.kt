package graphics.animation

import core.math.Matrix4
import core.math.Quaternion
import core.math.Vector3
import graphics.rendering.Color
import org.lwjgl.BufferUtils
import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*
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
        val scene = aiImportFileFromMemory(
            buffer,
            aiProcess_Triangulate
                    or aiProcess_FlipUVs
                    or aiProcess_GenSmoothNormals
                    or aiProcess_JoinIdenticalVertices
                    or aiProcess_CalcTangentSpace
                    or aiProcess_LimitBoneWeights,
            null as ByteBuffer?
        ) ?: throw RuntimeException("Error parsing model: ${aiGetErrorString()}")

        val animations = parseAnimations(scene, numAnimations)
        val meshes = parseMeshes(scene, numMeshes)
        val materials = parseMaterials(scene, numMeshes)

        val aiRoot = scene.mRootNode()!!
        val root = Node(aiRoot.mName().dataString(), convertAiMatrixToMatrix4(aiRoot.mTransformation()))
        parseNodes(aiRoot, root)

        val globalInverseTransform = convertAiMatrixToMatrix4(aiRoot.mTransformation()).invert()

        aiReleaseImport(scene)

        return AnimationModel(
            root,
            globalInverseTransform,
            animations,
            meshes,
            materials
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
        val boneIndexMap0 = HashMap<Int, Int>()
        val boneIndexMap1 = HashMap<Int, Int>()

        val numBones = mesh.mNumBones()
        if (numBones > maxNumBones) {
            throw IllegalStateException("Maximum number of bones is $maxNumBones")
        }

        println("NUM BONES: $numBones")

        val bones = Array(numBones) { Bone() }
        for (b in 0..<numBones) {
            val bonePtr = mesh.mBones()!![b]
            val bone = AIBone.create(bonePtr)

            bones[b].name = bone.mName().dataString()
            bones[b].offset = convertAiMatrixToMatrix4(bone.mOffsetMatrix())

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

        return bones
    }

    private fun parseMaterials(scene: AIScene, limit: Int): MutableList<MtlData> {
        val numMaterials = min(scene.mNumMaterials(), limit)
        val materials = mutableListOf<MtlData>()

        println("NUM MATERIALS: $numMaterials")

        for (m in 0..<numMaterials) {
            val mtlPtr = scene.mMaterials()!!.get(m)
            val mtl = AIMaterial.create(mtlPtr)

            val diffuseColor = getMaterialColorProperty(mtl, MtlColorKey.DIFFUSE)
            val specularColor = getMaterialColorProperty(mtl, MtlColorKey.SPECULAR)
            val emissiveColor = getMaterialColorProperty(mtl, MtlColorKey.EMISSIVE)
            val ambientColor = getMaterialColorProperty(mtl, MtlColorKey.AMBIENT)

            val opacity = getMaterialFloatProperty(mtl, MtlKey.OPACITY)
            val shininess = getMaterialFloatProperty(mtl, MtlKey.SHININESS)
            val shininessStrength = getMaterialFloatProperty(mtl, MtlKey.SHININESS_STRENGTH)

            val mtlData = MtlData(
                m,
                diffuseColor,
                ambientColor,
                specularColor,
                emissiveColor,
                shininess,
                shininessStrength,
                opacity
            )

            materials.add(mtlData)
        }

        return materials
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
            val defaultVector3d = AIVector3D.create()

            for (v in 0..<mesh.mNumVertices()) {
                val position = mesh.mVertices()[v]
                val normal = mesh.mNormals()?.get(v) ?: defaultVector3d
                val tangent = mesh.mTangents()?.get(v) ?: defaultVector3d
                val texCoord = mesh.mTextureCoords(0)?.get(v) ?: defaultVector3d

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

            val mtlIndex = mesh.mMaterialIndex()

            meshes.add(Mesh(meshName, vertices, indices, bones, mtlIndex))
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

    private fun getMaterialColorProperty(material: AIMaterial, key: MtlColorKey): Color {
        val aiColor4D = AIColor4D.create()
        val result = aiGetMaterialColor(material, key.value, 0, 0, aiColor4D)

        return if (result == aiReturn_SUCCESS) Color(aiColor4D.r(), aiColor4D.g(), aiColor4D.b(), aiColor4D.a())
        else Color(1f, 1f, 1f, 1f)
    }

    private fun getMaterialFloatProperty(material: AIMaterial, key: MtlKey): Float {
        val floatArray = FloatArray(1) { 0.0f }
        val intArray = IntArray(1) { 1 }
        val result = aiGetMaterialFloatArray(
            material,
            key.value,
            0,
            0,
            floatArray,
            intArray
        )

        return if (result == aiReturn_SUCCESS) floatArray[0] else 0.0f
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