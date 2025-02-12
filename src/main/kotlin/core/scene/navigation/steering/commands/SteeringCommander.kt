package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.commands.CommandInterface
import core.commands.CommanderInterface
import core.math.Vector3
import core.math.truncate
import core.scene.navigation.steering.SteeringAgent
import java.util.concurrent.PriorityBlockingQueue

class SteeringCommander(
    initialCommands: List<SteeringCommand> = emptyList()
) : CommanderInterface<SteeringAgent, SteeringCommandResult> {
    private val commandQueue = PriorityBlockingQueue(12, SteeringCommandComparator())
    private val commandListLock = Object()

    override val commands: List<CommandInterface<SteeringAgent, SteeringCommandResult>>
        get() = synchronized(commandListLock) {
            commandQueue.toList()
        }

    init {
        commandQueue.addAll(initialCommands)
    }

    override fun addCommand(command: CommandInterface<SteeringAgent, SteeringCommandResult>) {
        commandQueue.put(command as SteeringCommand)
    }

    override fun removeCommand(command: CommandInterface<SteeringAgent, SteeringCommandResult>) {
        commandQueue.remove(command)
    }

    override fun processCommands(actor: SteeringAgent) {
        var acceleration = Vector3(0f)

        val repeatableCommands = mutableListOf<SteeringCommand>()
        while (true) {
            val command = commandQueue.poll() ?: break
            val result = command.execute(actor)

            if (result.steeringForce.x.isNaN()
                || result.steeringForce.y.isNaN()
                || result.steeringForce.z.isNaN()
            ) {
                throw IllegalStateException("Illegal force: $command ${result.steeringForce}")
            }

            acceleration = acceleration + result.steeringForce

            if (command.hasFlag(CommandFlag.REPEATABLE) ||
                (command.hasFlag(CommandFlag.REPEATABLE_UNLESS_SUCCEEDED) && !result.isSuccessful)
            ) {
                repeatableCommands.add(command)
            }
        }
        repeatableCommands.forEach { commandQueue.put(it) }

        actor.velocity = actor.velocity.truncate(actor.maxSpeed)
        actor.acceleration = acceleration.truncate(actor.maxForce)
    }

    override fun clearCommands() {
        commandQueue.clear()
    }
}