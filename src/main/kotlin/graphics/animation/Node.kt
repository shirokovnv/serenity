package graphics.animation

import core.math.Matrix4

class Node(
    val name: String,
    val transform: Matrix4,
    val children: MutableList<Node> = mutableListOf()
)