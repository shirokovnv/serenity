package platform

import core.scene.Object
import core.scene.SceneGraph
import core.scene.TraversalOrder
import graphics.rendering.RenderPipeline
import graphics.rendering.UpdatePipeline
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.ReflectionPass
import graphics.rendering.passes.RefractionPass
import graphics.rendering.passes.ShadowPass
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.system.MemoryUtil
import platform.services.FrameCounter
import platform.services.filesystem.ImageLoader
import platform.services.filesystem.TextFileLoader
import platform.services.input.KeyboardInput
import platform.services.input.MouseInput
import java.nio.ByteBuffer

abstract class Application(private val settings: ApplicationSettings) {

    private var window: Long = 0
    private var isRunning = false

    inner class ApplicationServices {
        lateinit var keyboardInput: KeyboardInput
        lateinit var mouseInput: MouseInput
        lateinit var imageLoader: ImageLoader
        lateinit var textFileLoader: TextFileLoader
        lateinit var frameCounter: FrameCounter
    }

    private var appServices = ApplicationServices()

    inner class ApplicationPipelines {
        lateinit var updatePipeline: UpdatePipeline
        lateinit var renderPipeline: RenderPipeline
    }

    private var appPipes = ApplicationPipelines()

    private lateinit var sceneGraph: SceneGraph

    abstract fun oneTimeSceneInit(): SceneGraph

    private fun update() {
        appPipes.updatePipeline.update(sceneGraph, TraversalOrder.BREADTH_FIRST)
    }

    private fun render() {
        GL20.glFrontFace(GL20.GL_CCW)
        GL20.glEnable(GL20.GL_CULL_FACE)
        GL20.glCullFace(GL20.GL_BACK)
        GL20.glEnable(GL20.GL_DEPTH_TEST)

        GL20.glClearDepth(1.0)
        GL20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        appPipes.renderPipeline.render(sceneGraph, TraversalOrder.BREADTH_FIRST)

        GLFW.glfwSwapBuffers(window)
        GLFW.glfwPollEvents()
    }

    fun launch() {
        create()
        printDeviceProperties()
        registerSharedServices()
        registerPipelines()
        registerInputCallbacks()
        registerSceneGraph()
        setIcon()
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

        val frameCounter = appServices.frameCounter
        while (isRunning) {
            var canRender = false

            frameCounter.begin()
            while (frameCounter.canRenderFrame()) {
                frameCounter.processFrame()
                canRender = true

                if (GLFW.glfwWindowShouldClose(window)) {
                    stop()
                }

                update()

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
        GLFW.glfwSetCursorPosCallback(window, appServices.mouseInput::mousePosCallback)
        GLFW.glfwSetMouseButtonCallback(window, appServices.mouseInput::mouseButtonCallback)
        GLFW.glfwSetKeyCallback(window, appServices.keyboardInput::keyCallback)
    }

    protected open fun registerSharedServices() {
        appServices.keyboardInput = KeyboardInput(window)
        appServices.mouseInput = MouseInput(window)
        appServices.imageLoader = ImageLoader()
        appServices.textFileLoader = TextFileLoader()
        appServices.frameCounter = FrameCounter(settings.frameRate)

        Object.services.putService<KeyboardInput>(appServices.keyboardInput)
        Object.services.putService<MouseInput>(appServices.mouseInput)
        Object.services.putService<ImageLoader>(appServices.imageLoader)
        Object.services.putService<TextFileLoader>(appServices.textFileLoader)
        Object.services.putService<FrameCounter>(appServices.frameCounter)
    }

    protected open fun registerPipelines() {
        appPipes.updatePipeline = UpdatePipeline(appServices.frameCounter)
        appPipes.renderPipeline = RenderPipeline()

        appPipes.renderPipeline.addRenderPass(ReflectionPass)
        appPipes.renderPipeline.addRenderPass(RefractionPass)
        appPipes.renderPipeline.addRenderPass(ShadowPass)
        appPipes.renderPipeline.addRenderPass(NormalPass)
    }

    protected open fun registerSceneGraph() {
        sceneGraph = oneTimeSceneInit()
    }

    private fun setIcon() {
        val bufferedImage: ByteBuffer = appServices.imageLoader.loadImageToByteBuffer("logo/icon.png")

        val image = GLFWImage.malloc()

        image[32, 32] = bufferedImage

        val images = GLFWImage.malloc(1)
        images.put(0, image)

        GLFW.glfwSetWindowIcon(window, images)
    }
}