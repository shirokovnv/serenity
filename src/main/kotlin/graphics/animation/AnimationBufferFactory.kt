package graphics.animation

object AnimationBufferFactory {
    private val buffers: MutableMap<AnimationModel, MutableMap<String, AnimationBuffer>> = mutableMapOf()
    private val referenceCounters: MutableMap<String, Int> = mutableMapOf()

    fun getOrCreateBuffer(model: AnimationModel): MutableMap<String, AnimationBuffer> {
        referenceCounters[model.name()] = (referenceCounters[model.name()] ?: 0) + 1

        return buffers.getOrPut(model) {
            val buffer = mutableMapOf<String, AnimationBuffer>()
            model.meshes().forEach { mesh ->
                buffer[mesh.name] = AnimationBuffer(mesh.vertices, mesh.indices)
            }
            buffer
        }
    }

    fun disposeBuffer(model: AnimationModel) {
        referenceCounters[model.name()] = (referenceCounters[model.name()] ?: 0) - 1
        referenceCounters[model.name()]?.coerceAtLeast(0)

        if (referenceCounters[model.name()] == 0) {
            buffers[model]?.forEach {
                it.value.destroy()
            }
            buffers.remove(model)
        }
    }
}