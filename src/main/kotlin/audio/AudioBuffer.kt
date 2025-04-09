package audio

import core.management.Disposable
import org.lwjgl.openal.AL10.*
import java.nio.ByteBuffer

class AudioBuffer : Disposable {
    private var id: Int = 0

    init {
        create()
    }

    fun getId(): Int {
        return id
    }

    fun create() {
        if (id == 0) {
            id = alGenBuffers()
        }
    }

    fun uploadData(data: ByteBuffer, format: Int, sampleRate: Int) {
        alBufferData(id, format, data, sampleRate)
        checkAudioErrors("Failed to upload data to buffer")
    }

    override fun dispose() {
        if (id != 0) {
            alDeleteBuffers(id)
            checkAudioErrors("Failed to delete OpenAL buffer")
            id = 0
        }
    }
}