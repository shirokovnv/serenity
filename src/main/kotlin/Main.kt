import core.math.Quaternion
import core.math.Vector3
import core.math.toFloatBuffer
import core.scene.Object
import core.scene.Transform
import core.scene.camera.*
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

    private var isRunning = false
    val NANOSECOND = 1000000000L

    companion object {
        private var fps = 0
        private const val framerate = 200f
        const val frameTime = 1.0f / framerate
        var currentFrameTime = 0f

        fun getFps(): Int {
            return fps
        }

        fun setFps(fps: Int) {
            Companion.fps = fps
        }
    }

    fun run() {
        isRunning = true
        var frames = 0
        var frameCounter: Long = 0
        var lastTime = System.nanoTime()
        var unprocessedTime = 0.0

        // Rendering Loop
        while (isRunning) {
            var render = false
            val startTime = System.nanoTime()
            val passedTime = startTime - lastTime
            lastTime = startTime
            unprocessedTime += passedTime / NANOSECOND.toDouble()
            frameCounter += passedTime
            while (unprocessedTime > frameTime) {
                render = true
                unprocessedTime -= frameTime.toDouble()
                if (glfwWindowShouldClose(window)) {
                    stop()
                }
                if (frameCounter >= NANOSECOND) {
                    setFps(frames)
                    currentFrameTime = 1.0f / fps
                    frames = 0
                    frameCounter = 0
                }
            }
            if (render) {
                //update()
                processInput()
                render()
                frames++
            } else {
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
            }
        }
        cleanup()
    }

    private fun stop() {
        if (!isRunning) return
        isRunning = false
    }

    private var lastFrameTime = 0.0

    lateinit private var mouseInput: MouseInput
    lateinit private var keyboardInput: KeyboardInput

    private var deltaTime = 0.0f
    lateinit private var cameraController: CameraController

    private fun processInput() {
        if (keyboardInput.isKeyPressed(GLFW_KEY_ESCAPE)) {
            println("SHUTDOWN")
        }

        if (keyboardInput.isKeyPressed(GLFW_KEY_W)) {
            cameraController.processKeyboard(CameraMovement.FORWARD, deltaTime)
        }
        if (keyboardInput.isKeyPressed(GLFW_KEY_S)) {
            cameraController.processKeyboard(CameraMovement.BACKWARD, deltaTime)
        }
        if (keyboardInput.isKeyPressed(GLFW_KEY_A)) {
            cameraController.processKeyboard(CameraMovement.LEFT, deltaTime)
        }
        if (keyboardInput.isKeyPressed(GLFW_KEY_D)) {
            cameraController.processKeyboard(CameraMovement.RIGHT, deltaTime)
        }

        if (keyboardInput.isKeyPressed(GLFW_KEY_UP)) {
            cameraController.processKeyboard(CameraMovement.UP, deltaTime)
        }
        if (keyboardInput.isKeyPressed(GLFW_KEY_DOWN)) {
            cameraController.processKeyboard(CameraMovement.DOWN, deltaTime)
        }

        if (keyboardInput.isKeyPressed(GLFW_KEY_LEFT)) {
            cameraController.processKeyboardForRotation(CameraRotation.LEFT, deltaTime)
        }

        if (keyboardInput.isKeyPressed(GLFW_KEY_RIGHT)) {
            cameraController.processKeyboardForRotation(CameraRotation.RIGHT, deltaTime)
        }

        //cameraController.processMouseMovement(mouseInput.mouseXDelta, mouseInput.mouseYDelta)
//        camera.processScroll(mouseInput.mouseScrollY);
    }

    private fun render() {
        val currentFrameTime = glfwGetTime()
        deltaTime = (currentFrameTime - lastFrameTime).toFloat();
        lastFrameTime = currentFrameTime;
        // Теперь применяйте deltaTime для ротации
        val rotationSpeed = 1.3f; // Угол поворота в секунду
        val rotationAngle = rotationSpeed * deltaTime;

        glFrontFace(GL_CCW)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glEnable(GL_DEPTH_TEST)

        glClearDepth(1.0)
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glUseProgram(shaderProgram)

        val viewLocation = glGetUniformLocation(shaderProgram, "view")
        val projectionLocation = glGetUniformLocation(shaderProgram, "projection")
        val viewProjectionLocation = glGetUniformLocation(shaderProgram, "viewProjection")
        val objLocation = glGetUniformLocation(shaderProgram, "model")

        glUniformMatrix4fv(viewLocation, false, camera.view.toFloatBuffer())
        glUniformMatrix4fv(projectionLocation, false, camera.projection.toFloatBuffer())
        glUniformMatrix4fv(viewProjectionLocation, true, camera.viewProjection.toFloatBuffer())
        glUniformMatrix4fv(objLocation, false, objTransform.matrix().toFloatBuffer())
        glBindVertexArray(VAO)

        glEnableVertexAttribArray(0)
        glDrawArrays(GL_TRIANGLES, 0, 3)
        //glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
        glDisableVertexAttribArray(0)
        glBindVertexArray(0)

        glfwSwapBuffers(window)
        glfwPollEvents()

        //camera.updatePosition(Vector3(0.0f, 0.0f, 0.001f))
//        camera.updateRotation(Vector3(0.0f, 0.0f, 1.0f), rotationAngle)
        //camera.updateRotation(Vector3(0.0f, 0.0f, rotationAngle))

//        println("FPS: ${getFps()}")
    }

    fun launch() {
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

        window = glfwCreateWindow(width, height, "Kotlin LWJGL", 0, 0)
        if (window == NULL) {
            throw IllegalStateException("Failed to create the GLFW window")
        }

        glfwMakeContextCurrent(window)
        GL.createCapabilities()

//        glfwSwapInterval(1)
        glfwShowWindow(window)

        setupScene()
    }

    private val objTransform = Transform()

    private fun setupScene() {
        val vertexShaderSource = """
            #version 330 core
            layout (location = 0) in vec3 aPos;

            uniform mat4 projection;
            uniform mat4 view;
            uniform mat4 viewProjection;
            uniform mat4 model;

            void main()
            {
                gl_Position = viewProjection * model * vec4(aPos, 1.0f);
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
            0.0f, 0.0f, -1f,
            1.0f, 0.0f, -1f,
            1.0f, 1.0f, -1f
        )
        val indices = intArrayOf(
            0, 1, 2
        )

        objTransform.setScale(Vector3(2f, 2f, 1f))
        objTransform.setTranslation(Vector3(-0.0f, 0.0f, -3.0f))

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

        val position = Vector3(0.0f, 0.0f, 2.0f)
        val forward = Vector3(0.0f, 0.0f, -1.0f)
        val up = Vector3(0.0f, 1.0f, 0.0f)
        val fovY = 70f
        val zNear = 0.1f
        val zFar = 1000.0f

        camera = PerspectiveCamera(
            width.toFloat(),
            height.toFloat(),
            fovY,
            zNear,
            zFar
        )

//        camera = OrthographicCamera(
//            -100f,
//            100f,
//            -100f,
//            100f,
//            -100f,
//            100f
//        )

        val camObj = Object()
        camObj.addComponent(camera)

        camera.transform.setTranslation(Vector3(0f,0f, -2f))
//        camera.position.z += -2.0f
//        camera.rotateY(60f)
        //camera.transform.setRotation(Vector3(0.0f, 180f.toRadians(), 0.0f))

        println(camera.position())
        println(camera.forward())
        println(camera.up())
        println(camera.view)

        cameraController = CameraController(3.1f, 0.1f)

        mouseInput = MouseInput(window, cameraController)
        keyboardInput = KeyboardInput(window)

        camObj.addComponent(cameraController)

        glfwSetCursorPosCallback(window, mouseInput::mousePosCallback)
        glfwSetMouseButtonCallback(window, mouseInput::mouseButtonCallback)
        glfwSetKeyCallback(window, keyboardInput::keyCallback)

        val stride = 3
        var i = 0
        while (i < vertices.size) {
            val p0 = Vector3(vertices[i], vertices[i+1], vertices[i+2])
            val p = camera.projection * camera.view * Quaternion(p0, 1.0f)
            val w = p.w
            println(p/w)
            i+= stride
        }
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
        run()
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

class KeyboardInput(val window: Long) {
    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if(action==GLFW_PRESS) {
            //println("Pressed $key")
        }

    }
    fun isKeyPressed(key: Int): Boolean {
        return glfwGetKey(window, key) == GLFW_PRESS
    }
}

var lastX = 1280.0
var lastY = 720.0
var isFirstMouse = true


class MouseInput(val window: Long, val controller: CameraController) {
    var mouseXDelta = 0f
    var mouseYDelta = 0f
    var mouseScrollY = 0f

    fun mousePosCallback(window: Long, xpos: Double, ypos: Double) {

        if (isFirstMouse) {
            lastX = xpos
            lastY = ypos

            isFirstMouse = false
        }

        val xOffset = xpos - lastX
        val yOffset = lastY - ypos

        lastX = xpos
        lastY = ypos

        controller.processMouseMovement(xOffset.toFloat(), yOffset.toFloat())
    }
    fun mouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {

    }
}

fun main() {
    Main().launch()
}