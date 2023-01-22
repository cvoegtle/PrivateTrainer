package org.voegtle.privatetrainer.business.bluetooth

import java.util.concurrent.ArrayBlockingQueue

class BluetoothCommandQueue {
    val commandQueue = ArrayBlockingQueue<() -> Unit>(100)
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
        val command = commandQueue.poll()
        running = command != null
        command?.invoke()
    }
}
