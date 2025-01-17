package platform.services.filesystem

import graphics.assets.texture.Texture2d
import graphics.model.Model
import graphics.model.ModelDataLoader
import java.io.File
import java.io.IOException
import java.nio.file.Paths

class ObjLoader(private val fileLoader: TextFileLoader, private val imageLoader: ImageLoader) {
    fun load(objFilePath: String, mtlFilePath: String? = null): Model {

        val objSource = fileLoader.load(objFilePath)
        val mtlSource = if (mtlFilePath != null) fileLoader.load(mtlFilePath) else null

        if (objSource == null) {
            throw RuntimeException("Unable to load object with path: $objFilePath")
        }

        val (_, dir) = splitPath(objFilePath)
        val modelDataLoader = ModelDataLoader()
        val modelMeshData = modelDataLoader.load(objSource, mtlSource)

        modelMeshData
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

        return Model(modelMeshData)
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