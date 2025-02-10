package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.commands.CommandInterface
import core.commands.CommanderInterface
import core.math.Vector3
import core.math.truncate
import core.scene.navigation.steering.SteeringAgent
import java.util.PriorityQueue

class SteeringCommander(
    initialCommands: List<SteeringCommand> = emptyList()
) : CommanderInterface<SteeringAgent, SteeringCommandResult> {
    private val commandQueue = PriorityQueue(SteeringCommandComparator())

    override val commands: List<CommandInterface<SteeringAgent, SteeringCommandResult>>
        get() = commandQueue.toList()

    init {
        commandQueue.addAll(initialCommands)
    }

    override fun addCommand(command: CommandInterface<SteeringAgent, SteeringCommandResult>) {
        commandQueue.add(command as SteeringCommand)
    }

    override fun removeCommand(command: CommandInterface<SteeringAgent, SteeringCommandResult>) {
        commandQueue.remove(command)
    }

    override fun processCommands(actor: SteeringAgent) {
        var acceleration = Vector3(0f)

        val repeatableCommands = mutableListOf<SteeringCommand>()
        while (commandQueue.isNotEmpty()) {
            val command = commandQueue.remove()
            val result = command.execute(actor)

            acceleration = acceleration + result.steeringForce

            if (command.hasFlag(CommandFlag.REPEATABLE) ||
                (command.hasFlag(CommandFlag.REPEATABLE_UNLESS_SUCCEEDED) && !result.isSuccessful)
            ) {
                repeatableCommands.add(command)
            }
        }
        commandQueue.addAll(repeatableCommands)

        actor.velocity = actor.velocity.truncate(actor.maxSpeed)
        actor.acceleration = acceleration.truncate(actor.maxForce)
    }

    override fun clearCommands() {
        commandQueue.clear()
    }
}