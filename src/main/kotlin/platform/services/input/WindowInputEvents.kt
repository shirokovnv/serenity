package platform.services.input

data class WindowResizedEvent(val newWidth: Int, val newHeight: Int): InputEvent

data class WindowFocusedEvent(val focused: Boolean): InputEvent