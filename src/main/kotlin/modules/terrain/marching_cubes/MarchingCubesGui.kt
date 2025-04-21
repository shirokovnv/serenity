package modules.terrain.marching_cubes

import core.events.Events
import core.math.Vector3
import graphics.gui.GuiBehaviour
import graphics.gui.GuiWindow
import imgui.ImGui
import imgui.type.ImBoolean

class MarchingCubesGui(
    private val gridParams: MarchingCubesGridParams,
    private val noiseParams: MarchingCubesNoiseParams,
    private val extraParams: MarchingCubesExtraParams
) : GuiBehaviour() {
    private val resolutionRange = IntArray(1) { gridParams.resolution }
    private val isoLevelRange = FloatArray(1) { gridParams.isoLevel }

    private val frequencyRange = FloatArray(1) { noiseParams.frequency }
    private val amplitudeRange = FloatArray(1) { noiseParams.amplitude }
    private val lacunarityRange = FloatArray(1) { noiseParams.lacunarity }
    private val persistenceRange = FloatArray(1) { noiseParams.persistence }
    private val octaveRange = IntArray(1) { noiseParams.octaves }

    private val isTerracingEnabled = ImBoolean(extraParams.isTerracingEnabled)
    private val terraceHeightRange = FloatArray(1) { extraParams.terraceHeight }

    private val isWarpingEnabled = ImBoolean(extraParams.isWarpingEnabled)
    private val warpFactorRange = IntArray(1) { extraParams.warpFactor }

    private val colorOne = floatArrayOf(extraParams.colorOne.x, extraParams.colorOne.y, extraParams.colorOne.z)
    private val colorTwo = floatArrayOf(extraParams.colorTwo.x, extraParams.colorTwo.y, extraParams.colorTwo.z)

    override fun guiWindow(): GuiWindow {
        return GuiWindow.GridWindow("Marching Cubes")
    }

    override fun update(deltaTime: Float) {
    }

    override fun onRenderGUI() {
        ImGui.text("Grid")

        var changed = ImGui.sliderInt(
            "Resolution",
            resolutionRange,
            MarchingCubesGridParams.MIN_RESOLUTION,
            MarchingCubesGridParams.MAX_RESOLUTION
        )

        changed = changed || ImGui.sliderFloat(
            "ISO Level",
            isoLevelRange,
            MarchingCubesGridParams.MIN_ISO_LEVEL,
            MarchingCubesGridParams.MAX_ISO_LEVEL
        )

        ImGui.separator()

        ImGui.text("Noise")

        changed = changed || ImGui.sliderFloat(
            "Frequency",
            frequencyRange,
            MarchingCubesNoiseParams.MIN_FREQUENCY,
            MarchingCubesNoiseParams.MAX_FREQUENCY
        )

        changed = changed || ImGui.sliderFloat(
            "Amplitude",
            amplitudeRange,
            MarchingCubesNoiseParams.MIN_AMPLITUDE,
            MarchingCubesNoiseParams.MAX_AMPLITUDE
        )

        changed = changed || ImGui.sliderFloat(
            "Lacunarity",
            lacunarityRange,
            MarchingCubesNoiseParams.MIN_LACUNARITY,
            MarchingCubesNoiseParams.MAX_LACUNARITY
        )

        changed = changed || ImGui.sliderFloat(
            "Persistence",
            persistenceRange,
            MarchingCubesNoiseParams.MIN_PERSISTENCE,
            MarchingCubesNoiseParams.MAX_PERSISTENCE
        )

        changed = changed || ImGui.sliderInt(
            "Octaves", octaveRange,
            MarchingCubesNoiseParams.MIN_OCTAVES,
            MarchingCubesNoiseParams.MAX_OCTAVES
        )

        changed = changed || ImGui.checkbox("Terracing", isTerracingEnabled)

        if (isTerracingEnabled.get()) {
            changed = changed || ImGui.sliderFloat(
                "Terrace Height",
                terraceHeightRange,
                MarchingCubesExtraParams.MIN_TERRACE_HEIGHT,
                MarchingCubesExtraParams.MAX_TERRACE_HEIGHT
            )
        }

        changed = changed || ImGui.checkbox("Warping", isWarpingEnabled)

        if (isWarpingEnabled.get()) {
            changed = changed || ImGui.sliderInt(
                "Warp factor",
                warpFactorRange,
                MarchingCubesExtraParams.MIN_WARP_FACTOR,
                MarchingCubesExtraParams.MAX_WARP_FACTOR
            )
        }

        changed = changed || ImGui.colorEdit3("Color One", colorOne)
        changed = changed || ImGui.colorEdit3("Color Two", colorTwo)

        if (changed) {
            Events.publish(
                MarchingCubesChangedEvent(
                    MarchingCubesGridParams(
                        resolutionRange[0],
                        isoLevelRange[0]
                    ),
                    MarchingCubesNoiseParams(
                        frequencyRange[0],
                        amplitudeRange[0],
                        lacunarityRange[0],
                        persistenceRange[0],
                        octaveRange[0]
                    ),
                    MarchingCubesExtraParams(
                        isTerracingEnabled.get(),
                        terraceHeightRange[0],
                        isWarpingEnabled.get(),
                        warpFactorRange[0],
                        Vector3(colorOne[0], colorOne[1], colorOne[2]),
                        Vector3(colorTwo[0], colorTwo[1], colorTwo[2])
                    )
                ),
                this
            )
        }
    }
}