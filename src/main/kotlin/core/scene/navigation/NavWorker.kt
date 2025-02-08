package core.scene.navigation

class NavWorker(
    private val request: NavRequest,
    private val navigator: NavigatorInterface
) : Runnable {
    override fun run() {
        try {
            val pathResult = navigator.calculatePath(request.start, request.finish, request.agent)
            request.callback(NavResponse(pathResult, NavResponseStatus.COMPLETE))
        } catch (ie: InterruptedException) {
            request.callback(NavResponse(null, NavResponseStatus.INTERRUPTED))
        }
    }
}