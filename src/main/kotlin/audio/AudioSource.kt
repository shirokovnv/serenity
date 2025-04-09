package audio

import core.management.Disposable
import core.math.Vector3
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.AL11

class AudioSource : Disposable {
    private var id: Int = 0

    init {
        create()
    }

    fun create() {
        if (id == 0) {
            id = alGenSources()
        }
    }

    fun getId(): Int = id

    fun setBuffer(buffer: AudioBuffer): AudioSource {
        alSourcei(id, AL_BUFFER, buffer.getId())
        checkAudioErrors("Failed to set buffer for source")

        return this
    }

    fun setPosition(x: Float, y: Float, z: Float): AudioSource {
        alSource3f(id, AL_POSITION, x, y, z)
        checkAudioErrors("Failed to set position for source")

        return this
    }

    fun setPosition(position: Vector3): AudioSource {
        return setPosition(position.x, position.y, position.z)
    }

    fun setVelocity(x: Float, y: Float, z: Float): AudioSource {
        alSource3f(id, AL_VELOCITY, x, y, z)
        checkAudioErrors("Failed to set velocity for source")

        return this
    }

    fun setVelocity(velocity: Vector3): AudioSource {
        return setVelocity(velocity.x, velocity.y, velocity.z)
    }

    fun setLooping(looping: Boolean): AudioSource {
        alSourcei(id, AL_LOOPING, if (looping) AL_TRUE else AL_FALSE)
        checkAudioErrors("Failed to set looping for source")

        return this
    }

    fun setGain(gain: Float): AudioSource {
        alSourcef(id, AL_GAIN, gain)
        checkAudioErrors("Failed to set gain for source")

        return this
    }

    fun setPitch(pitch: Float): AudioSource {
        alSourcef(id, AL_PITCH, pitch)
        checkAudioErrors("Failed to set pitch for source")

        return this
    }

    fun setAttenuationModel(
        model: AudioAttenuationModel,
        referenceDistance: Float = 1f,
        rolloffFactor: Float = 1f,
        maxDistance: Float = 100.0f
    ): AudioSource {
        alSourcef(id, AL_REFERENCE_DISTANCE, referenceDistance)
        alSourcef(id, AL_MAX_DISTANCE, maxDistance)
        alSourcef(id, AL_ROLLOFF_FACTOR, rolloffFactor)

        when (model) {
            AudioAttenuationModel.NONE -> {
                alDistanceModel(AL_NONE)
            }

            AudioAttenuationModel.LINEAR -> {
                alDistanceModel(AL11.AL_LINEAR_DISTANCE)
            }

            AudioAttenuationModel.LINEAR_CLAMPED -> {
                alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED)
            }

            AudioAttenuationModel.INVERSE -> {
                alDistanceModel(AL_INVERSE_DISTANCE)
            }

            AudioAttenuationModel.INVERSE_CLAMPED -> {
                alDistanceModel(AL_INVERSE_DISTANCE_CLAMPED)
            }

            AudioAttenuationModel.EXPONENT -> {
                alDistanceModel(AL11.AL_EXPONENT_DISTANCE)
            }

            AudioAttenuationModel.EXPONENT_CLAMPED -> {
                alDistanceModel(AL11.AL_EXPONENT_DISTANCE_CLAMPED)
            }
        }

        checkAudioErrors("Failed to set attenuation model for source")

        return this
    }

    fun play(): AudioSource {
        alSourcePlay(id)
        checkAudioErrors("Failed to play source")

        return this
    }

    fun pause(): AudioSource {
        alSourcePause(id)
        checkAudioErrors("Failed to pause source")

        return this
    }

    fun stop(): AudioSource {
        alSourceStop(id)
        checkAudioErrors("Failed to stop source")

        return this
    }

    override fun dispose() {
        if (id != 0) {
            alDeleteSources(id)
            checkAudioErrors("Failed to delete OpenAL source")
            id = 0
        }
    }
}