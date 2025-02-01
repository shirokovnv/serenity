package platform.services.filesystem

import graphics.animation.AnimationModel
import graphics.animation.AnimationParser

class AssimpLoader(
    private val fileLoader: FileLoader,
    private val animationParser: AnimationParser
) {
    fun load(modelResourceName: String): AnimationModel {
        val modelByteBuffer = fileLoader.loadAsBytes(modelResourceName)!!

        return animationParser.parse(modelByteBuffer)
    }
}