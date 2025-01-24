package graphics.model

import core.management.Disposable
import core.math.Matrix4
import graphics.rendering.Drawable
import org.lwjgl.opengl.GL43.*

class Model(private val modelData: MutableMap<String, ModelData>) : Drawable, Disposable {
    private val instanceMatrices = mutableListOf<Matrix4>()
    private var buffers = mutableMapOf<String, ModelDataBuffer>()
    private var instanceBuffer: ModelInstanceBuffer? = null
    private var isReadyForRendering: Boolean = false

    fun createBuffers() {
        modelData.forEach { (materialName, geometryData) ->
            buffers[materialName] = ModelDataBuffer(geometryData)
        }

        if (isInstanced()) {
            val matrices = getInstances().map { m -> m.transpose() }.toTypedArray()
            val vaoIds = buffers.values.map { buffer -> buffer.getId() }.toTypedArray()

            instanceBuffer = ModelInstanceBuffer(matrices, vaoIds)
        }

        isReadyForRendering = true
    }

    fun destroyBuffers() {
        buffers.values.forEach { buffer -> buffer.destroy() }
        buffers.clear()

        instanceBuffer?.destroy()
        instanceBuffer = null

        isReadyForRendering = false
    }

    fun destroyTextures() {
        modelData.values.forEach{mtlData ->
            mtlData.material?.textures?.forEach { texture ->
                texture.value.texture?.destroy()
            }
        }
    }

    fun getBuffers(): MutableMap<String, ModelDataBuffer> = buffers

    fun getModelDataByMaterial(materialName: String): ModelData? {
        return modelData[materialName]
    }

    fun getModelData(): MutableMap<String, ModelData> {
        return modelData
    }

    fun getInstances(): List<Matrix4> {
        return instanceMatrices.toMutableList()
    }

    fun getMaterialNames(): List<String> {
        return modelData.keys.toMutableList()
    }

    fun getMtlDataByName(materialName: String): ModelMtlData? {
        return modelData[materialName]?.material
    }

    fun addInstances(listOfMatrices: MutableList<Matrix4>) {
        instanceMatrices.addAll(listOfMatrices)
    }

    fun addInstances(arrayOfMatrices: Array<Matrix4>) {
        instanceMatrices.addAll(arrayOfMatrices)
    }

    fun addInstance(matrix: Matrix4) {
        instanceMatrices.add(matrix)
    }

    fun isInstanced(): Boolean = instanceMatrices.isNotEmpty()

    fun countInstances(): Int = instanceMatrices.size

    fun drawByMaterial(materialName: String) {
        if (!isReadyForRendering) {
            return
        }

        val buffer = buffers[materialName]
        val count = modelData[materialName]?.indices?.size ?: 0

        buffer?.bind()
        instanceBuffer?.bindForVaoId(buffer?.getId() ?: 0)

        if (isInstanced()) {
            glDrawElementsInstanced(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0, countInstances())
        } else {
            glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0)
        }

        buffer?.unbind()
        instanceBuffer?.unbindForVaoId(buffer?.getId() ?: 0)
    }

    override fun draw() {
        modelData.keys.forEach { materialName ->
            drawByMaterial(materialName)
        }
    }

    fun setupTextureFilters() {
        modelData
            .values
            .filter { it.material?.textures != null }
            .flatMap { it.material?.textures?.values!! }
            .filter { it.texture != null }
            .forEach {
                println("${it.texture} ${it.name}")
                it.texture!!.bind()
                it.texture!!.bilinearFilter()
            }
    }

    override fun dispose() {
        destroyBuffers()
        destroyTextures()
    }
}