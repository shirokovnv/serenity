package graphics.gui

data class GuiColumnGridSettings(val numColumns: Int = 2, val percentage: Float = 0.333f) {
    init {
        require(percentage in 0.1f..1.0f)
        require(numColumns > 0)
    }
}