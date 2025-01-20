package core.management

import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResourceManagerTest {
    private lateinit var manager: ResourceManager

    @BeforeEach
    fun setup(){
        manager = ResourceManager()
    }

    @Test
    fun `put and get single resource`() {
        val resource = "Test String"
        manager.put(resource)
        val retrievedResource = manager.get<String>()
        assertEquals(resource, retrievedResource)
    }

    @Test
    fun `put and get resource by key`() {
        val resource = 123
        manager.put(resource, "testKey")
        val retrievedResource = manager.get<Int>("testKey")
        assertEquals(resource, retrievedResource)
    }

    @Test
    fun `has single resource`() {
        val resource = 123.0
        manager.put(resource)
        assertTrue(manager.has<Double>())
    }

    @Test
    fun `has resource with key`() {
        val resource = "Test string"
        manager.put(resource, "testKey")
        assertTrue(manager.has<String>("testKey"))
    }

    @Test
    fun `get non existing single resource`() {
        val retrievedResource = manager.get<String>()
        assertNull(retrievedResource)
    }

    @Test
    fun `get non existing resource with key`() {
        val retrievedResource = manager.get<Int>("testKey")
        assertNull(retrievedResource)
    }

    @Test
    fun `put replace single resource`() {
        manager.put("old")
        val newResource = "new"
        manager.put(newResource)
        val retrievedResource = manager.get<String>()
        assertEquals(newResource, retrievedResource)
    }

    @Test
    fun `put get resource group`() {
        val resource1 = "resource1"
        val resource2 = 123
        val resource3 = 123.0f

        manager.put(resource1, "key1")
        manager.put(resource2, "key2")
        manager.put(resource3, "key1")

        assertEquals(resource1, manager.get<String>("key1"))
        assertEquals(resource2, manager.get<Int>("key2"))
        assertEquals(resource3, manager.get<Float>("key1"))
        assertNull(manager.get<String>("key2"))
    }

    @Test
    fun `put get wrong type`(){
        val resource = "test"
        manager.put(resource)
        val res = manager.get<Int>()
        assertNull(res)
    }
}