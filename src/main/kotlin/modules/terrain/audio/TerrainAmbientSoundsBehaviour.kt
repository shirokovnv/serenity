package modules.terrain.audio

import audio.AudioAttenuationModel
import audio.AudioListener
import audio.AudioManagerInterface
import audio.AudioSource
import core.events.Events
import core.management.Resources
import core.math.Vector3
import core.scene.behaviour.FrameUpdateBehaviour
import core.scene.camera.Camera
import org.lwjgl.glfw.GLFW
import platform.services.input.KeyPressedEvent

class TerrainAmbientSoundsBehaviour(
    private val terrainCenter: Vector3,
    private val hearingRadius: Float
) : FrameUpdateBehaviour() {
    private val ambientSoundsFilename = "audio/birds.wav"

    private val audioManager: AudioManagerInterface
        get() = Resources.get<AudioManagerInterface>()!!

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val listener: AudioListener = AudioListener()

    private lateinit var source: AudioSource
    private var playSounds: Boolean = true

    override fun create() {
        source = audioManager.loadSound(ambientSoundsFilename)
            .setPosition(terrainCenter)
            .setGain(1f)
            .setPitch(1f)
            .setAttenuationModel(AudioAttenuationModel.EXPONENT, 1f, 10f, hearingRadius)
            .setLooping(true)

        audioManager.setListener(listener)

        Events.subscribe<KeyPressedEvent, Any>(::onKeyPressed)
    }

    override fun destroy() {
        audioManager.unloadSound(ambientSoundsFilename)
    }

    override fun onUpdate(deltaTime: Float) {
        listener.setPosition(camera.position())
        listener.setOrientation(camera.forward(), camera.up())
        if (playSounds) {
            audioManager.playSoundWithinHearingRange(hearingRadius)
        }
    }

    private fun onKeyPressed(event: KeyPressedEvent, sender: Any) {
        if (event.key == GLFW.GLFW_KEY_O) {
            playSounds = !playSounds
        }

        if (!playSounds) {
            audioManager.pauseAllSounds()
        }
    }
}