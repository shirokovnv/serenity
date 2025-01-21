package core.event

import kotlin.reflect.KClass

interface Event

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
}

object Events: EventBus()