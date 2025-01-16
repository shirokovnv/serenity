package modules.light

import core.ecs.Behaviour
import core.scene.Object

class AtmosphereController: Behaviour() {
    private lateinit var atmosphereManager: AtmosphereManager
    private lateinit var atmosphereSsbo: AtmosphereConstantsSsbo

    override fun create() {
        atmosphereManager = AtmosphereManager()
        atmosphereSsbo = AtmosphereConstantsSsbo(atmosphereManager.getShaderParams())

        Object.services.putService<AtmosphereConstantsSsbo>(atmosphereSsbo)
    }

    override fun update(deltaTime: Float) {
        if (atmosphereManager.isChanged()) {
            atmosphereSsbo.updateAtmosphereConstants(atmosphereManager.getShaderParams())
        }
    }

    override fun destroy() {
    }
}