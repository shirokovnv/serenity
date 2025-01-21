package platform.services.input

data class KeyPressedEvent(val key: Int): InputEvent

data class KeyReleasedEvent(val key: Int): InputEvent