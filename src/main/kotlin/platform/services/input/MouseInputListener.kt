package platform.services.input

interface MouseInputListener {
    fun onMouseMoved(xOffset: Float, yOffset: Float)
    fun onMouseScrolled(yOffset: Float)
    fun onMouseButtonPressed(button: Int)
    fun onMouseButtonReleased(button: Int)
}