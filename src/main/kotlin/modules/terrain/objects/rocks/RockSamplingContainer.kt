package modules.terrain.objects.rocks

import core.math.Vector2
import modules.terrain.sampling.BaseSamplingContainer

class RockSamplingContainer(
    override var points: List<Vector2>,
    override var innerRadius: Float,
    override var outerRadius: Float
) : BaseSamplingContainer()