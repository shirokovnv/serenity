package modules.terrain.navigation

import core.math.Vector2
import modules.terrain.sampling.BaseSamplingContainer
import modules.terrain.sampling.SamplingContainerInterface

class TerrainAgentSamplingContainer(
    override var points: List<Vector2>,
    override var innerRadius: Float,
    override var outerRadius: Float
) : BaseSamplingContainer()