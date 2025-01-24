package modules.light

import core.ecs.Behaviour
import core.management.Resources

class AtmosphereController: Behaviour() {
    private lateinit var atmosphereManager: AtmosphereManager
    private lateinit var atmosphereSsbo: AtmosphereConstantsSsbo

    override fun create() {
        atmosphereManager = AtmosphereManager()
        atmosphereSsbo = AtmosphereConstantsSsbo(atmosphereManager.getShaderParams())

        Resources.put<AtmosphereConstantsSsbo>(atmosphereSsbo)
    }

    override fun update(deltaTime: Float) {
        if (atmosphereManager.isChanged()) {
            atmosphereSsbo.updateAtmosphereConstants(atmosphereManager.getShaderParams())
        }
    }

    override fun destroy() {
        atmosphereSsbo.destroy()
    }
}