package graphics.assets.buffer

import graphics.assets.Asset
import org.lwjgl.opengl.GL43.*
import java.nio.Buffer
import java.nio.DoubleBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Vbo<BufferData : Buffer>(
    private val bufferType: Int = GL_ARRAY_BUFFER,
    private val usage: Int = GL_STATIC_DRAW
) : Asset {
    private var vboId: Int = 0

    init {
        require(
            bufferType in listOf(
                GL_ARRAY_BUFFER,
                GL_ELEMENT_ARRAY_BUFFER,
                GL_UNIFORM_BUFFER,
                GL_COPY_READ_BUFFER,
                GL_COPY_WRITE_BUFFER
            )
        ) {
            "Invalid buffer type: $bufferType. Must be one of GL_ARRAY_BUFFER, GL_ELEMENT_ARRAY_BUFFER, GL_UNIFORM_BUFFER, GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER."
        }
        require(
            usage in listOf(
                GL_STATIC_DRAW,
                GL_DYNAMIC_DRAW,
                GL_STREAM_DRAW,
                GL_STATIC_READ,
                GL_DYNAMIC_READ,
                GL_STREAM_READ,
                GL_STATIC_COPY,
                GL_DYNAMIC_COPY,
                GL_STREAM_COPY
            )
        ) {
            "Invalid usage type: $usage. Must be one of GL_STATIC_DRAW, GL_DYNAMIC_DRAW, GL_STREAM_DRAW, GL_STATIC_READ, GL_DYNAMIC_READ, GL_STREAM_READ, GL_STATIC_COPY, GL_DYNAMIC_COPY, GL_STREAM_COPY."
        }
        create()
    }

    override fun getId(): Int {
        return vboId
    }

    override fun create() {
        vboId = glGenBuffers()
    }

    override fun destroy() {
        if (vboId != 0) {
            glDeleteBuffers(vboId)
            vboId = 0
        }
    }

    override fun bind() {
        glBindBuffer(bufferType, vboId)
    }

    override fun unbind() {
        glBindBuffer(bufferType, 0)
    }

    fun uploadData(bufferData: BufferData) {
        when (bufferData) {
            is FloatBuffer -> glBufferData(bufferType, bufferData, usage)
            is IntBuffer -> glBufferData(bufferType, bufferData, usage)
            is DoubleBuffer -> glBufferData(bufferType, bufferData, usage)
            else -> throw IllegalArgumentException("Unsupported buffer type: ${bufferData::class}")
        }
    }
}