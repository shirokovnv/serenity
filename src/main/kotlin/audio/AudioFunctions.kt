package audio

import org.lwjgl.openal.AL10

fun checkAudioErrors(message: String) {
    val error = AL10.alGetError()
    val errorString = AL10.alGetString(error)
    if (error != AL10.AL_NO_ERROR) {
        throw IllegalStateException("${message}. Error ($error): $errorString")
    }
}