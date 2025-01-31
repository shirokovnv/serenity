package platform.services.filesystem

import org.lwjgl.BufferUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import java.nio.file.Paths

class FileLoader {

    fun loadAsString(resourceName: String): String? {
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
        } catch (e: IOException) {
            System.err.println("Failed to load resource: $resourceName")
            e.printStackTrace()
            null
        }
    }

    fun loadAsBytes(resourceName: String): ByteBuffer? {
        return try {
            val path = Paths.get(resourceName)
            if (Files.isReadable(path)) {
                val file = File(resourceName)
                val bytes = Files.readAllBytes(file.toPath())
                BufferUtils.createByteBuffer(bytes.size).apply {
                    order(ByteOrder.nativeOrder())
                    put(bytes)
                    flip()
                }
            } else {
                javaClass.classLoader.getResourceAsStream(resourceName)?.use { inputStream ->
                    val outputStream = ByteArrayOutputStream()
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    val byteArray = outputStream.toByteArray()
                    BufferUtils.createByteBuffer(byteArray.size).apply {
                        order(ByteOrder.nativeOrder())
                        put(byteArray)
                        flip()
                    }
                }
            }
        } catch (e: IOException) {
            System.err.println("Failed to load resource: $resourceName")
            e.printStackTrace()
            null
        }
    }
}