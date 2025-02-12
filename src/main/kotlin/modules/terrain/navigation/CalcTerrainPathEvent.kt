package modules.terrain.navigation

import core.events.Event
import core.scene.navigation.path.Path

data class CalcTerrainPathEvent(val path: Path?): Event