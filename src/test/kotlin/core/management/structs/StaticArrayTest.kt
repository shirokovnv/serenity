package core.management.structs

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class StaticArrayTest {

    @Test
    fun `test constructor throws exception with non-positive capacity`() {
        assertThrows<IllegalArgumentException> { StaticArray<Int>(0) }
        assertThrows<IllegalArgumentException> { StaticArray<String>(-5) }
    }

    @Test
    fun `test add and count`() {
        val array = StaticArray<Int>(3)
        assertEquals(0, array.count())

        array.add(10)
        assertEquals(1, array.count())

        array.add(20)
        array.add(30)
        assertEquals(3, array.count())

        assertThrows<IllegalArgumentException> { array.add(40) } // Array is full
    }

    @Test
    fun `test get`() {
        val array = StaticArray<String>(3)
        array.add("A")
        array.add("B")
        array.add("C")

        assertEquals("A", array[0])
        assertEquals("B", array[1])
        assertEquals("C", array[2])

        assertThrows<IndexOutOfBoundsException> { array[-1] }
        assertThrows<IndexOutOfBoundsException> { array[3] }
    }

    @Test
    fun `test set`() {
        val array = StaticArray<Int>(3)
        array.add(1)
        array.add(2)
        array.add(3)

        array[1] = 100
        assertEquals(100, array[1])

        assertThrows<IndexOutOfBoundsException> { array[-1] = 5 }
        assertThrows<IndexOutOfBoundsException> { array[3] = 5 }
    }

    @Test
    fun `test clear`() {
        val array = StaticArray<Double>(5)
        array.add(1.1)
        array.add(2.2)
        array.add(3.3)

        array.clear()
        assertEquals(0, array.count())

        // Check that you can still add elements after clearing
        array.add(4.4)
        assertEquals(1, array.count())
        assertEquals(4.4, array[0])
    }

    @Test
    fun `test iterator`() {
        val array = StaticArray<Char>(4)
        array.add('a')
        array.add('b')
        array.add('c')

        val iterator = array.iterator()
        assertTrue(iterator.hasNext())
        assertEquals('a', iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals('b', iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals('c', iterator.next())
        assertFalse(iterator.hasNext())
        assertThrows<NoSuchElementException> { iterator.next() }
    }

    @Test
    fun `test forEach`() {
        val array = StaticArray<String>(3)
        array.add("X")
        array.add("Y")
        array.add("Z")

        val result = mutableListOf<String>()
        array.forEach { element -> result.add(element) }

        assertEquals(listOf("X", "Y", "Z"), result)
    }

    @Test
    fun `test add more than capacity throws exception`() {
        val array = StaticArray<Int>(2)
        array.add(1)
        array.add(2)
        assertThrows<IllegalArgumentException> { array.add(3) }
    }

    @Test
    fun `test get with index out of bounds throws exception`() {
        val array = StaticArray<Int>(2)
        array.add(1)
        assertThrows<IndexOutOfBoundsException> { array[2] }
        assertThrows<IndexOutOfBoundsException> { array[-1] }
    }

    @Test
    fun `test get empty array`() {
        val array = StaticArray<Int>(2)
        assertThrows<IndexOutOfBoundsException> { array[0] }
    }
}