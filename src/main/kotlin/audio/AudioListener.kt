package audio

import core.math.Vector3
import org.lwjgl.BufferUtils
import org.lwjgl.openal.AL10.*

class AudioListener {
    private val position: Vector3 = Vector3(0f)
    private val forward: Vector3 = Vector3(0f)
    private val up: Vector3 = Vector3(0f, 1f, 0f)
    private val velocity: Vector3 = Vector3(0f)

    fun setPosition(x: Float, y: Float, z: Float) {
        position.x = x
        position.y = y
        position.z = z

        alListener3f(AL_POSITION, x, y, z)
        checkAudioErrors("Failed to set listener position")
    }

    fun setPosition(position: Vector3) {
        setPosition(position.x, position.y, position.z)
    }

    fun setOrientation(
        fwX: Float,
        fwY: Float,
        fwZ: Float,
        upX: Float,
        upY: Float,
        upZ: Float
    ) {
        forward.x = fwX
        forward.y = fwY
        forward.z = fwZ

        up.x = upX
        up.y = upY
        up.z = upZ

        val data = floatArrayOf(fwX, fwY, fwZ, upX, upY, upZ)
        val buffer = BufferUtils.createFloatBuffer(data.size).put(data)
        buffer.flip()

        alListenerfv(AL_ORIENTATION, buffer)
        checkAudioErrors("Failed to set listener orientation")
    }

    fun setOrientation(forward: Vector3, up: Vector3) {
        setOrientation(
            forward.x,
            forward.y,
            forward.z,
            up.x,
            up.y,
            up.z
        )
    }

    fun setVelocity(x: Float, y: Float, z: Float) {
        velocity.x = x
        velocity.y = y
        velocity.z = z

        alListener3f(AL_VELOCITY, x, y, z)
        checkAudioErrors("Failed to set listener velocity")
    }

    fun setVelocity(velocity: Vector3) {
        setVelocity(velocity.x, velocity.y, velocity.z)
    }

    fun getPosition(): Vector3 = position
    fun getOrientation(): Pair<Vector3, Vector3> = Pair(forward, up)
    fun getVelocity(): Vector3 = velocity
}