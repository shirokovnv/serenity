package graphics.assets.texture

import org.lwjgl.opengl.GL43
import java.nio.ByteBuffer

object CubemapTextureFactory {
    fun createEmpty(width: Int, height: Int): CubemapTexture {
        val cubeTexId = GL43.glGenTextures()
        GL43.glBindTexture(GL43.GL_TEXTURE_CUBE_MAP, cubeTexId)

        for (i in cubemapFaceTypes.indices) {
            GL43.glTexImage2D(
                cubemapFaceTypes[i],
                0,
                GL43.GL_RGBA8,
                width,
                height,
                0,
                GL43.GL_RGBA8,
                GL43.GL_UNSIGNED_BYTE,
                null as ByteBuffer?
            )

            GL43.glTexParameteri(GL43.GL_TEXTURE_CUBE_MAP, GL43.GL_TEXTURE_MAG_FILTER, GL43.GL_LINEAR)
            GL43.glTexParameteri(GL43.GL_TEXTURE_CUBE_MAP, GL43.GL_TEXTURE_MIN_FILTER, GL43.GL_LINEAR)
            GL43.glTexParameteri(GL43.GL_TEXTURE_CUBE_MAP, GL43.GL_TEXTURE_WRAP_S, GL43.GL_CLAMP_TO_EDGE)
            GL43.glTexParameteri(GL43.GL_TEXTURE_CUBE_MAP, GL43.GL_TEXTURE_WRAP_T, GL43.GL_CLAMP_TO_EDGE)
            GL43.glTexParameteri(GL43.GL_TEXTURE_CUBE_MAP, GL43.GL_TEXTURE_WRAP_R, GL43.GL_CLAMP_TO_EDGE)
        }

        GL43.glBindTexture(GL43.GL_TEXTURE_CUBE_MAP, 0)

        return CubemapTexture(cubeTexId)
    }
}