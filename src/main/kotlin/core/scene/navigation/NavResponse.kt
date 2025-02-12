package core.scene.navigation

import core.scene.navigation.path.Path

typealias NavResponseCallback = (response: NavResponse) -> Unit

enum class NavResponseStatus {
    COMPLETE,
    INTERRUPTED
}

data class NavResponse(val path: Path?, val status: NavResponseStatus)