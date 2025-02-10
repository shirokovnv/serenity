package modules.terrain.navigation

import core.management.Resources
import core.math.Sphere
import core.scene.camera.Camera
import core.scene.navigation.NavGrid
import core.scene.navigation.NavigatorInterface
import core.scene.navigation.steering.SteeringBehaviour
import core.scene.navigation.steering.commands.SteeringCommand
import graphics.rendering.Colors
import graphics.rendering.Renderer
import graphics.rendering.gizmos.SphereDrawer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass

class TerrainAgentBehaviour(
    override val agent: TerrainAgent,
    private val navGrid: NavGrid,
    private val navigator: NavigatorInterface,
    private val initialCommands: List<SteeringCommand> = emptyList()
) : SteeringBehaviour(), Renderer {

    private lateinit var sphereDrawer: SphereDrawer

    private val camera: Camera
        get() = Resources.get<Camera>()!!


    private val sphereProvider: MutableList<Sphere>
        get() {
            return mutableListOf(Sphere(agent.position, agent.radius))
        }

    override fun create() {
        initialCommands.forEach { command ->
            commander.addCommand(command)
        }

        sphereDrawer = SphereDrawer(camera, { sphereProvider }, Colors.Red)
        owner()?.addComponent(sphereDrawer)
    }

    override fun destroy() {
        commander.clearCommands()
        sphereDrawer.dispose()
    }

    override fun shouldApplySteering(): Boolean {
        return navigator.evaluatePoint(agent.position + agent.velocity, agent)
    }

    override fun beforeApplyingSteering() {

    }

    override fun afterApplyingSteering() {
        navGrid.remove(agent)
        agent.recalculateBounds()
        navGrid.insert(agent)
    }

    override fun render(pass: RenderPass) {
        if (::sphereDrawer.isInitialized) sphereDrawer.draw()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}