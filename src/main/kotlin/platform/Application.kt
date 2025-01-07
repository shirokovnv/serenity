package platform

import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.system.MemoryUtil
import platform.components.input.KeyboardInput
import platform.components.input.MouseInput

abstract class Application(private val settings: ApplicationSettings) {

    private var window: Long = 0
    private var isRunning = false

    protected lateinit var keyboardInput: KeyboardInput
    protected lateinit var mouseInput: MouseInput

    abstract fun oneTimeSceneInit()

    fun render() {
        GL20.glFrontFace(GL20.GL_CCW)
        GL20.glEnable(GL20.GL_CULL_FACE)
        GL20.glCullFace(GL20.GL_BACK)
        GL20.glEnable(GL20.GL_DEPTH_TEST)

        GL20.glClearDepth(1.0)
        GL20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        GLFW.glfwSwapBuffers(window)
        GLFW.glfwPollEvents()
    }

    fun launch() {
        create()
        printDeviceProperties()
        registerInputCallbacks()
        oneTimeSceneInit()
        run()
        destroy()
    }

    private fun create() {
        GLFWErrorCallback.createPrint(System.err).set()
        if (!GLFW.glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, 1)

        window = GLFW.glfwCreateWindow(settings.screenWidth, settings.screenHeight, settings.title, 0, 0)
        if (window == MemoryUtil.NULL) {
            throw IllegalStateException("Failed to create the GLFW window")
        }

        GLFW.glfwMakeContextCurrent(window)
        GL.createCapabilities()

        GLFW.glfwShowWindow(window)
    }

    private fun run() {
        isRunning = true

        val frameCounter = FrameCounter(settings.frameRate)
        while (isRunning) {
            var canRender = false

            frameCounter.begin()
            while (frameCounter.canRenderFrame()) {
                frameCounter.processFrame()
                canRender = true

                if (GLFW.glfwWindowShouldClose(window)) {
                    stop()
                }

                frameCounter.updateFrameFps()
            }

            if (canRender) {
                render()
                frameCounter.incrementFrames()
            } else {
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
            }
        }
    }

    private fun stop() {
        isRunning = false
    }

    private fun destroy() {
        Callbacks.glfwFreeCallbacks(window)
        GLFW.glfwDestroyWindow(window)
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)?.free()
    }

    private fun printDeviceProperties() {
        println("OpenGL version: ${GL11.glGetString(GL11.GL_VERSION)} bytes")
        println("Max Geometry Uniform Blocks: ${GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS} bytes")
        println("Max Geometry Shader Invocations: ${GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS} bytes")
        println("Max Uniform Buffer Bindings: ${GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS} bytes")
        println("Max Uniform Block Size: ${GL31.GL_MAX_UNIFORM_BLOCK_SIZE} bytes")
        println("Max SSBO Block Size: ${GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE} bytes")
    }

    private fun registerInputCallbacks() {
        keyboardInput = KeyboardInput(window)
        mouseInput = MouseInput(window)

        GLFW.glfwSetCursorPosCallback(window, mouseInput::mousePosCallback)
        GLFW.glfwSetMouseButtonCallback(window, mouseInput::mouseButtonCallback)
        GLFW.glfwSetKeyCallback(window, keyboardInput::keyCallback)
    }
}