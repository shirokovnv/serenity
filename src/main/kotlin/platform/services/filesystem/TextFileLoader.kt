package platform.services.filesystem

import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths

class TextFileLoader {

    fun load(resourceName: String): String? {
        return try {
            val path = Paths.get(resourceName)
            if (Files.isReadable(path)) {
                return File(resourceName).readText(Charsets.UTF_8)
            } else {
                javaClass.classLoader.getResourceAsStream(resourceName)?.use { inputStream ->
                    InputStreamReader(inputStream, Charsets.UTF_8).buffered().use { reader ->
                        reader.readText()
                    }
                }
            }
        }
        catch (e: IOException) {
            System.err.println("Failed to load resource: $resourceName")
            e.printStackTrace()
            null
        }
    }
}