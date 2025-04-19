package modules.terrain.objects.flora.trees

import core.math.Vector2
import modules.terrain.sampling.SamplingContainerInterface

class TreeSamplingContainer(
    override var points: List<Vector2>,
    override var innerRadius: Float,
    override var outerRadius: Float
) : SamplingContainerInterface