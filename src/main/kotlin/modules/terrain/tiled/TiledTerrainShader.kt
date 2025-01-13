package modules.terrain.tiled

import core.scene.Object
import core.scene.camera.Camera
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL43
import platform.services.filesystem.TextFileLoader

class TiledTerrainShader: BaseShader<TiledTerrainShader, TiledTerrainMaterial>() {
    override fun setup() {
        val fileLoader = Object.services.getService<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/terrain/tiled/Terrain_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.load("shaders/terrain/tiled/Terrain_TC.glsl")!!,
            ShaderType.TESSELLATION_CONTROL_SHADER
        )

        addShader(
            fileLoader.load("shaders/terrain/tiled/Terrain_TE.glsl")!!,
            ShaderType.TESSELLATION_EVALUATION_SHADER
        )

        addShader(
            fileLoader.load("shaders/terrain/tiled/Terrain_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.load("shaders/terrain/tiled/Terrain_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("m_World")
        addUniform("m_View")
        addUniform("m_ViewProjection")
        addUniform("cameraPosition")
        addUniform("gridScale")
        addUniform("heightmap")
        addUniform("normalmap")
        addUniform("blendmap")
        addUniform("minDistance")
        addUniform("maxDistance")
        addUniform("minLOD")
        addUniform("maxLOD")
        addUniform("scaleY")
        addUniform("tbnRange")
        addUniform("tbnThreshold")

        for (i in TiledTerrainTextureType.entries) {
            addUniform("materials[${i.ordinal}].diffusemap")
            addUniform("materials[${i.ordinal}].normalmap")
            addUniform("materials[${i.ordinal}].displacementmap")
            addUniform("materials[${i.ordinal}].verticalScale")
            addUniform("materials[${i.ordinal}].horizontalScale")
        }
    }

    override fun updateUniforms() {
        setUniform("m_World", shaderMaterial!!.world)
        setUniform("m_View", shaderMaterial!!.view)
        setUniform("m_ViewProjection", shaderMaterial!!.viewProjection)
        setUniform("cameraPosition", Object.services.getService<Camera>()!!.position())
        setUniformf("gridScale", shaderMaterial!!.gridScale)

        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        shaderMaterial!!.heightmap.getTexture().bind()
        setUniformi("heightmap", 0)

        GL43.glActiveTexture(GL43.GL_TEXTURE1)
        shaderMaterial!!.normalmap.bind()
        setUniformi("normalmap", 1)

        GL43.glActiveTexture(GL43.GL_TEXTURE2)
        shaderMaterial!!.blendmap.bind()
        setUniformi("blendmap", 2)

        var texUnit = 3
        lateinit var materialDetail: TiledTerrainMaterialDetail
        for (i in TiledTerrainTextureType.entries) {
            materialDetail = shaderMaterial!!.materialDetailMap[i]!!

            GL43.glActiveTexture(GL43.GL_TEXTURE0 + texUnit)
            materialDetail.diffuseMap.bind()
            setUniformi("materials[${i.ordinal}].diffusemap", texUnit)
            texUnit++

            GL43.glActiveTexture(GL43.GL_TEXTURE0 + texUnit)
            materialDetail.normalMap.bind()
            setUniformi("materials[${i.ordinal}].normalmap", texUnit)
            texUnit++

            GL43.glActiveTexture(GL43.GL_TEXTURE0 + texUnit)
            materialDetail.displacementMap.bind()
            setUniformi("materials[${i.ordinal}].displacementmap", texUnit)
            texUnit++

            setUniformf("materials[${i.ordinal}].verticalScale", materialDetail.verticalScale)
            setUniformf("materials[${i.ordinal}].horizontalScale", materialDetail.horizontalScale)
        }

        setUniformf("minDistance", shaderMaterial!!.minDistance)
        setUniformf("maxDistance", shaderMaterial!!.maxDistance)
        setUniformf("minLOD", shaderMaterial!!.minLOD)
        setUniformf("maxLOD", shaderMaterial!!.maxLOD)
        setUniformf("scaleY", shaderMaterial!!.scaleY)
        setUniformf("tbnRange", shaderMaterial!!.tbnRange)
        setUniformf("tbnThreshold", shaderMaterial!!.tbnThreshold)
    }
}