package modules.ocean

import core.math.Vector2
import graphics.gui.GuiBehaviour
import graphics.gui.GuiWindow
import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiMouseCursor
import imgui.flag.ImGuiWindowFlags
import kotlin.math.hypot

class OceanGui(private val material: OceanMaterial) : GuiBehaviour() {
    private var windDirection: ImVec2
    private var windMagnitudeRange = FloatArray(1) { material.wind.length() }
    private var amplitudeRange = FloatArray(1) { material.amplitude }
    private var choppinessRange = FloatArray(1) { material.choppiness }

    init {
        val normalizedWindDirection = Vector2(material.wind).normalize()
        windDirection = ImVec2(normalizedWindDirection.x, normalizedWindDirection.y)
    }

    override fun guiWindow(): GuiWindow {
        return GuiWindow.GridWindow("Ocean")
    }

    override fun update(deltaTime: Float) {
        material.amplitude = amplitudeRange[0]
        material.choppiness = choppinessRange[0]
        material.wind = Vector2(
            windDirection.x,
            windDirection.y
        ) * windMagnitudeRange[0]
    }

    override fun onRenderGUI() {
        ImGui.beginChild("Wind vector", ImVec2(120f, 120f), false, ImGuiWindowFlags.NoScrollbar)

        val windowPos = ImGui.getWindowPos()
        val center = ImVec2(windowPos.x + 60f, windowPos.y + 60f)
        val radius = 60f
        val color = ImGui.getColorU32(ImGuiCol.Text)

        val drawList = ImGui.getWindowDrawList()
        drawList.addCircle(center, radius, color)

        var isDragging = false

        if (ImGui.isMouseHoveringRect(center.x - radius, center.y - radius, center.x + radius, center.y + radius)) {
            if (ImGui.isMouseClicked(0)) {
                isDragging = true
            }
        }

        if (isDragging) {
            if (ImGui.isMouseDragging(0) || ImGui.isMouseDown(0)) {
                windDirection = ImVec2(ImGui.getMousePos().x, ImGui.getMousePos().y)
                windDirection.x -= center.x
                windDirection.y -= center.y

                val len = hypot(windDirection.x.toDouble(), windDirection.y.toDouble()).toFloat()
                windDirection.x /= len
                windDirection.y /= len
                ImGui.setMouseCursor(ImGuiMouseCursor.Hand)
            } else {
                ImGui.setMouseCursor(ImGuiMouseCursor.Arrow)
            }
        }

        drawList.addCircleFilled(center, 5f, color)
        drawList.addLine(
            center,
            ImVec2(center.x + windDirection.x * radius, center.y + windDirection.y * radius),
            color
        )

        ImGui.endChild()

        ImGui.text("(${windDirection.x}, ${windDirection.y}) Wind direction")

        ImGui.sliderFloat(
            "Wind magnitude", windMagnitudeRange,
            OceanMaterial.MIN_WIND_MAGNITUDE,
            OceanMaterial.MAX_WIND_MAGNITUDE
        )

        ImGui.separator()

        ImGui.sliderFloat(
            "Amplitude", amplitudeRange,
            OceanMaterial.MIN_AMPLITUDE,
            OceanMaterial.MAX_AMPLITUDE
        )

        ImGui.separator()

        ImGui.sliderFloat(
            "Choppiness", choppinessRange,
            OceanMaterial.MIN_CHOPPINESS,
            OceanMaterial.MAX_CHOPPINESS
        )
    }
}