package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.commands.CommandInterface
import core.scene.navigation.steering.SteeringAgent

abstract class SteeringCommand : CommandInterface<SteeringAgent, SteeringCommandResult> {
    abstract var priority: Int
    abstract var weight: Float
    abstract var flags: Int

    fun withFlag(flag: CommandFlag): SteeringCommand {
        flags = flags or flag.value
        return this
    }

    fun withWeight(weight: Float): SteeringCommand {
        this.weight = weight
        return this
    }

    fun withPriority(priority: Int): SteeringCommand {
        this.priority = priority
        return this
    }

    fun hasFlag(flag: CommandFlag): Boolean {
        return flags and flag.value != 0
    }
}