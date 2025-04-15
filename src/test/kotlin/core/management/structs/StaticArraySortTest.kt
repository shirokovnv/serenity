package core.management.structs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StaticArraySortTest {
    @Test
    fun `should sort an empty array`() {
        val array = StaticArray<Int>(5)
        array.sort(compareBy { it })
        assertEquals(0, array.count())
    }

    @Test
    fun `should sort an array with one element`() {
        val array = StaticArray<Int>(5)
        array.add(1)
        array.sort(compareBy { it })
        assertEquals(1, array.count())
        assertEquals(1, array[0])
    }

    @Test
    fun `should sort an array with multiple elements in ascending order`() {
        val array = StaticArray<Int>(5)
        array.add(5)
        array.add(2)
        array.add(8)
        array.add(1)
        array.add(9)

        array.sort(compareBy { it })

        assertEquals(5, array.count())
        assertEquals(1, array[0])
        assertEquals(2, array[1])
        assertEquals(5, array[2])
        assertEquals(8, array[3])
        assertEquals(9, array[4])
    }

    @Test
    fun `should sort an array with multiple elements in descending order`() {
        val array = StaticArray<Int>(5)
        array.add(5)
        array.add(2)
        array.add(8)
        array.add(1)
        array.add(9)

        array.sort(compareByDescending { it })

        assertEquals(5, array.count())
        assertEquals(9, array[0])
        assertEquals(8, array[1])
        assertEquals(5, array[2])
        assertEquals(2, array[3])
        assertEquals(1, array[4])
    }

    @Test
    fun `should sort an array with duplicate elements`() {
        val array = StaticArray<Int>(5)
        array.add(5)
        array.add(2)
        array.add(5)
        array.add(1)
        array.add(2)

        array.sort(compareBy { it })

        assertEquals(5, array.count())
        assertEquals(1, array[0])
        assertEquals(2, array[1])
        assertEquals(2, array[2])
        assertEquals(5, array[3])
        assertEquals(5, array[4])
    }

    @Test
    fun `should sort an already sorted array`() {
        val array = StaticArray<Int>(5)
        array.add(1)
        array.add(2)
        array.add(3)
        array.add(4)
        array.add(5)

        array.sort(compareBy { it })

        assertEquals(5, array.count())
        assertEquals(1, array[0])
        assertEquals(2, array[1])
        assertEquals(3, array[2])
        assertEquals(4, array[3])
        assertEquals(5, array[4])
    }

    @Test
    fun `should sort a reverse sorted array`() {
        val array = StaticArray<Int>(5)
        array.add(5)
        array.add(4)
        array.add(3)
        array.add(2)
        array.add(1)

        array.sort(compareBy { it })

        assertEquals(5, array.count())
        assertEquals(1, array[0])
        assertEquals(2, array[1])
        assertEquals(3, array[2])
        assertEquals(4, array[3])
        assertEquals(5, array[4])
    }

    @Test
    fun `should sort an array with a custom comparator`() {
        val array = StaticArray<String>(3)
        array.add("apple")
        array.add("banana")
        array.add("pear")

        // Sort by length of the string
        array.sort(compareBy { it.length })

        assertEquals(3, array.count())
        assertEquals("pear", array[0])
        assertEquals("apple", array[1])
        assertEquals("banana", array[2])
    }

    @Test
    fun `should handle different data types`() {
        val array = StaticArray<Double>(3)
        array.add(3.14)
        array.add(1.618)
        array.add(2.718)

        array.sort(compareBy { it })

        assertEquals(3, array.count())
        assertEquals(1.618, array[0])
        assertEquals(2.718, array[1])
        assertEquals(3.14, array[2])
    }
}