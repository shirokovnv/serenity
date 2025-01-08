package core.ecs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class BaseComponentTest {
    @Test
    fun testInitialState() {
        val component = BaseComponent()
        assertNull(component.owner())
        assertTrue(component.isActive())
    }

    @Test
    fun testSetOwner() {
        val component = BaseComponent()
        val owner = Entity()

        component.setOwner(owner)
        assertEquals(owner, component.owner())

        component.setOwner(null)
        assertNull(component.owner())
    }

    @Test
    fun testSetActive() {
        val component = BaseComponent()

        component.setActive(false)
        assertFalse(component.isActive())

        component.setActive(true)
        assertTrue(component.isActive())
    }
}