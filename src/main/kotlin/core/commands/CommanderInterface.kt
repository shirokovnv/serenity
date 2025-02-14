package core.commands

interface CommanderInterface<Actor, Result : CommandResultInterface> {
    val commands: List<CommandInterface<Actor, Result>>

    fun addCommand(command: CommandInterface<Actor, Result>)
    fun removeCommand(command: CommandInterface<Actor, Result>)
    fun processCommands(actor: Actor)
    fun clearCommands()
}