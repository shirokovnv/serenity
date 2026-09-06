package modules.terrain.heightmap.filters

interface HeightmapFilterInterface : FilterInterface {
    fun filter(map: FloatArray, size: Int): FloatArray
}