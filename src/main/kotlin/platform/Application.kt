package platform

import audio.AudioInitializationFactory
import audio.AudioManagerInterface
import core.ecs.Behaviour
import core.management.Resources
import core.scene.SceneGraph
import core.scene.TraversalOrder
import graphics.animation.AnimationParser
import graphics.gui.GuiWrapper
import graphics.rendering.RenderPipeline
import graphics.rendering.UpdatePipeline
import graphics.rendering.passes.*
import graphics.rendering.viewport.Viewport
import graphics.rendering.viewport.ViewportInterface
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.system.MemoryUtil
import platform.services.FrameCounter
import platform.services.filesystem.AssimpLoader
import platform.services.filesystem.FileLoader
import platform.services.filesystem.ImageLoader
import platform.services.filesystem.ObjLoader
import platform.services.input.KeyboardInput
import platform.services.input.MouseInput
import platform.services.input.WindowInput
import java.nio.ByteBuffer

abstract class Application(private val settings: ApplicationSettings) {

    private var window: Long = 0
    private var isRunning = false

    inner class ApplicationResources {
        lateinit var keyboardInput: KeyboardInput
        lateinit var mouseInput: MouseInput
        lateinit var windowInput: WindowInput
        lateinit var imageLoader: ImageLoader
        lateinit var fileLoader: FileLoader
        lateinit var objLoader: ObjLoader
        lateinit var fbxLoader: AssimpLoader
        lateinit var frameCounter: FrameCounter
        lateinit var audioManager: AudioManagerInterface
        lateinit var guiWrapper: GuiWrapper
    }

    private var appResources = ApplicationResources()

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
        GL20.glEnable(GL20.GL_DEPTH_TEST)
        GL20.glClearDepth(1.0)
        GL20.glClearColor(
            settings.backgroundColor.red,
            settings.backgroundColor.green,
            settings.backgroundColor.blue,
            settings.backgroundColor.alpha
        )
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        appPipes.renderPipeline.render(sceneGraph, TraversalOrder.BREADTH_FIRST)
        appResources.guiWrapper.render()

        GLFW.glfwSwapBuffers(window)
        GLFW.glfwPollEvents()
    }

    fun launch() {
        create()
        printDeviceProperties()
        registerSharedResources()
        registerPipelines()
        registerInputCallbacks()
        registerGui()
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

        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)

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

        val frameCounter = appResources.frameCounter
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
        // TODO: separate
        val behaviours = mutableListOf<Behaviour>()
        sceneGraph.traverse({ obj ->
            behaviours.addAll(obj.getComponents<Behaviour>())
        }, TraversalOrder.DEPTH_FIRST)
        behaviours.forEach { it.destroy() }
        behaviours.clear()

        appPipes.renderPipeline.dispose()
        Resources.dispose()

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
        println("Max Texture Units: ${GL11.glGetInteger(GL43.GL_MAX_TEXTURE_IMAGE_UNITS)}")
    }

    private fun registerInputCallbacks() {
        GLFW.glfwSetCursorPosCallback(window, appResources.mouseInput::mousePosCallback)
        GLFW.glfwSetMouseButtonCallback(window, appResources.mouseInput::mouseButtonCallback)
        GLFW.glfwSetKeyCallback(window, appResources.keyboardInput::keyCallback)
        GLFW.glfwSetFramebufferSizeCallback(window, appResources.windowInput::windowResizeCallback)
        GLFW.glfwSetWindowFocusCallback(window, appResources.windowInput::windowFocusCallback)
    }

    private fun registerGui() {
        appResources.guiWrapper = GuiWrapper(window)
        Resources.put<GuiWrapper>(appResources.guiWrapper)

        appResources.keyboardInput.setGuiWrapper(appResources.guiWrapper)
        appResources.mouseInput.setGuiWrapper(appResources.guiWrapper)
    }

    protected open fun registerSharedResources() {
        val viewPort = Viewport(settings.screenWidth, settings.screenHeight)
        Resources.put<ViewportInterface>(viewPort)

        appResources.keyboardInput = KeyboardInput(window)
        appResources.mouseInput = MouseInput(window)
        appResources.windowInput = WindowInput(window)
        appResources.imageLoader = ImageLoader()
        appResources.fileLoader = FileLoader()
        appResources.objLoader = ObjLoader(appResources.fileLoader, appResources.imageLoader)
        appResources.fbxLoader = AssimpLoader(appResources.fileLoader, AnimationParser())
        appResources.frameCounter = FrameCounter(settings.frameRate)
        appResources.audioManager = AudioInitializationFactory.initialize()

        Resources.put<KeyboardInput>(appResources.keyboardInput)
        Resources.put<MouseInput>(appResources.mouseInput)
        Resources.put<WindowInput>(appResources.windowInput)
        Resources.put<ImageLoader>(appResources.imageLoader)
        Resources.put<FileLoader>(appResources.fileLoader)
        Resources.put<ObjLoader>(appResources.objLoader)
        Resources.put<AssimpLoader>(appResources.fbxLoader)
        Resources.put<FrameCounter>(appResources.frameCounter)
        Resources.put<AudioManagerInterface>(appResources.audioManager)
    }

    protected open fun registerPipelines() {
        appPipes.updatePipeline = UpdatePipeline(appResources.frameCounter)
        appPipes.renderPipeline = RenderPipeline()

        appPipes.renderPipeline.addRenderPass(ReflectionPass)
        appPipes.renderPipeline.addRenderPass(RefractionPass)
        appPipes.renderPipeline.addRenderPass(ShadowPass)
        appPipes.renderPipeline.addRenderPass(NormalPass)
        appPipes.renderPipeline.addRenderPass(PostProcPass)
    }

    protected open fun registerSceneGraph() {
        sceneGraph = oneTimeSceneInit()
    }

    private fun setIcon() {
        val bufferedImage: ByteBuffer = appResources.imageLoader.loadImageToByteBuffer("logo/icon.png")

        val image = GLFWImage.malloc()

        image[32, 32] = bufferedImage

        val images = GLFWImage.malloc(1)
        images.put(0, image)

        GLFW.glfwSetWindowIcon(window, images)
    }
}