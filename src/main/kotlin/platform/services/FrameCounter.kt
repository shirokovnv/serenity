package platform.services

import core.scene.Timer
import platform.Constants.NANOSECOND
import kotlin.properties.Delegates

class FrameCounter(private var frameRate: Float) : Timer {
    private var frames by Delegates.notNull<Int>()
    private var frameCounter by Delegates.notNull<Long>()
    private var lastTime by Delegates.notNull<Long>()
    private var passedTime by Delegates.notNull<Long>()
    private var unprocessedTime by Delegates.notNull<Double>()

    private var currentFrameTime by Delegates.notNull<Float>()
    private var fps by Delegates.notNull<Int>()
    private var currentFrame by Delegates.notNull<Int>()

    init {
        reset()
    }

    val frameTime: Float
        get() = 1.0f / frameRate

    fun reset() {
        frames = 0
        frameCounter = 0
        lastTime = System.nanoTime()
        unprocessedTime = 0.0

        currentFrameTime = 0.0f
        fps = 0
        currentFrame = 0
    }

    fun begin() {
        val startTime = System.nanoTime()
        passedTime = startTime - lastTime
        lastTime = startTime
        unprocessedTime += passedTime / NANOSECOND.toDouble()
        frameCounter += passedTime
    }

    fun canRenderFrame(): Boolean {
        return unprocessedTime > frameTime
    }

    fun processFrame() {
        unprocessedTime -= frameTime.toDouble()
    }

    fun updateFrameFps() {
        if (frameCounter >= NANOSECOND) {
            fps = frames
            currentFrameTime = 1.0f / fps
            frames = 0
            frameCounter = 0
        }
    }

    fun incrementFrames() {
        frames++
        currentFrame = (currentFrame + 1) % frameRate()
    }

    fun fps(): Int = fps

    fun frame(): Int = currentFrame
    fun frameRate(): Int = frameRate.toInt()

    override fun deltaTime(): Float {
        return (passedTime / NANOSECOND.toDouble()).toFloat()
    }
}