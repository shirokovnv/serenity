import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.ARBVertexArrayObject.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Main {
    private var window: Long = 0
    private var shaderProgram: Int = 0
    private var VAO: Int = 0

    fun run() {
        init()
        loop()
        cleanup()
    }

    private fun init() {
        GLFWErrorCallback.createPrint(System.err).set()
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        val width = 800
        val height = 600
        window = glfwCreateWindow(width, height, "Kotlin LWJGL", NULL, NULL)
        if (window == NULL) {
            throw IllegalStateException("Failed to create the GLFW window")
        }

        glfwMakeContextCurrent(window)
        glfwSwapInterval(1)
        glfwShowWindow(window)
        GL.createCapabilities()

        // Shaders
        val vertexShaderSource = """
    #version 330 core
    layout (location = 0) in vec3 aPos;
    void main() {
        gl_Position = vec4(aPos, 1.0);
    }
    """

        val fragmentShaderSource = """
    #version 330 core
    out vec4 FragColor;
    void main() {
        FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
    }
    """
        shaderProgram = createShaderProgram(vertexShaderSource, fragmentShaderSource)

        // VBO, VAO
        val vertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.0f, 0.5f, 0.0f
        )
        val indices = intArrayOf(0, 1, 2)

        val vao = glGenVertexArrays()
        glBindVertexArray(vao)

        val vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        val verticesBuffer: FloatBuffer = MemoryUtil.memAllocFloat(vertices.size)
        verticesBuffer.put(vertices)
        verticesBuffer.flip()

        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW)
        MemoryUtil.memFree(verticesBuffer)


        val ebo = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        val indicesBuffer: IntBuffer = MemoryUtil.memAllocInt(indices.size)
        indicesBuffer.put(indices)
        indicesBuffer.flip()

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)
        MemoryUtil.memFree(indicesBuffer)

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.SIZE_BYTES, 0)
        glEnableVertexAttribArray(0)

        VAO = vao
    }


    private fun createShaderProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
        val vertexShader = glCreateShader(GL_VERTEX_SHADER)
        glShaderSource(vertexShader, vertexShaderSource)
        glCompileShader(vertexShader)
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            println("error compiling vertex shader")
        }


        val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fragmentShader, fragmentShaderSource)
        glCompileShader(fragmentShader)
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            println("error compiling fragment shader")
        }

        val shaderProgram = glCreateProgram()
        glAttachShader(shaderProgram, vertexShader)
        glAttachShader(shaderProgram, fragmentShader)
        glLinkProgram(shaderProgram)
        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE)
            println("error linking shader")

        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)

        return shaderProgram
    }

    private fun loop() {
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT)

            glUseProgram(shaderProgram)
            glBindVertexArray(VAO)
            glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0)


            glfwSwapBuffers(window)
            glfwPollEvents()
        }
    }

    private fun cleanup() {
        glDeleteProgram(shaderProgram)
        glDeleteVertexArrays(VAO)

        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }
}

fun main() {
    Main().run()
}