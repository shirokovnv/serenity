package core.scene.navigation

import core.management.Disposable
import java.util.concurrent.*

class NavRequestExecutor(private val navigator: NavigatorInterface) : Disposable {
    private val workerPool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    private val requestQueue: BlockingQueue<NavRequest> = LinkedBlockingQueue()
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    private val maxQueueSize = 1000
    private val maxProcessingElements = 10
    private val processIntervalMillis: Long = 100

    init {
        scheduleQueueProcessing()
    }

    fun execute(request: NavRequest): Boolean {
        if (requestQueue.size >= maxQueueSize) {
            return false
        }

        return requestQueue.add(request)
    }

    override fun dispose() {
        workerPool.shutdownNow()
        scheduler.shutdownNow()
    }

    private fun scheduleQueueProcessing() {
        scheduler.scheduleAtFixedRate(
            {
                try {
                    val requests = mutableListOf<NavRequest>()
                    requestQueue.drainTo(requests, maxProcessingElements)

                    requests.forEach { request ->
                        val worker = NavWorker(request, navigator)
                        workerPool.execute(worker)
                    }
                } catch (e: Exception) {
                    println("Error processing queue: ${e.message}")

                    if (e is InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
            },
            0,
            processIntervalMillis,
            TimeUnit.MILLISECONDS
        )
    }
}