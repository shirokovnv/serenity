package core.commands

interface CommandInterface<Actor, Result: CommandResultInterface> {
    fun execute(actor: Actor): Result
}