package modules.terrain.navigation

import core.events.Event
import core.scene.navigation.path.PathResult

data class CalcTerrainPathEvent(val pathResult: PathResult?): Event