package core.ecs

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

data class Position(var x: Float, var y: Float) : Component()
data class Velocity(var vx: Float, var vy: Float) : Component()

class EntityTest {
    @Test
    fun `test add component and get component`() {
        val entity = Entity()
        val position = Position(10f, 20f)
        entity.addComponent(position)

        val retrievedPosition = entity.getComponent<Position>()

        assertNotNull(retrievedPosition)
        assertEquals(10f, retrievedPosition!!.x)
        assertEquals(20f, retrievedPosition.y)

        val velocity = entity.getComponent(Velocity::class)

        assertNull(velocity)
    }

    @Test
    fun `test get all components`() {
        val entity = Entity()
        val position = Position(10f, 20f)
        val velocity = Velocity(1f, 2f)
        entity.addComponent(position)
        entity.addComponent(velocity)

        val allComponents = entity.getAllComponents()

        assertEquals(2, allComponents.size)
        assertTrue(allComponents.contains(position))
        assertTrue(allComponents.contains(velocity))
    }

    @Test
    fun `test has component`() {
        val entity = Entity()
        val position = Position(10f, 20f)
        entity.addComponent(position)

        assertTrue(entity.hasComponent(Position::class))
        assertTrue(entity.hasComponent(position))
        assertFalse(entity.hasComponent(Velocity::class))
        assertFalse(entity.hasComponent(Velocity(1f, 1f)))
    }

    @Test
    fun `test get component inline reified`(){
        val entity = Entity();
        val position = Position(10f, 20f)
        entity.addComponent(position)

        val retrievedPosition : Position? = entity.getComponent<Position>();
        assertNotNull(retrievedPosition)
        assertEquals(10f, retrievedPosition!!.x)
        assertEquals(20f, retrievedPosition.y)

        val velocity =  entity.getComponent<Velocity>()
        assertNull(velocity)
    }

    @Test
    fun `test has component inline reified`() {
        val entity = Entity()
        val position = Position(10f, 20f)
        entity.addComponent(position)

        assertTrue(entity.hasComponent<Position>())
        assertFalse(entity.hasComponent<Velocity>())
    }

    @Test
    fun `test remove component`() {
        val entity = Entity()
        val position = Position(10f, 20f)
        val velocity = Velocity(1f, 2f)
        entity.addComponent(position)
        entity.addComponent(velocity)

        entity.removeComponent(Position::class)

        assertNull(entity.getComponent(Position::class))
        assertNotNull(entity.getComponent(Velocity::class))
        assertEquals(1, entity.getAllComponents().size)
    }

    @Test
    fun `test remove component inline reified`() {
        val entity = Entity()
        val position = Position(10f, 20f)
        val velocity = Velocity(1f, 2f)
        entity.addComponent(position)
        entity.addComponent(velocity)

        entity.removeComponent<Position>()

        assertNull(entity.getComponent(Position::class))
        assertNotNull(entity.getComponent(Velocity::class))
        assertEquals(1, entity.getAllComponents().size)
    }

    @Test
    fun `test get multiple components`(){
        val entity = Entity();
        val position1 = Position(10f, 10f);
        val position2 = Position(20f, 20f);

        entity.addComponent(position1);
        entity.addComponent(position2);

        val positions  = entity.getComponents(Position::class);
        assertEquals(2, positions.size);
        assertTrue(positions.contains(position1));
        assertTrue(positions.contains(position2));
    }
}