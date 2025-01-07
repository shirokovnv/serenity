package core.di

import kotlin.reflect.KClass

class ServiceLocator {
    private val services = mutableMapOf<KClass<*>, Any>()

    fun <Service: Any> putService(service: Service, serviceType: KClass<Service>) {
        services[serviceType] = service
    }

    inline fun <reified Service: Any> putService(service: Service){
        putService(service, Service::class)
    }

    fun <Service: Any> getService(serviceType: KClass<Service>): Service? {
        return services[serviceType] as? Service
    }

    inline fun <reified ServiceType : Any> getService(): ServiceType? {
        return getService(ServiceType::class)
    }

    fun <Service: Any> hasService(serviceType: KClass<Service>): Boolean {
        return services.containsKey(serviceType)
    }

    inline fun <reified ServiceType: Any> hasService(): Boolean {
        return hasService(ServiceType::class)
    }
}