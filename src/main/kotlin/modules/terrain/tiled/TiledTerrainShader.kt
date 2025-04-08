package modules.terrain.tiled

import core.management.Resources
import core.scene.camera.Camera
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import modules.light.SunLightManager
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class TiledTerrainShader : BaseShader<TiledTerrainShader, TiledTerrainMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!
        val frustumInc = fileLoader.loadAsString("shaders/include/Frustum.glsl")!!
        val shadowInc = fileLoader.loadAsString("shaders/include/Shadow.glsl")!!

        val vertexShaderSource = fileLoader.loadAsString("shaders/terrain/tiled/Terrain_VS.glsl")!!
        val fragmentShaderSource = preprocessShader(
            fileLoader.loadAsString("shaders/terrain/tiled/Terrain_FS.glsl")!!,
            mapOf("Shadow.glsl" to shadowInc)
        )

        val tessControlShaderSource = fileLoader.loadAsString("shaders/terrain/tiled/Terrain_TC.glsl")!!
        val tessEvalShaderSource = fileLoader.loadAsString("shaders/terrain/tiled/Terrain_TE.glsl")!!
        val geomShaderSource = preprocessShader(
            fileLoader.loadAsString("shaders/terrain/tiled/Terrain_GS.glsl")!!,
            mapOf("Frustum.glsl" to frustumInc)
        )

        addShader(
            vertexShaderSource,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            tessControlShaderSource,
            ShaderType.TESSELLATION_CONTROL_SHADER
        )

        addShader(
            tessEvalShaderSource,
            ShaderType.TESSELLATION_EVALUATION_SHADER
        )

        addShader(
            geomShaderSource,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fragmentShaderSource,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("m_World")
        addUniform("m_View")
        addUniform("m_ViewProjection")
        addUniform("m_LightViewProjection")
        addUniform("cameraPosition")
        addUniform("clipPlane")
        addUniform("gridScale")
        addUniform("heightmap")
        addUniform("normalmap")
        addUniform("blendmap")
        addUniform("shadowmap")
        addUniform("minDistance")
        addUniform("maxDistance")
        addUniform("minLOD")
        addUniform("maxLOD")
        addUniform("scaleY")
        addUniform("tbnRange")
        addUniform("tbnThreshold")
        addUniform("sunVector")
        addUniform("sunIntensity")
        addUniform("sunColor")
        addUniform("renderInBlack")

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
        setUniform("m_LightViewProjection", shaderMaterial!!.lightViewProjection)
        setUniform("cameraPosition", Resources.get<Camera>()!!.position())
        setUniform("clipPlane", shaderMaterial!!.clipPlane)
        setUniformf("gridScale", shaderMaterial!!.gridScale)
        setUniformi("renderInBlack", if (shaderMaterial!!.renderInBlack) 1 else 0)

        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        shaderMaterial!!.heightmap.texture().bind()
        setUniformi("heightmap", 0)

        GL43.glActiveTexture(GL43.GL_TEXTURE1)
        shaderMaterial!!.normalmap.bind()
        setUniformi("normalmap", 1)

        GL43.glActiveTexture(GL43.GL_TEXTURE2)
        shaderMaterial!!.blendmap.bind()
        setUniformi("blendmap", 2)

        GL43.glActiveTexture(GL43.GL_TEXTURE3)
        shaderMaterial!!.shadowmap.bind()
        setUniformi("shadowmap", 3)

        var texUnit = 4
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
        setUniform("sunVector", Resources.get<SunLightManager>()!!.sunVector())
        setUniformf("sunIntensity", Resources.get<SunLightManager>()!!.sunIntensity())
        setUniform("sunColor", Resources.get<SunLightManager>()!!.sunColor())
    }
}