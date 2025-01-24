package graphics.assets.buffer

enum class DepthBufferType(val value: Int) {
    NONE(0),
    DEPTH_TEXTURE(1),
    DEPTH_RENDER_BUFFER(2)
}