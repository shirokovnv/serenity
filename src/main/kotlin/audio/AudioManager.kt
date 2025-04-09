package audio

import core.math.Vector3
import core.math.helpers.distance
import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10
import org.lwjgl.openal.ALCCapabilities
import java.nio.ByteBuffer
import java.nio.IntBuffer

class AudioManager : AudioManagerInterface {
    private var device: Long = 0L
    private var context: Long = 0L
    private lateinit var deviceCaps: ALCCapabilities

    private var listener: AudioListener? = null
    private val sources: MutableMap<String, Pair<AudioBuffer, AudioSource>> = mutableMapOf()

    init {
        create()
    }

    fun create() {
        if (device != 0L || context != 0L) {
            throw IllegalStateException("OpenAL device and context already created.")
        }

        device = ALC10.alcOpenDevice(null as ByteBuffer?)

        if (device == 0L) {
            throw IllegalStateException("Failed to open OpenAL device: ${printAudioError()}")
        }

        deviceCaps = ALC.createCapabilities(device)
        context = ALC10.alcCreateContext(device, null as IntBuffer?)

        if (context == 0L) {
            throw IllegalStateException("Failed to create OpenAL context: ${printAudioError()} ")
        }

        if (!ALC10.alcMakeContextCurrent(context)) {
            throw IllegalStateException("Failed to make OpenAL context current: ${printAudioError()}")
        }

        AL.createCapabilities(deviceCaps)

        println("AUDIO MANAGER INITIALIZED")
    }

    override fun loadSound(filename: String): AudioSource {
        val audioData = AudioData.create(filename) ?: throw IllegalStateException("Cannot load audio source.")

        val source = AudioSource()
        val buffer = AudioBuffer()
        buffer.uploadData(audioData.data, audioData.format, audioData.sampleRate)
        source.setBuffer(buffer)

        audioData.dispose()

        sources[filename] = Pair(buffer, source)

        return source
    }

    override fun unloadSound(filename: String, dispose: Boolean) {
        if (dispose) {
            sources[filename]?.second?.let {
                it.stop()
                it.dispose()
            }
            sources[filename]?.first?.dispose()
        }

        sources.remove(filename)
    }

    override fun setListener(listener: AudioListener?) {
        this.listener = listener
    }

    override fun removeListener() {
        this.listener = null
    }

    override fun playSoundWithinHearingRange(hearingRadius: Float) {
        if (listener == null) {
            return
        }

        sources.values.forEach {
            val source = it.second
            val sourcePosition = FloatArray(3)
            alGetSourcefv(source.getId(), AL_POSITION, sourcePosition)

            val distanceToSource = distance(
                Vector3(sourcePosition[0], sourcePosition[1], sourcePosition[2]),
                Vector3(listener!!.getPosition())
            )

            if (distanceToSource <= hearingRadius) {
                if (!isSourcePlaying(source.getId())) {
                    source.play()
                }
                //OpenAL calculates the volume itself using Rolloff, Exp. Distance

            } else {
                if (isSourcePlaying(source.getId())) {
                    source.stop()
                }
            }
        }
    }

    override fun dispose() {
        sources.values.forEach { source ->
            if (isSourcePlaying(source.second.getId())) {
                source.second.stop()
            }

            source.second.dispose()
            source.first.dispose()
        }
        listener = null

        ALC10.alcMakeContextCurrent(0) // detach context

        if (context != 0L) {
            ALC10.alcDestroyContext(context)
            context = 0L
        }

        if (device != 0L) {
            ALC10.alcCloseDevice(device)
            device = 0L
        }
    }

    private fun printAudioError(device: Long = 0L): String? {
        val error = ALC10.alcGetError(device)
        return ALC10.alcGetString(0, error)
    }

    private fun isSourcePlaying(sourceId: Int): Boolean {
        val state = alGetSourcei(sourceId, AL_SOURCE_STATE)
        return state == AL_PLAYING
    }
}