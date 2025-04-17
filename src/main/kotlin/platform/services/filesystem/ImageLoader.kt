package platform.services.filesystem

import graphics.assets.texture.CubemapTexture
import graphics.assets.texture.ImageData
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43
import org.lwjgl.stb.STBImage
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths

class ImageLoader {

    companion object {
        private val cubemapFaceTypes = arrayOf(
            GL43.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
            GL43.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
            GL43.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
            GL43.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
            GL43.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
            GL43.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
        )
    }

    fun loadImage(fileName: String): ImageData {
        val imageBuffer: ByteBuffer = try {
            ioResourceToByteBuffer(fileName)
        } catch (e: IOException) {
            throw e
        }
        val w = BufferUtils.createIntBuffer(1)
        val h = BufferUtils.createIntBuffer(1)
        val c = BufferUtils.createIntBuffer(1)

        // Use info to read image metadata without decoding the entire image.
        if (!STBImage.stbi_info_from_memory(imageBuffer, w, h, c)) {
            throw RuntimeException("Failed to read image information: ${STBImage.stbi_failure_reason()}")
        }

        // Decode the image
        val image = STBImage.stbi_load_from_memory(imageBuffer, w, h, c, 0)
            ?: throw RuntimeException("Failed to load image: ${STBImage.stbi_failure_reason()}")
        val width = w[0]
        val height = h[0]
        val comp = c[0]
        val texId = GL43.glGenTextures()
        GL43.glBindTexture(GL43.GL_TEXTURE_2D, texId)
        if (comp == 3) {
            if (width and 3 != 0) {
                GL43.glPixelStorei(GL43.GL_UNPACK_ALIGNMENT, 2 - (width and 1))
            }
            GL43.glTexImage2D(
                GL43.GL_TEXTURE_2D,
                0,
                GL43.GL_RGB,
                width,
                height,
                0,
                GL43.GL_RGB,
                GL43.GL_UNSIGNED_BYTE,
                image
            )
        } else {
            GL43.glTexImage2D(
                GL43.GL_TEXTURE_2D,
                0,
                GL43.GL_RGBA,
                width,
                height,
                0,
                GL43.GL_RGBA,
                GL43.GL_UNSIGNED_BYTE,
                image
            )
        }

        STBImage.stbi_image_free(image)
        return ImageData(texId, w.get(), h.get())
    }

    fun loadCubeImage(
        posXFilename: String,
        negXFilename: String,
        posYFilename: String,
        negYFilename: String,
        posZFilename: String,
        negZFilename: String
    ): CubemapTexture {
        val cubeTextureFilenames = arrayOf(
            posXFilename,
            negXFilename,
            posYFilename,
            negYFilename,
            posZFilename,
            negZFilename
        )

        val w = BufferUtils.createIntBuffer(1)
        val h = BufferUtils.createIntBuffer(1)
        val c = BufferUtils.createIntBuffer(1)

        val cubeTexId = GL43.glGenTextures()
        GL43.glBindTexture(GL43.GL_TEXTURE_CUBE_MAP, cubeTexId)

        // TODO: what if exception will be thrown while processing files ?
        for (i in cubeTextureFilenames.indices) {
            val fileName = cubeTextureFilenames[i]

            val imageBuffer: ByteBuffer = try {
                ioResourceToByteBuffer(fileName)
            } catch (e: IOException) {
                throw e
            }

            w.rewind().limit(1)
            h.rewind().limit(1)
            c.rewind().limit(1)

            // Use info to read image metadata without decoding the entire image.
            if (!STBImage.stbi_info_from_memory(imageBuffer, w, h, c)) {
                throw RuntimeException("Failed to read image information: ${STBImage.stbi_failure_reason()}")
            }

            // Decode the image
            val image = STBImage.stbi_load_from_memory(imageBuffer, w, h, c, 0)
                ?: throw RuntimeException("Failed to load image: ${STBImage.stbi_failure_reason()}")

            val width = w[0]
            val height = h[0]

            GL43.glTexImage2D(
                cubemapFaceTypes[i],
                0,
                GL43.GL_RGB,
                width,
                height,
                0,
                GL43.GL_RGB,
                GL43.GL_UNSIGNED_BYTE,
                image
            )

            GL43.glTexParameteri(GL43.GL_TEXTURE_CUBE_MAP, GL43.GL_TEXTURE_MAG_FILTER, GL43.GL_LINEAR)
            GL43.glTexParameteri(GL43.GL_TEXTURE_CUBE_MAP, GL43.GL_TEXTURE_MIN_FILTER, GL43.GL_LINEAR)
            GL43.glTexParameteri(GL43.GL_TEXTURE_CUBE_MAP, GL43.GL_TEXTURE_WRAP_S, GL43.GL_CLAMP_TO_EDGE)
            GL43.glTexParameteri(GL43.GL_TEXTURE_CUBE_MAP, GL43.GL_TEXTURE_WRAP_T, GL43.GL_CLAMP_TO_EDGE)
            GL43.glTexParameteri(GL43.GL_TEXTURE_CUBE_MAP, GL43.GL_TEXTURE_WRAP_R, GL43.GL_CLAMP_TO_EDGE)

            STBImage.stbi_image_free(image)
        }

        GL43.glBindTexture(GL43.GL_TEXTURE_CUBE_MAP, 0)

        return CubemapTexture(cubeTexId)
    }

    fun loadImageToByteBuffer(fileName: String): ByteBuffer {
        val imageBuffer: ByteBuffer = try {
            ioResourceToByteBuffer(fileName)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        val w = BufferUtils.createIntBuffer(1)
        val h = BufferUtils.createIntBuffer(1)
        val c = BufferUtils.createIntBuffer(1)

        // Use info to read image metadata without decoding the entire image.
        if (!STBImage.stbi_info_from_memory(imageBuffer, w, h, c)) {
            throw RuntimeException("Failed to read image information: " + STBImage.stbi_failure_reason())
        }

        // Decode the image
        return STBImage.stbi_load_from_memory(imageBuffer, w, h, c, 0)
            ?: throw RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason())
    }

    @Throws(IOException::class)
    private fun ioResourceToByteBuffer(resource: String): ByteBuffer {
        var buffer: ByteBuffer? = null
        val path = Paths.get(resource)

        if (Files.isReadable(path)) {
            buffer = Files.readAllBytes(path).let { bytes ->
                BufferUtils.createByteBuffer(bytes.size).put(bytes).flip()
            }
        } else {
            javaClass.classLoader.getResourceAsStream(resource)?.use { source ->
                val bytes = source.readBytes()
                buffer = BufferUtils.createByteBuffer(bytes.size).put(bytes).flip()
            }
        }

        if (buffer == null) {
            throw IOException("Unable to load resource: $resource")
        }

        return buffer!!
    }
}