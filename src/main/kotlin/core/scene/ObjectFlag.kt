package core.scene

class ObjectFlag private constructor(val value: Long, val name: String) {
    companion object {
        private const val MAX_BIT_POSITION = 64
        private var nextBitPosition = -1
        private val flags = mutableListOf<ObjectFlag>()

        val None = createFlag("NONE")
        val Active = createFlag("ACTIVE")

        fun getAllFlags(): List<ObjectFlag> {
            return flags.toMutableList()
        }

        fun getFlagByName(name: String): ObjectFlag? {
            return flags.firstOrNull { it.name == name }
        }

        fun createFlag(name: String): ObjectFlag {
            if (nextBitPosition >= MAX_BIT_POSITION) {
                throw IllegalStateException("Maximum number of flags exceeded ($MAX_BIT_POSITION).")
            }

            val value = if (nextBitPosition == -1) 0 else 1L shl nextBitPosition
            val flag = ObjectFlag(value, name)
            flags.add(++nextBitPosition, flag)

            return flag
        }

        infix fun ObjectFlag.or(other: ObjectFlag): ObjectFlag {
            val newValue = this.value or other.value
            return ObjectFlag(newValue, "${this.name} or ${other.name}")
        }

        infix fun ObjectFlag.and(other: ObjectFlag): ObjectFlag {
            val newValue = this.value and other.value
            return ObjectFlag(newValue, "${this.name} and ${other.name}")
        }

        fun ObjectFlag.inv(): ObjectFlag {
            val newValue = this.value.inv()
            return ObjectFlag(newValue, "inv ${this.name}")
        }
    }

    override fun toString(): String {
        return "ObjectFlag(value=$value, name=$name)"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ObjectFlag) {
            return false
        }

        return this.value == other.value
    }

    override fun hashCode(): Int {
        return 31 * value.hashCode()
    }
}