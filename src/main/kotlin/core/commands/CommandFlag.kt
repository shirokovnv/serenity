package core.commands

enum class CommandFlag(val value: Int) {
    NONE(0),
    REPEATABLE(1),
    REPEATABLE_UNLESS_SUCCEEDED(2)
}

infix fun CommandFlag.or(other: CommandFlag): Int {
    return this.value or other.value
}

infix fun CommandFlag.and(other: CommandFlag): Int {
    return this.value and other.value
}