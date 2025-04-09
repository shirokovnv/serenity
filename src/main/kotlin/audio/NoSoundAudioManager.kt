package audio

class NoSoundAudioManager : AudioManagerInterface {
    override fun loadSound(filename: String): AudioSource {
        println("Audio not initialized. Cannot load sound.")

        return AudioSource()
    }

    override fun unloadSound(filename: String, dispose: Boolean) {
        println("Audio not initialized. Cannot unload sound.")
    }

    override fun setListener(listener: AudioListener?) {
        println("Audio not initialized. Cannot set listener.")
    }

    override fun removeListener() {
        println("Audio not initialized. Cannot remove listener.")
    }

    override fun playSoundWithinHearingRange(hearingRadius: Float) {
        println("Audio not initialized. Cannot play sounds.")
    }

    override fun pauseAllSounds() {
        println("Audio not initialized. Nothing to pause.")
    }

    override fun isReady(): Boolean {
        return false
    }

    override fun dispose() {
        println("Audio not initialized. Nothing to dispose.")
    }
}