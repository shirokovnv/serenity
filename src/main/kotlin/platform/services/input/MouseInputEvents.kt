package platform.services.input

data class MouseMovedEvent(val xOffset: Float, val yOffset: Float): InputEvent

data class MouseScrolledEvent(val yOffset: Float): InputEvent

data class MouseButtonPressedEvent(val button: Int): InputEvent

data class MouseButtonReleasedEvent(val button: Int): InputEvent