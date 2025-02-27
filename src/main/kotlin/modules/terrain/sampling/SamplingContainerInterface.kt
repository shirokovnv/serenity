package modules.terrain.sampling

import core.math.Vector2

interface SamplingContainerInterface {
    var points: List<Vector2>
    var innerRadius: Float
    var outerRadius: Float
}