package modules.terrain.audio

import audio.AudioAttenuationModel
import audio.AudioListener
import audio.AudioManagerInterface
import audio.AudioSource
import core.management.Resources
import core.math.Vector3
import core.scene.behaviour.FrameUpdateBehaviour
import core.scene.camera.Camera

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

    override fun create() {
        source = audioManager.loadSound(ambientSoundsFilename)
            .setPosition(terrainCenter)
            .setGain(1f)
            .setPitch(1f)
            .setAttenuationModel(AudioAttenuationModel.EXPONENT, 1f, 10f, hearingRadius)
            .setLooping(true)

        audioManager.setListener(listener)
    }

    override fun destroy() {
        audioManager.unloadSound(ambientSoundsFilename)
    }

    override fun onUpdate(deltaTime: Float) {
        listener.setPosition(camera.position())
        audioManager.playSoundWithinHearingRange(hearingRadius)
    }
}