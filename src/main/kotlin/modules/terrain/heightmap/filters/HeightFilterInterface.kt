package modules.terrain.heightmap.filters

interface HeightFilterInterface : FilterInterface {
    fun filter(x: Int, y: Int, height: Float): Float
}