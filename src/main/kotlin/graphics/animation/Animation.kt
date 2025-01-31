package graphics.animation

data class Animation(
    val name: String,
    val duration: Double,
    val ticksPerSecond: Double,
    val channels: List<AnimationNode>
)