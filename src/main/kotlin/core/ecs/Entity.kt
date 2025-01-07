package core.ecs

import kotlin.reflect.KClass

open class Entity {
    val id: String = java.util.UUID.randomUUID().toString()
    private val components = mutableListOf<Component>()

    fun <T : Component> addComponent(component: T) {
        components.add(component)
        component.setOwner(this)
    }

    fun <T : Component> getComponent(componentType: KClass<T>): T? {
        return components.filterIsInstance(componentType.java).firstOrNull()
    }

    fun <T : Component> getComponents(componentType: KClass<T>): List<T> {
        return components.filterIsInstance(componentType.java)
    }

    fun getAllComponents(): List<Component> {
        return components
    }

    inline fun <reified T : Component> getComponent(): T? {
        return getComponent(T::class)
    }

    inline fun <reified T : Component> getComponents(): List<T> {
        return getComponents(T::class)
    }

    inline fun <reified T : Component> hasComponent(): Boolean {
        return hasComponent(T::class)
    }

    fun <T : Component> hasComponent(component: T): Boolean {
        return components.any { it == component }
    }

    fun <T : Component> hasComponent(componentType: KClass<T>): Boolean {
        return components.any { it::class == componentType }
    }

    fun <T : Component> removeComponent(componentType: KClass<T>) {
        components.removeIf {
            if (it::class == componentType) {
                it.setOwner(null)
                true
            } else {
                false
            }
        }
    }

    fun <T : Component> removeComponent(component: T) {
        components.removeIf {
            if (it == component) {
                it.setOwner(null)
                true
            } else {
               false
            }
        }
    }

    inline fun <reified T : Component> removeComponent() {
        removeComponent(T::class)
    }
}