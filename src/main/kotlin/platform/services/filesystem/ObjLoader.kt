package platform.services.filesystem

import graphics.assets.texture.Texture2d
import graphics.model.Model
import graphics.model.ModelDataLoader
import java.io.File
import java.io.IOException
import java.nio.file.Paths

class ObjLoader(private val fileLoader: FileLoader, private val imageLoader: ImageLoader) {
    fun load(objFilePath: String, mtlFilePath: String? = null): Model {

        val objSource = fileLoader.loadAsString(objFilePath)
        val mtlSource = if (mtlFilePath != null) fileLoader.loadAsString(mtlFilePath) else null

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
        val normalizedDir = dir?.replace('\\', '/')
        return if (normalizedDir.isNullOrBlank()) {
            filename
        } else if (isResourcePath(normalizedDir)) {
            // Path is a resource path, do not try to use Paths.get().
            if (normalizedDir.endsWith("/")) {
                "$normalizedDir$filename"
            } else {
                "$normalizedDir/$filename"
            }
        } else {
            // Path is a file system path.
            Paths.get(normalizedDir, filename).toString()
        }
    }

    private fun splitPath(path: String): Pair<String?, String?> {
        val normalizedPath = path.replace('\\', '/')

        return if (isResourcePath(normalizedPath)) { // classpath
            val fileName = normalizedPath.substringAfterLast("/")
            val parentPath = normalizedPath.substringBeforeLast("/", "")
            if (parentPath.isEmpty()) {
                Pair(fileName, null)
            } else {
                Pair(fileName, parentPath)
            }
        } else { // file path
            val file = File(normalizedPath)
            val fileName = file.name
            val parentPath = file.parent
            Pair(fileName, parentPath)
        }
    }

    private fun isResourcePath(path: String): Boolean {
        return !File(path).exists()
    }
}