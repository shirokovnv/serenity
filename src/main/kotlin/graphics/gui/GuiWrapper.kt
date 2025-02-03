package graphics.gui

import core.management.Disposable
import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiCond
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.lwjgl.glfw.GLFW

class GuiWrapper(
    private val window: Long,
    private val glslVersion: String = "#version 150",
    private var columnGridSettings: GuiColumnGridSettings = GuiColumnGridSettings(),
    private var iniSettingsFlag: Boolean = false
) : Disposable {
    private var imGuiGlfw = ImGuiImplGlfw()
    private var imGuiGl3 = ImGuiImplGl3()

    private val windows = mutableMapOf<String, GuiWindow>()

    init {
        ImGui.createContext()

        // Disable saving settings to imgui.ini
        if (!iniSettingsFlag) {
            val io = ImGui.getIO()
            io.iniFilename = null
        }

        imGuiGlfw.init(window, true)
        imGuiGl3.init(glslVersion)
    }

    fun addOrUpdateWindow(window: GuiWindow) {
        val windowCache = windows.remove(window.name)

        if (windowCache != null) {
            window.components.addAll(windowCache.components)
        }

        windows[window.name] = window
    }

    fun removeWindow(name: String): Boolean {
        return windows.remove(name) != null
    }

    fun removeWindow(window: GuiWindow): Boolean {
        return windows.remove(window.name) != null
    }

    fun addComponent(windowName: String, component: GuiComponent) {
        windows[windowName]?.components?.add(component)
    }

    fun addComponent(window: GuiWindow, component: GuiComponent) {
        windows[window.name]?.components?.add(component)
    }

    fun removeComponent(windowName: String, component: GuiComponent) {
        windows[windowName]?.components?.remove(component)
    }

    fun removeComponent(window: GuiWindow, component: GuiComponent) {
        windows[window.name]?.components?.remove(component)
    }

    fun wantCaptureMouse(): Boolean {
        return ImGui.getIO().wantCaptureMouse
    }

    fun wantCaptureKeyboard(): Boolean {
        return ImGui.getIO().wantCaptureKeyboard
    }

    fun render() {
        startFrame()

        val numWindows = windows.values.filterIsInstance<GuiWindow.GridWindow>().size
        val cols = columnGridSettings.numColumns
        val rows = (numWindows + cols - 1) / cols

        val percentage = columnGridSettings.percentage
        val fixedGridWidth = ImGui.getIO().displaySize.x * percentage
        val windowWidth = fixedGridWidth / cols
        val windowHeight = ImGui.getIO().displaySize.y / rows

        var windowIndex = 0
        for (window in windows.values) {
            when (window) {
                is GuiWindow.GridWindow -> {
                    val row = windowIndex / cols
                    val col = windowIndex % cols
                    val windowPosX = (col * windowWidth)
                    val windowPosY = (row * windowHeight)
                    ImGui.setNextWindowPos(ImVec2(windowPosX, windowPosY), ImGuiCond.FirstUseEver)
                    ImGui.setNextWindowSize(ImVec2(windowWidth, windowHeight), ImGuiCond.FirstUseEver)
                    ImGui.begin(window.name)
                    window.components
                        .filter { it.isActive() }
                        .forEach { guiComponent ->
                            guiComponent.onRenderGUI()
                        }
                    ImGui.end()
                    windowIndex++
                }

                is GuiWindow.FreeWindow -> {
                    ImGui.setNextWindowPos(window.position, ImGuiCond.FirstUseEver)
                    ImGui.setNextWindowSize(window.size, ImGuiCond.FirstUseEver)
                    ImGui.begin(window.name)
                    window.components
                        .filter { it.isActive() }
                        .forEach { guiComponent ->
                            guiComponent.onRenderGUI()
                        }
                    ImGui.end()
                }
            }
        }

        endFrame()
    }

    fun setIniSettingsFlag(iniSettingsFlag: Boolean) {
        this.iniSettingsFlag = iniSettingsFlag
    }

    fun getIniSettingsFlag(): Boolean = iniSettingsFlag

    fun setColumnGridSettings(columnGridSettings: GuiColumnGridSettings) {
        this.columnGridSettings = columnGridSettings
    }

    fun getColumnGridSettings(): GuiColumnGridSettings = columnGridSettings

    private fun startFrame() {
        imGuiGl3.newFrame()
        imGuiGlfw.newFrame()
        ImGui.newFrame()
    }

    private fun endFrame() {
        ImGui.render()
        imGuiGl3.renderDrawData(ImGui.getDrawData())

        // Update and Render additional Platform Windows
        // (Platform functions may change the current OpenGL context, so we save/restore it to make it easier to paste this code elsewhere.
        //  For this specific demo app we could also call glfwMakeContextCurrent(window) directly)
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val backupCurrentContext = GLFW.glfwGetCurrentContext()
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            GLFW.glfwMakeContextCurrent(backupCurrentContext)
        }
    }

    override fun dispose() {
        windows.clear()
        imGuiGl3.shutdown()
        imGuiGlfw.shutdown()
        ImGui.destroyContext()
    }
}