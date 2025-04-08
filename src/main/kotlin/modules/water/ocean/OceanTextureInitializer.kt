package modules.water.ocean

import core.management.Disposable
import graphics.assets.texture.Texture2d
import graphics.assets.texture.TextureFactory
import org.lwjgl.opengl.GL43.*
import java.nio.FloatBuffer

enum class OceanTextureType {
    SPECTRUM0,
    SPECTRUMT,
    GAUSS,
    DISPLACEMENT,
    NORMAL,
    DX,
    DY,
    DZ,
    TEMP
}

class OceanTextureInitializer(private val resolution: Int): Disposable {
    private val textureMap = HashMap<OceanTextureType, Texture2d>()
    
    val spectrum0: Texture2d
        get() = textureMap[OceanTextureType.SPECTRUM0]!!
    val spectrumT: Texture2d
        get() = textureMap[OceanTextureType.SPECTRUMT]!!
    val gaussTex: Texture2d
        get() = textureMap[OceanTextureType.GAUSS]!!
    val dispTex: Texture2d
        get() = textureMap[OceanTextureType.DISPLACEMENT]!!
    val normalTex: Texture2d
        get() = textureMap[OceanTextureType.NORMAL]!!
    val dxTex: Texture2d
        get() = textureMap[OceanTextureType.DX]!!
    val dyTex: Texture2d
        get() = textureMap[OceanTextureType.DY]!!
    val dzTex: Texture2d
        get() = textureMap[OceanTextureType.DZ]!!
    val tempTex: Texture2d
        get() = textureMap[OceanTextureType.TEMP]!!

    init {
        initSpectrum0Texture()
        initSpectrumTTexture()
        initGaussTexture()
        initDisplacementTexture()
        initNormalTexture()
        initDxTexture()
        initDyTexture()
        initDzTexture()
        initTempTexture()
    }

    fun getTexture(textureType: OceanTextureType): Texture2d {
        return textureMap[textureType]!!
    }

    fun swapWithTemp(oceanTextureType: OceanTextureType) {
        val texture = textureMap[oceanTextureType]!!
        textureMap[oceanTextureType] = textureMap[OceanTextureType.TEMP]!!
        textureMap[OceanTextureType.TEMP] = texture
    }

    private fun initSpectrum0Texture() {
        val spectrum0 = Texture2d(resolution, resolution)
        setDefaultTextureParams(spectrum0)
        textureMap[OceanTextureType.SPECTRUM0] = spectrum0
    }

    private fun initSpectrumTTexture() {
        val spectrumT = Texture2d(resolution, resolution)
        setDefaultTextureParams(spectrumT)
        textureMap[OceanTextureType.SPECTRUMT] = spectrumT
    }

    private fun initGaussTexture() {
        val gaussTex = TextureFactory.fromGaussNoise(resolution, resolution, 16.0f)
        gaussTex.bind()
        gaussTex.noFilter()
        gaussTex.wrapModeClampToBorder()
        gaussTex.unbind()
        
        textureMap[OceanTextureType.GAUSS] = gaussTex
    }

    private fun initDxTexture() {
        val dxTex = Texture2d(resolution, resolution)
        setDefaultTextureParams(dxTex)
        textureMap[OceanTextureType.DX] = dxTex
    }

    private fun initDyTexture() {
        val dyTex = Texture2d(resolution, resolution)
        setDefaultTextureParams(dyTex)
        textureMap[OceanTextureType.DY] = dyTex
    }

    private fun initDzTexture() {
        val dzTex = Texture2d(resolution, resolution)
        setDefaultTextureParams(dzTex)
        textureMap[OceanTextureType.DZ] = dzTex
    }

    private fun initNormalTexture() {
        val normalTex = Texture2d(resolution, resolution)
        setDefaultTextureParams(normalTex)
        textureMap[OceanTextureType.NORMAL] = normalTex
    }

    private fun initDisplacementTexture() {
        val dispTex = Texture2d(resolution, resolution)
        setDefaultTextureParams(dispTex)
        textureMap[OceanTextureType.DISPLACEMENT] = dispTex
    }

    private fun initTempTexture() {
        val tempTex = Texture2d(resolution, resolution)
        setDefaultTextureParams(tempTex)
        textureMap[OceanTextureType.TEMP] = tempTex
    }

    private fun setDefaultTextureParams(texture2d: Texture2d) {
        texture2d.bind()
        glTexImage2D(
            GL_TEXTURE_2D, 0, GL_RGBA32F, resolution, resolution,
            0, GL_RGBA, GL_FLOAT, null as FloatBuffer?
        )
        texture2d.noFilter()
        texture2d.wrapModeRepeat()
        texture2d.unbind()
    }

    override fun dispose() {
        textureMap.values.forEach { texture2d -> texture2d.destroy() }
    }
}