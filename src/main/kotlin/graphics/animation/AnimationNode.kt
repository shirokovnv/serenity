package graphics.animation

import core.math.Quaternion
import core.math.Vector3
import graphics.animation.AnimationKey

data class AnimationNode(
    val name: String,
    val positionKeys: List<AnimationKey<Vector3>>,
    val rotationKeys: List<AnimationKey<Quaternion>>,
    val scalingKeys: List<AnimationKey<Vector3>>
)