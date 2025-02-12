package core.scene.behaviour

import core.ecs.Behaviour
import core.management.Resources
import platform.services.FrameCounter

abstract class FrameUpdateBehaviour: Behaviour() {
    private val frameCounter: FrameCounter?
        get() = Resources.get<FrameCounter>()

    private var currentFrame: Int = 0

    override fun update(deltaTime: Float) {
        frameCounter?.let {
            if (it.frame() == currentFrame) {
                onUpdate(deltaTime)
                currentFrame = (currentFrame + 1) % it.frameRate()
            }
        }
    }

    abstract fun onUpdate(deltaTime: Float)
}