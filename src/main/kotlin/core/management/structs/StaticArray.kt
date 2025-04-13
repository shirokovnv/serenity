package core.management.structs

class StaticArray<T>(private val capacity: Int) : Iterable<T> {
    init {
        require(capacity > 0) { "Capacity must be positive" }
    }

    private val data = arrayOfNulls<Any>(capacity) as Array<T?>
    private var count: Int = 0

    fun add(element: T) {
        require(count < capacity) { "Array is full" }

        data[count++] = element
    }

    fun count(): Int = count

    fun clear() {
        count = 0
    }

    operator fun get(index: Int): T {
        if (index >= count) {
            throw IndexOutOfBoundsException()
        }

        return data[index]!!
    }

    operator fun set(index: Int, element: T) {
        if (index >= count) {
            throw IndexOutOfBoundsException()
        }

        data[index] = element
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var currentIndex = 0

        override fun hasNext(): Boolean = currentIndex < count

        override fun next(): T {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            return data[currentIndex++]!!
        }
    }

    // Extension function for forEach (optional - Iterable already provides this)
    fun forEach(action: (T) -> Unit) {
        for (i in 0..<count) {
            action(data[i]!!)
        }
    }
}