package platform.services.input

interface KeyboardInputListener {
    fun onKeyPressed(key: Int)
    fun onKeyReleased(key: Int)
}