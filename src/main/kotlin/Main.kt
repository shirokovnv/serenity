import core.scene.SceneGraph
import core.scene.camera.CameraSettings
import modules.terrain.TerrainSceneParams
import modules.terrain.quadtree.QuadTreeTerrainScene
import modules.terrain.tiled.TiledTerrainScene
import platform.Application
import platform.ApplicationSettings

val settings = ApplicationSettings(
    1280,
    720,
    200.0f,
    "Serenity Engine - OPENGL"
)

class App(settings: ApplicationSettings) : Application(settings) {
    override fun oneTimeSceneInit(): SceneGraph {
        val params = TerrainSceneParams(
            cameraSettings = CameraSettings(
                settings.screenWidth,
                settings.screenHeight
            )
        )

        val quadTreeScene = QuadTreeTerrainScene(params)
        val scene = quadTreeScene.oneTimeSceneInit()
        quadTreeScene
            .withSkyDome()
            .withLensFlare()
        
        return scene
    }
}

fun main() {
    val app = App(settings)

    app.launch()
}