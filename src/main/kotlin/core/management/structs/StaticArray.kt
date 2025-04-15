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

    fun sort(comparator: Comparator<T>) {
        quickSort(0, count - 1, comparator)
    }

    private fun quickSort(low: Int, high: Int, comparator: Comparator<T>) {
        if (low < high) {
            val partitionIndex = partition(low, high, comparator)

            quickSort(low, partitionIndex - 1, comparator)
            quickSort(partitionIndex + 1, high, comparator)
        }
    }

    private fun partition(low: Int, high: Int, comparator: Comparator<T>): Int {
        val pivot = data[high]!!
        var i = (low - 1)

        for (j in low..<high) {
            if (comparator.compare(data[j]!!, pivot) < 0) {
                i++

                // Swap data[i] and data[j]
                val temp = data[i]
                data[i] = data[j]
                data[j] = temp
            }
        }

        // Swap data[i+1] and data[high] (or pivot)
        val temp = data[i + 1]
        data[i + 1] = data[high]
        data[high] = temp
        return i + 1
    }
}