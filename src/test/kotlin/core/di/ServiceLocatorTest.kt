package core.di

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

interface Logger {
    fun log(message: String)
}

class ConsoleLogger : Logger {
    override fun log(message: String) {
        println("Log: $message")
    }
}

interface ApiClient{
    fun getData() : String
}

class MockApiClient: ApiClient{
    override fun getData(): String {
        return "Some data from API"
    }
}

class ServiceLocatorTest {
    @Test
    fun `test putService and getService with correct type`() {
        val locator = ServiceLocator()
        val logger = ConsoleLogger()
        locator.putService<Logger>(logger)

        val retrievedLogger: Logger? = locator.getService()
        assertEquals(logger, retrievedLogger)
    }

    @Test
    fun `test putService and getService with incorrect type`() {
        val locator = ServiceLocator()
        val logger = ConsoleLogger()
        locator.putService(logger)

        val apiClient: ApiClient? = locator.getService()
        assertNull(apiClient)
    }

    @Test
    fun `test putService and getService with KClass`() {
        val locator = ServiceLocator()
        val logger = ConsoleLogger()
        locator.putService<Logger>(logger)

        val retrievedLogger = locator.getService(Logger::class)
        assertEquals(logger, retrievedLogger)
    }

    @Test
    fun `test putService and getService with incorrect KClass`() {
        val locator = ServiceLocator()
        val logger = ConsoleLogger()
        locator.putService(logger)

        val apiClient = locator.getService(ApiClient::class)
        assertNull(apiClient)
    }

    @Test
    fun `test hasService with correct type`() {
        val locator = ServiceLocator()
        val logger = ConsoleLogger()
        locator.putService<Logger>(logger)

        val hasLogger = locator.hasService<Logger>()
        assertTrue(hasLogger)
    }

    @Test
    fun `test hasService with incorrect type`() {
        val locator = ServiceLocator()
        val logger = ConsoleLogger()
        locator.putService(logger)

        val hasApi = locator.hasService<ApiClient>()
        assertFalse(hasApi)
    }

    @Test
    fun `test hasService with correct KClass`() {
        val locator = ServiceLocator()
        val logger = ConsoleLogger()
        locator.putService<Logger>(logger)

        val hasLogger = locator.hasService(Logger::class)
        assertTrue(hasLogger)
    }

    @Test
    fun `test hasService with incorrect KClass`() {
        val locator = ServiceLocator()
        val logger = ConsoleLogger()
        locator.putService(logger)

        val hasApi = locator.hasService(ApiClient::class)
        assertFalse(hasApi)
    }

    @Test
    fun `test getService returns null when service is not registered`() {
        val locator = ServiceLocator()
        val apiClient: ApiClient? = locator.getService()
        assertNull(apiClient)
    }

    @Test
    fun `test getService with KClass returns null when service is not registered`(){
        val locator = ServiceLocator()
        val apiClient = locator.getService(ApiClient::class)
        assertNull(apiClient)
    }

    @Test
    fun `test can register multiple services`(){
        val locator = ServiceLocator()

        val consoleLogger = ConsoleLogger()
        val mockApiClient = MockApiClient()

        locator.putService<Logger>(consoleLogger)
        locator.putService<ApiClient>(mockApiClient)

        val logger: Logger? = locator.getService<Logger>()
        val apiClient : ApiClient? = locator.getService()

        assertEquals(consoleLogger, logger)
        assertEquals(mockApiClient, apiClient)
    }
}