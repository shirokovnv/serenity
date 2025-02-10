package modules.terrain.navigation

import core.ecs.Behaviour
import core.events.Events
import core.management.Resources
import core.math.Sphere
import core.math.Vector2
import core.math.Vector3
import core.scene.Object
import core.scene.TraversalOrder
import core.scene.camera.Camera
import core.scene.camera.PerspectiveCamera
import core.scene.navigation.NavRequest
import core.scene.navigation.NavRequestExecutor
import core.scene.navigation.NavResponse
import core.scene.navigation.obstacles.NavMeshObstacle
import core.scene.navigation.path.PathNode
import core.scene.navigation.steering.SteeringBehaviour
import core.scene.raytracing.RayData
import core.scene.raytracing.RayTracer
import core.scene.traverse
import graphics.rendering.Colors
import graphics.rendering.Renderer
import graphics.rendering.gizmos.DrawGizmosEvent
import graphics.rendering.gizmos.RayDrawer
import graphics.rendering.gizmos.SphereDrawer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.terrain.heightmap.Heightmap
import modules.terrain.heightmap.PoissonDiscSampler
import modules.terrain.heightmap.PoissonDiscSamplerParams
import modules.terrain.heightmap.binarySearch
import org.lwjgl.glfw.GLFW
import platform.services.input.MouseButtonPressedEvent
import platform.services.input.MouseInput
import java.util.Collections
import kotlin.math.PI
import kotlin.math.cos
import kotlin.random.Random

class TerrainNavMeshBehaviour(
    private val heightmap: Heightmap,
    private val camera: Camera,
    private val gridSize: Float,
    private val maxSlope: Float
) : Behaviour(), Renderer {
    companion object {
        private const val RAY_LENGTH = 2500.0f
        private const val V_OFFSET = 3f
    }

    private lateinit var rayTracer: RayTracer
    private lateinit var rayDrawer: RayDrawer
    private lateinit var sphereDrawer: SphereDrawer
    private lateinit var navMesh: TerrainNavMesh
    private lateinit var navigator: TerrainNavigator
    private lateinit var navRequestExecutor: NavRequestExecutor

    private val mouseInput: MouseInput
        get() = Resources.get<MouseInput>()!!

    // TODO: replace with real object
    private val agent = TerrainNavMeshAgent(Vector3(50f, 0f, 50f))
    private val targets = mutableListOf<Vector3>()
    private var targetPath: MutableList<PathNode> = Collections.synchronizedList(mutableListOf())

    private var agents = mutableListOf<SteeringBehaviour>()

    private val raysProvider: MutableList<RayData>
        get() {
            val rays = mutableListOf<RayData>()
            for (i in 0..<targetPath.size - 1) {
                val currentPoint = targetPath[i].point
                val nextPoint = targetPath[i + 1].point
                val rayDirection = (nextPoint - currentPoint).normalize()
                val rayLength = (nextPoint - currentPoint).length()

                rays.add(
                    RayData(
                        Vector3(currentPoint.x, currentPoint.y + V_OFFSET, currentPoint.z),
                        rayDirection,
                        rayLength,
                        System.currentTimeMillis()
                    )
                )
            }

            return rays
        }

    private val sphereProvider: MutableList<Sphere>
        get() {
            return targetPath.map {
                Sphere(Vector3(it.point.x, it.point.y + V_OFFSET, it.point.z), 1f)
            }.toMutableList()
        }

    override fun create() {
        rayTracer = RayTracer(camera as PerspectiveCamera)
        rayDrawer = RayDrawer(camera, { raysProvider }, Colors.Cyan)
        sphereDrawer = SphereDrawer(camera, { sphereProvider }, Colors.Red)

        navMesh = TerrainNavMesh(heightmap, gridSize, maxSlope, collectObstacles())
        navMesh.bake()
        navigator = TerrainNavigator(heightmap, navMesh.grid())
        navRequestExecutor = NavRequestExecutor(navigator)

        owner()!!.addComponent(TerrainNavMeshDrawer(navMesh) { camera.viewProjection })
        owner()!!.addComponent(TerrainNavMeshGui(agent))

        Events.subscribe<MouseButtonPressedEvent, Any>(::onMouseButtonPressed)
        Events.subscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        val sampler = PoissonDiscSampler()
        val positions = sampler.generatePoints(heightmap,
            PoissonDiscSamplerParams(
                50f,
                Vector2(500f, 500f),
                30,
                0.0f,
                1.0f,
                0.0f
            )
            )

        println("NUM AGENT POSITIONS: ${positions.size}")

        val numAgents = positions.size
        for (i in 0..<numAgents) {
            val x = Random.nextFloat() * 2 * PI.toFloat()
            val z = Random.nextFloat() * 2 * PI.toFloat()

            val px = positions[i].x
            val pz = positions[i].y
            val py = heightmap.getInterpolatedHeight(px, pz) * heightmap.worldScale().y

            val tmpAgent = TerrainNavMeshAgent(Vector3(px, py, pz))

            val velocity = Vector3(cos(x), 0f, cos(z))
            val sagent = SteeringBehaviour(tmpAgent, heightmap, navMesh.grid(), navigator, navRequestExecutor)
            sagent.velocity = velocity

            sagent.position = Vector3(px, py, pz)

            agents.add(sagent)
            owner()!!.addComponent(sagent)
        }

        agents.forEach { ag ->
            ag.neighbours = agents.filter { it != ag }.toMutableList()
        }
    }

    override fun update(deltaTime: Float) {
    }

    override fun destroy() {
        Events.unsubscribe<MouseButtonPressedEvent, Any>(::onMouseButtonPressed)
        Events.unsubscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        owner()!!.getComponent<TerrainNavMeshDrawer>()?.dispose()

        navRequestExecutor.dispose()
    }

    private fun onMouseButtonPressed(event: MouseButtonPressedEvent, sender: Any) {
        if (event.button == GLFW.GLFW_MOUSE_BUTTON_2) {
            val ray = rayTracer.castRayInWorldSpace(mouseInput.lastX().toFloat(), mouseInput.lastY().toFloat())
            val pointOnTerrain = binarySearch(heightmap, camera.position(), ray, Vector2(0f, RAY_LENGTH))

            if (pointOnTerrain != null && navigator.evaluatePoint(pointOnTerrain, agent)) {
                targets.add(pointOnTerrain)
            }

            if (targets.size == 2) {
                navRequestExecutor.execute(NavRequest(targets[0], targets[1], agent, ::onNavResponseCompletedCallback))
                targets.clear()
            }
        }
    }

    override fun render(pass: RenderPass) {
        rayDrawer.draw()
        sphereDrawer.draw()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }

    private fun onDrawGizmos(event: DrawGizmosEvent, sender: Any) {
        owner()!!.getComponent<TerrainNavMeshDrawer>()?.draw()
    }

    private fun onNavResponseCompletedCallback(response: NavResponse) {
        val pathResult = response.path
        if (pathResult != null && pathResult.isValid()) {
            targetPath = pathResult.nodes!!.toMutableList()
            Events.publish(CalcTerrainPathEvent(pathResult), this)
        }
    }

    private fun collectObstacles(): List<NavMeshObstacle> {
        val root = (owner() as Object).getRoot()
        val obstacles = mutableListOf<NavMeshObstacle>()
        traverse(root, { obj ->
            if (obj is NavMeshObstacle) obstacles.add(obj)
        }, TraversalOrder.BREADTH_FIRST)
        return obstacles.toList()
    }
}