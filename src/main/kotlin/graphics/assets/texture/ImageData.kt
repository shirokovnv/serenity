package graphics.assets.texture

data class ImageData(val id: Int, val width: Int, val height: Int) {
    companion object {
        fun empty(): ImageData = ImageData(0, 0, 0)
    }
}