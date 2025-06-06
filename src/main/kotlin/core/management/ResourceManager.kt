package core.management

import graphics.assets.Asset
import kotlin.reflect.KClass

open class ResourceManager {
    protected val resources = mutableMapOf<KClass<*>, Any>()
    protected val resourceGroups = mutableMapOf<KClass<*>, MutableMap<String, Any>>()

    fun <Resource: Any> put(resource: Resource, resourceType: KClass<Resource>, key: String) {
        val serviceMap = resourceGroups.computeIfAbsent(resourceType) { mutableMapOf() }
        serviceMap[key] = resource
    }

    inline fun <reified Resource: Any> put(resource: Resource, key: String) {
        put(resource, Resource::class, key)
    }

    fun <Resource: Any> get(resourceType: KClass<Resource>, key: String): Resource? {
        return resourceGroups[resourceType]?.get(key) as? Resource
    }

    inline fun <reified ResourceType: Any> get(key: String): ResourceType? {
        return get(ResourceType::class, key)
    }

    fun <Resource: Any> has(resourceType: KClass<Resource>, key: String): Boolean {
        return resourceGroups[resourceType]?.containsKey(key) ?: false
    }

    inline fun <reified ResourceType: Any> has(key: String): Boolean {
        return has(ResourceType::class, key)
    }

    fun <Resource: Any> put(resource: Resource, resourceType: KClass<Resource>) {
        resources[resourceType] = resource
    }

    inline fun <reified Resource: Any> put(resource: Resource){
        put(resource, Resource::class)
    }

    fun <Resource: Any> get(resourceType: KClass<Resource>): Resource? {
        return resources[resourceType] as? Resource
    }

    inline fun <reified ResourceType : Any> get(): ResourceType? {
        return get(ResourceType::class)
    }

    fun <Resource: Any> has(resourceType: KClass<Resource>): Boolean {
        return resources.containsKey(resourceType)
    }

    inline fun <reified ResourceType: Any> has(): Boolean {
        return has(ResourceType::class)
    }
}

object Resources: ResourceManager(), Disposable {
    override fun dispose() {
        resources.values.forEach(::disposeResource)
        resourceGroups.values.forEach { group ->
            group.values.forEach(::disposeResource)
        }
    }

    private fun disposeResource(resource: Any) {
        if (resource is Asset) {
            resource.destroy()
        }

        if (resource is Disposable) {
            resource.dispose()
        }
    }
}