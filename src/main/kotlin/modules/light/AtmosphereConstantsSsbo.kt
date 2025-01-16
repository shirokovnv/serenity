package modules.light

import core.math.toFloatArray
import graphics.assets.buffer.BufferSsbo
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43.*
import java.nio.FloatBuffer

class AtmosphereConstantsSsbo(private var atm: AtmosphereConstants) : BufferSsbo() {
    private var ssboId: Int = 0

    init {
        create()
    }

    override fun getId(): Int {
        return ssboId
    }

    override fun create() {
        ssboId = glGenBuffers()
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssboId)

        val floatBuffer = createBufferFromAtmosphereConstants(atm)
        glBufferData(GL_SHADER_STORAGE_BUFFER, floatBuffer, GL_STATIC_READ)

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0)
    }

    override fun destroy() {
        if (ssboId != 0) {
            glDeleteBuffers(ssboId)
            ssboId = 0
        }
    }

    override fun bind() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, bindingPoint, ssboId)
    }

    override fun unbind() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, bindingPoint, 0)
    }

    fun updateAtmosphereConstants(atm: AtmosphereConstants) {
        destroy()
        this.atm = atm
        create()
    }

    private fun createBufferFromAtmosphereConstants(atm: AtmosphereConstants): FloatBuffer {
        val buffer = BufferUtils.createFloatBuffer(atm.memorySize())

        buffer.put(atm.vBeta1.toFloatArray())
        buffer.put(atm.vBeta2.toFloatArray())
        buffer.put(atm.vBetaD1.toFloatArray())
        buffer.put(atm.vBetaD2.toFloatArray())
        buffer.put(atm.vSumBeta1Beta2.toFloatArray())
        buffer.put(atm.vLog2eBetaSum.toFloatArray())
        buffer.put(atm.vRcpSumBeta1Beta2.toFloatArray())
        buffer.put(atm.vHG.toFloatArray())
        buffer.put(atm.vConstants.toFloatArray())
        buffer.put(atm.vTermMultipliers.toFloatArray())
        buffer.put(atm.vSoilReflectivity.toFloatArray())

        buffer.flip()
        return buffer
    }
}