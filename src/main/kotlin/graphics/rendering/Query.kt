package graphics.rendering

import org.lwjgl.opengl.GL43.*

class Query(private val type: Int) {
    private var id: Int = 0
    private var inUse = false

    init {
        id = glGenQueries()
    }

    fun start() {
        glBeginQuery(type, id)
        inUse = true
    }

    fun end() {
        glEndQuery(type)
    }

    val isResultReady: Boolean
        get() = glGetQueryObjecti(id, GL_QUERY_RESULT_AVAILABLE) == GL_TRUE

    val result: Int
        get() {
            inUse = false
            return glGetQueryObjecti(id, GL_QUERY_RESULT)
        }

    fun destroy() {
        glDeleteQueries(id)
    }

    fun isInUse(): Boolean = inUse
}