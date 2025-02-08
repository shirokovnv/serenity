package core.scene.navigation

import core.scene.navigation.path.PathResult

typealias NavResponseCallback = (response: NavResponse) -> Unit

enum class NavResponseStatus {
    COMPLETE,
    INTERRUPTED
}

data class NavResponse(val pathResult: PathResult?, val status: NavResponseStatus)