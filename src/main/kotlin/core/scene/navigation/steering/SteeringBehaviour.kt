package core.scene.navigation.steering

import core.scene.behaviour.FrameUpdateBehaviour
import core.scene.navigation.steering.commands.SteeringCommander

abstract class SteeringBehaviour : FrameUpdateBehaviour() {
    abstract val agent: SteeringAgent

    val commander = SteeringCommander()

    override fun onUpdate(deltaTime: Float) {
        beforeApplyingSteering()

        commander.processCommands(agent)

        if (shouldApplySteering()) {
            agent.velocity = agent.velocity + agent.acceleration
            agent.position = agent.position + agent.velocity
        }

        afterApplyingSteering()
    }

    protected abstract fun shouldApplySteering(): Boolean
    protected abstract fun beforeApplyingSteering()
    protected abstract fun afterApplyingSteering()
}