package core.events

import kotlin.reflect.KClass

typealias EventHandler<T, S> = (T, S) -> Unit

open class EventBus {
    private val handlers = mutableMapOf<KClass<out Event>, MutableList<EventHandler<in Event, Any>>>()

    fun <T : Event, S> subscribe(eventType: KClass<T>, handler: EventHandler<T, S>) {
        val list = handlers.getOrPut(eventType) { mutableListOf() }
        list.add(handler as EventHandler<in Event, Any>)
    }

    inline fun <reified T : Event, reified S> subscribe(noinline handler: EventHandler<T, S>) {
        val eventType = T::class
        subscribe(eventType, handler)
    }

    fun <T : Event, S> publish(event: T, sender: S) {
        val eventType = event::class.java
        handlers.forEach { (eventKType, listOfHandlers) ->
            if (eventKType.java.isAssignableFrom(eventType)) {
                listOfHandlers.forEach { (it as EventHandler<T, S>).invoke(event, sender) }
            }
        }
    }

    fun <T : Event, S> unsubscribe(eventType: KClass<T>, handler: EventHandler<T, S>) {
        handlers[eventType]?.removeIf { it == handler }
    }

    inline fun <reified T : Event, reified S> unsubscribe(noinline handler: EventHandler<T, S>) {
        val eventType = T::class
        unsubscribe(eventType, handler)
    }
}

object Events: EventBus()