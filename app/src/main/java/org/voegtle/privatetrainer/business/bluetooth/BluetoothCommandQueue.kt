package org.voegtle.privatetrainer.business.bluetooth

class BluetoothCommandQueue {
    val commandQueue = ArrayDeque<() -> Unit>()
    var running = false

    fun clear() {
        running = false
        commandQueue.clear()
    }

    fun schedule(command: () -> Unit) {
        commandQueue.add(command)
        run()
    }

    private fun run() {
        if (!running) {
            runNext()
        }
    }

    fun runNext() {
        val command = commandQueue.firstOrNull()
        running = command != null
        command?.invoke()
    }
}
