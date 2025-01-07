package core.di

import kotlin.reflect.KClass

class ServiceLocator {
    private val services = mutableMapOf<KClass<*>, Any>()

    fun <Service: Any> putService(service: Service) {
        services[service::class] = service
    }

    fun <Service: Any> getService(serviceType: KClass<Service>): Service? {
        return services[serviceType] as? Service
    }

    inline fun <reified ServiceType : Any> getService(): ServiceType? {
        return getService(ServiceType::class)
    }

    fun <Service: Any> hasService(serviceType: KClass<Service>): Boolean {
        return services.contains(serviceType)
    }

    inline fun <reified ServiceType: Any> hasService(): Boolean {
        return hasService(ServiceType::class)
    }
}