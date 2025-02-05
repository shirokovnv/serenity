package graphics.tools

import core.ecs.Behaviour
import core.events.Events
import core.management.Resources
import core.math.Quaternion
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.PerspectiveCamera
import core.scene.picking.PickingTargetEvent
import core.scene.picking.PickingSelector
import core.scene.raytracing.RayTracer
import core.scene.volumes.BoxAABB
import org.lwjgl.glfw.GLFW
import platform.services.input.MouseButtonPressedEvent
import platform.services.input.MouseButtonReleasedEvent
import platform.services.input.MouseInput

class PickingBehaviour : Behaviour() {
    private lateinit var rayTracer: RayTracer
    private val rayLength: Float = 2500.0f
    private val isDraggingPhase: Boolean
        get() = mouseInput.isMouseButtonHolding(GLFW.GLFW_MOUSE_BUTTON_1)

    private val pickings = mutableListOf<BoxAABB>()

    private val mouseInput: MouseInput
        get() = Resources.get<MouseInput>()!!

    private val camera: PerspectiveCamera
        get() = (Resources.get<Camera>()!! as PerspectiveCamera)

    override fun create() {
        rayTracer = RayTracer(camera)

        Events.subscribe<MouseButtonPressedEvent, Any>(::onMouseButtonPressed)
        Events.subscribe<MouseButtonReleasedEvent, Any>(::onMouseButtonReleased)
    }

    override fun update(deltaTime: Float) {
        if (isDraggingPhase && pickings.isNotEmpty()) {
            val mouseX = mouseInput.lastX().toFloat()
            val mouseY = mouseInput.lastY().toFloat()

            val rayView = rayTracer.castRayInViewSpace(mouseX, mouseY)

            val bounds = pickings.first()
            val transform = bounds.owner()!!.getComponent<Transform>()!!
            val objViewSpacePosition = camera.view * Quaternion(transform.translation(), 1.0f)
            val viewSpaceIntersect = Quaternion(rayView * -objViewSpacePosition.z, 1.0f)

            val newWorldPosition = (camera.view.invert() * viewSpaceIntersect).xyz()

            transform.setTranslation(newWorldPosition)
            (bounds.owner()!! as Object).recalculateBounds()

            Events.publish<PickingTargetEvent, Any>(PickingTargetEvent(bounds.owner()!! as Object), this)
        }
    }

    override fun destroy() {
        Events.unsubscribe<MouseButtonPressedEvent, Any>(::onMouseButtonPressed)
        Events.unsubscribe<MouseButtonReleasedEvent, Any>(::onMouseButtonReleased)
    }

    private fun onMouseButtonPressed(event: MouseButtonPressedEvent, sender: Any) {
        if (event.button == GLFW.GLFW_MOUSE_BUTTON_1) {
            val mouseX = mouseInput.lastX().toFloat()
            val mouseY = mouseInput.lastY().toFloat()

            val ray = rayTracer.castRayInWorldSpace(mouseX, mouseY)
            pickings.addAll(PickingSelector.selectInRange(camera.position(), ray, rayLength))
        }
    }

    private fun onMouseButtonReleased(event: MouseButtonReleasedEvent, sender: Any) {
        if (event.button == GLFW.GLFW_MOUSE_BUTTON_1) {
            pickings.clear()
        }
    }
}