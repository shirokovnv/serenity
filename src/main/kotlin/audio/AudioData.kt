package audio

import core.management.Disposable
import org.lwjgl.BufferUtils
import org.lwjgl.openal.AL10.*
import java.io.BufferedInputStream
import java.io.IOException
import java.nio.ByteBuffer
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class AudioData private constructor(private val audioStream: AudioInputStream) : Disposable {

    val format: Int
    val sampleRate: Int
    val totalBytes: Int
    val bytesPerFrame: Int
    val data: ByteBuffer

    private val dataArray: ByteArray

    init {
        val audioFormat = audioStream.format
        format = getOpenAlFormat(audioFormat.channels, audioFormat.sampleSizeInBits)
        this.sampleRate = audioFormat.sampleRate.toInt()
        this.bytesPerFrame = audioFormat.frameSize
        this.totalBytes = (audioStream.frameLength * bytesPerFrame).toInt()
        this.data = BufferUtils.createByteBuffer(totalBytes)
        this.dataArray = ByteArray(totalBytes)
        loadData()
    }

    override fun dispose() {
        try {
            audioStream.close()
            data.clear()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadData(): ByteBuffer {
        try {
            val bytesRead = audioStream.read(dataArray, 0, totalBytes)
            data.clear()
            data.put(dataArray, 0, bytesRead)
            data.flip()
        } catch (e: IOException) {
            e.printStackTrace()
            println("Couldn't read bytes from audio stream!")
        }
        return data
    }

    companion object {
        fun create(filename: String): AudioData? {
            return AudioData::class.java.classLoader.getResourceAsStream(filename)?.use { inputStream ->
                try {
                    val bufferedInput = BufferedInputStream(inputStream)
                    val audioStream = AudioSystem.getAudioInputStream(bufferedInput)
                    AudioData(audioStream)
                } catch (e: Exception) { // UnsupportedAudioFileException, IOException
                    e.printStackTrace()
                    println("Couldn't load audio file: $filename")
                    null
                }
            }
        }

        private fun getOpenAlFormat(channels: Int, bitsPerSample: Int): Int {
            return when {
                channels == 1 -> if (bitsPerSample == 8) AL_FORMAT_MONO8 else AL_FORMAT_MONO16
                else -> if (bitsPerSample == 8) AL_FORMAT_STEREO8 else AL_FORMAT_STEREO16
            }
        }
    }
}