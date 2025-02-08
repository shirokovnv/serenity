package core.scene.navigation

import core.management.Disposable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NavRequestExecutor(private val navigator: NavigatorInterface) : Disposable {
    private val workerPool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    fun execute(request: NavRequest) {
        val worker = NavWorker(request, navigator)

        workerPool.execute(worker)
    }

    override fun dispose() {
        workerPool.shutdownNow()
    }
}