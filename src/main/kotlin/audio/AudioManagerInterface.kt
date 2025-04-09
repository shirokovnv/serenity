package audio

import core.management.Disposable

interface AudioManagerInterface : Disposable {
    fun loadSound(filename: String): AudioSource
    fun unloadSound(filename: String, dispose: Boolean = true)
    fun setListener(listener: AudioListener?)
    fun removeListener()
    fun playSoundWithinHearingRange(hearingRadius: Float)
    fun pauseAllSounds()
}