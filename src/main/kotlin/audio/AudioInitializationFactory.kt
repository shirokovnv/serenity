package audio

object AudioInitializationFactory {
    fun initialize(): AudioManagerInterface {
        return try {
            AudioManager()
        } catch (_: Exception) {
            NoSoundAudioManager()
        }
    }
}