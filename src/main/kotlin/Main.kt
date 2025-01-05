import core.math.Quaternion
import core.math.Vector3
import core.math.toFloatBuffer
import core.scene.camera.Camera
import core.scene.camera.PerspectiveCamera
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.ARBVertexArrayObject.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.FloatBuffer
import java.nio.IntBuffer


class Main {
    private var window: Long = 0
    private var shaderProgram: Int = 0
    private var VAO: Int = 0
    private val width = 1280
    private val height = 720

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

//        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, 1)

        window = glfwCreateWindow(width, height, "Kotlin LWJGL", NULL, NULL)
        if (window == NULL) {
            throw IllegalStateException("Failed to create the GLFW window")
        }

        glfwMakeContextCurrent(window)

        glfwSwapInterval(1)
        glfwShowWindow(window)
        GL.createCapabilities()

        setupScene()
    }


    private fun setupScene() {
        val vertexShaderSource = """
            #version 330 core
            layout (location = 0) in vec3 aPos;

            uniform mat4 projection;
            uniform mat4 view;

            void main()
            {
                gl_Position = projection * view * vec4(aPos, 1.0f);
            }
        """.trimIndent()

        val fragmentShaderSource = """
            #version 330 core
            out vec4 FragColor;
            void main()
            {
                FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
            }
        """.trimIndent()

        shaderProgram = createShaderProgram(vertexShaderSource, fragmentShaderSource)

        // VBO, VAO
        val vertices = floatArrayOf(
            0.0f, 0.0f, 10f,
            10.5f, 10.5f, 10f,
            0.0f, 10.5f, 10f
        )
        val indices = intArrayOf(
            0, 1, 2
        )

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

        val position = Vector3(0.0f, 0.0f, -2.0f)
        val forward = Vector3(0.0f, 0.0f, -1.0f)
        val up = Vector3(0.0f, 1.0f, 0.0f)
        val fovY = 70f
        val zNear = 0.1f
        val zFar = 1000.0f

        camera = PerspectiveCamera(
            position,
            forward,
            up,
            width.toFloat(),
            height.toFloat(),
            fovY,
            zNear,
            zFar
        )
    }

    private lateinit var camera: Camera

    private fun FloatArray.toFloatBuffer(): FloatBuffer {
        val buffer = MemoryUtil.memAllocFloat(this.size)
        buffer.put(this).flip()
        return buffer
    }
    private fun IntArray.toIntBuffer(): IntBuffer {
        val buffer = MemoryUtil.memAllocInt(this.size)
        buffer.put(this).flip()
        return buffer
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
        glEnable(GL_DEPTH_TEST)

        val rotAngle = 0.05f

        while (!glfwWindowShouldClose(window)) {
            glClearDepth(1.0)
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            glUseProgram(shaderProgram)

            val viewLocation = glGetUniformLocation(shaderProgram, "view")
            val projectionLocation = glGetUniformLocation(shaderProgram, "projection")
            glUniformMatrix4fv(viewLocation, false, camera.view.toFloatBuffer())
            glUniformMatrix4fv(projectionLocation, false, camera.projection.toFloatBuffer())
            glBindVertexArray(VAO)

            glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, NULL)
            glBindVertexArray(0)

            glfwSwapBuffers(window)
            glfwPollEvents()

            camera.updateRotation(Vector3(0.0f, 0.0f, 1.0f), rotAngle)
            camera.updatePosition(Vector3(0.0f, 0.0f, -0.01f))
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