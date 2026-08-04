package modules.terrain.roam

class RoamTerrainPatchMetrics {
    var updateTimeMs: Long = 0L
    var drawTimeMs: Long = 0L
    var triangulationTimeMs: Long = 0L
    var meshRebuildTime: Long = 0L
    var numTriangles: Int = 0
    var timeToGetSplittingList: Long = 0L
    var splitLoopTotalMs: Long = 0L
    var splitWorkOnlyCallsMs: Long = 0L
    var numSplitsExecuted: Int = 0

    var timeToGetMergingList: Long = 0L
    var mergeLoopTotalMs: Long = 0L
    var mergeWorkOnlyCallsMs: Long = 0L
    var numMergesExecuted: Int = 0
}