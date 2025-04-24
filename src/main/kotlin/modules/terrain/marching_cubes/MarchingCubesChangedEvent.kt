package modules.terrain.marching_cubes

import core.events.Event

data class MarchingCubesChangedEvent(
    val gridParams: MarchingCubesGridParams,
    val noiseParams: MarchingCubesNoiseParams,
    val extraParams: MarchingCubesExtraParams,
    val meshParamsChanged: Boolean
) : Event