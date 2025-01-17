package platform.services.filesystem

import graphics.assets.texture.Texture2d
import graphics.model.ModelData
import graphics.model.ModelLoader
import java.io.File
import java.io.IOException
import java.nio.file.Paths

class ObjLoader(private val fileLoader: TextFileLoader, private val imageLoader: ImageLoader) {
    fun load(objFilePath: String, mtlFilePath: String? = null): MutableMap<String, ModelData> {

        val objSource = fileLoader.load(objFilePath)
        val mtlSource = if (mtlFilePath != null) fileLoader.load(mtlFilePath) else null

        if (objSource == null) {
            throw RuntimeException("Unable to load object with path: $objFilePath")
        }

        val (_, dir) = splitPath(objFilePath)
        val modelLoader = ModelLoader()
        val mapOfModels = modelLoader.load(objSource, mtlSource)

        mapOfModels
            .values
            .filter { it.material != null && it.material.textures.isNotEmpty() }
            .forEach { modelData ->

                modelData.material!!.textures.values.forEach { texToken ->
                    try {
                        val image = imageLoader.loadImage(combinePath(dir, texToken.name!!))
                        texToken.texture = Texture2d(image)
                    } catch (_: IOException) {
                        // NO TEXTURE LOADED
                    }
                }
            }

        return mapOfModels
    }

    private fun combinePath(dir: String?, filename: String): String {
        return if (dir.isNullOrBlank()) filename
        else Paths.get(dir, filename).toString()
    }

    private fun splitPath(path: String): Pair<String?, String?> {
        val file = File(path)
        val fileName = file.name
        val parentPath = file.parent
        return Pair(fileName, parentPath)
    }
}