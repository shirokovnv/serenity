package core.event

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

interface TestEvent: Event

data class TestKeyboardInputEvent(val key: Int) : TestEvent
data class TestWindowResizeEvent(val width: Int, val height: Int) : TestEvent

class Sender(val name: String)

class EventBusTest {
    @Test
    fun `should call handler for specific event type`() {
        val eventBus = EventBus()
        val sender = Sender("TestSender")
        var keyboardInputCalled = false

        eventBus.subscribe<TestKeyboardInputEvent, Sender>{ event, sender ->
            keyboardInputCalled = true
            assertEquals(123, event.key)
            assertEquals("TestSender", sender.name)
        }

        eventBus.publish(TestKeyboardInputEvent(123), sender)
        assertTrue(keyboardInputCalled, "KeyboardInputHandler should have been called")
    }

    @Test
    fun `should call handler for supertype event`() {
        val eventBus = EventBus()
        val sender = Sender("TestSender")
        var myEventCalled = false

        eventBus.subscribe<Event, Sender> { _, sender ->
            myEventCalled = true
            assertEquals("TestSender", sender.name)
        }

        eventBus.publish(TestKeyboardInputEvent(123), sender)
        assertTrue(myEventCalled, "MyEventHandler should have been called")
    }

    @Test
    fun `should not call handler for other event type`() {
        val eventBus = EventBus()
        val sender = Sender("TestSender")
        var keyboardInputCalled = false

        eventBus.subscribe<TestKeyboardInputEvent, Sender>{ _, sender ->
            keyboardInputCalled = true
        }

        eventBus.publish(TestWindowResizeEvent(100, 200), sender)
        assertFalse(keyboardInputCalled, "KeyboardInputHandler should not be called")
    }

    @Test
    fun `should call multiple handlers for single event`() {
        val eventBus = EventBus()
        val sender = Sender("TestSender")

        var keyboardInputCalled1 = false
        var keyboardInputCalled2 = false

        eventBus.subscribe<TestKeyboardInputEvent, Sender>{ _, sender ->
            keyboardInputCalled1 = true
        }

        eventBus.subscribe<TestKeyboardInputEvent, Sender> { _, sender ->
            keyboardInputCalled2 = true
        }

        eventBus.publish(TestKeyboardInputEvent(123), sender)
        assertTrue(keyboardInputCalled1, "KeyboardInputHandler1 should have been called")
        assertTrue(keyboardInputCalled2, "KeyboardInputHandler2 should have been called")
    }
}