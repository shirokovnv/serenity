package graphics.animation

import graphics.rendering.Color
import graphics.rendering.Colors

class MtlData(
    var index: Int,
    var diffuseColor: Color,
    var ambientColor: Color = Colors.LightGray,
    var specularColor: Color = Colors.White,
    var emissiveColor: Color = Colors.White,
    var shininess: Float = 0.0f,
    var shininessStrength: Float = 1.0f,
    var opacity: Float = 1.0f,
) {

    override fun toString(): String {
        return "MtlData($index), \n" +
                "diffuse: $diffuseColor, \n" +
                "ambient: $ambientColor, \n" +
                "specular: $specularColor, \n" +
                "emissive: $emissiveColor,\n " +
                "shininess: $shininess, \n" +
                "shininessStrength: $shininessStrength, \n" +
                "opacity: $opacity \n"
    }
}