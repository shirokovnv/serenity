package graphics.animation

import core.math.Matrix4

class Bone {
    var name: String = ""
    var offset: Matrix4 = Matrix4().identity()
    var transform: Matrix4 = Matrix4().identity()
}