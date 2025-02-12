package modules.terrain.navigation

import core.events.Events
import core.management.Resources
import core.math.Sphere
import core.math.Vector2
import core.math.Vector3
import core.scene.camera.Camera
import core.scene.camera.PerspectiveCamera
import core.scene.navigation.*
import core.scene.navigation.steering.SteeringBehaviour
import core.scene.navigation.steering.commands.PathFollowCommand
import core.scene.navigation.steering.commands.SteeringCommand
import core.scene.raytracing.RayData
import core.scene.raytracing.RayTracer
import graphics.rendering.Colors
import graphics.rendering.Renderer
import graphics.rendering.gizmos.RayDrawer
import graphics.rendering.gizmos.SphereDrawer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.terrain.heightmap.Heightmap
import modules.terrain.heightmap.binarySearch
import org.lwjgl.glfw.GLFW
import platform.services.input.MouseButtonPressedEvent
import platform.services.input.MouseInput

class TerrainAgentBehaviour(
    override val agent: TerrainAgent,
    private val heightmap: Heightmap,
    private val navGrid: NavGrid,
    private val navigator: NavigatorInterface,
    private val navRequestExecutor: NavRequestExecutor,
    private val initialCommands: List<SteeringCommand> = emptyList()
) : SteeringBehaviour(), Renderer {

    private lateinit var sphereDrawer: SphereDrawer
    private lateinit var rayDrawer: RayDrawer
    private lateinit var rayTracer: RayTracer

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val mouseInput: MouseInput
        get() = Resources.get<MouseInput>()!!

    private val sphereProvider: MutableList<Sphere>
        get() {
            return mutableListOf(Sphere(agent.position, agent.radius))
        }

    private val raysProvider: MutableList<RayData>
        get() {
            val rayOrigin = agent.position
            val rayDirection = Vector3(agent.velocity).normalize()
            return mutableListOf(
                RayData(rayOrigin, rayDirection, 20f, System.currentTimeMillis())
            )
        }

    override fun create() {
        initialCommands.forEach { command ->
            commander.addCommand(command)
        }

        rayTracer = RayTracer(camera as PerspectiveCamera)
        sphereDrawer = SphereDrawer(camera, { sphereProvider }, Colors.Red)
        rayDrawer = RayDrawer(camera, { raysProvider })
        owner()?.addComponent(sphereDrawer)
        owner()?.addComponent(rayDrawer)

        Events.subscribe<MouseButtonPressedEvent, Any>(::onMouseButtonPressed)
    }

    override fun destroy() {
        Events.unsubscribe<MouseButtonPressedEvent, Any>(::onMouseButtonPressed)

        commander.clearCommands()
        sphereDrawer.dispose()
        rayDrawer.dispose()
    }

    override fun shouldApplySteering(): Boolean {
        return true
//        return navigator.evaluatePoint(agent.position + agent.velocity, agent)
    }

    override fun beforeApplyingSteering() {
        agent.velocity.y = 0f
    }

    override fun afterApplyingSteering() {
        agent.velocity.y = 0f
        // restore heightmap position
        val position = agent.position
        position.y = heightmap.getInterpolatedHeight(position.x, position.z) * heightmap.worldScale().y
        agent.transform().setTranslation(position)

        navGrid.remove(agent)
        agent.recalculateBounds()
        navGrid.insert(agent)
    }

    override fun render(pass: RenderPass) {
        if (::sphereDrawer.isInitialized) sphereDrawer.draw()
        if (::rayDrawer.isInitialized) rayDrawer.draw()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }

    private fun onMouseButtonPressed(event: MouseButtonPressedEvent, sender: Any) {
        if (event.button == GLFW.GLFW_MOUSE_BUTTON_2) {
            val ray = rayTracer.castRayInWorldSpace(mouseInput.lastX().toFloat(), mouseInput.lastY().toFloat())
            val pointOnTerrain =
                binarySearch(heightmap, camera.position(), ray, Vector2(0f, 2500.0f))

            if (pointOnTerrain != null && navigator.evaluatePoint(pointOnTerrain, agent)) {
                val request = NavRequest(agent.position, pointOnTerrain, agent, ::onNavResponseCompletedCallback)
                navRequestExecutor.execute(request)
            }
        }
    }

    private fun onNavResponseCompletedCallback(response: NavResponse) {
        val path = response.path
        val prevPathFollowCommand =
            commander
                .commands
                .filterIsInstance<PathFollowCommand>()
                .firstOrNull()

        if (path != null && path.isValid()) {
            if (prevPathFollowCommand != null) {
                commander.removeCommand(prevPathFollowCommand)
            }

            commander.addCommand(
                PathFollowCommand(path)
            )
        }
    }
}