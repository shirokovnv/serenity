package graphics.rendering.viewport

class Viewport(private var width: Int, private var height: Int): ViewportInterface {
    override fun getWidth(): Int {
        return width
    }

    override fun getHeight(): Int {
        return height
    }
}