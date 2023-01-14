package org.voegtle.privatetrainer.business.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.os.Handler
import android.os.Looper

class BleScanManager(
    btManager: BluetoothManager,
    private val scanPeriod: Long = DEFAULT_SCAN_PERIOD,
    private val scanCallback: BleScanCallback = BleScanCallback(),
    private val beforeScanAction: () -> Unit = {},
    private val afterScanAction: () -> Unit = {}
) {
    private val btAdapter = btManager.adapter
    private val bleScanner = btAdapter.bluetoothLeScanner

    var beforeScanActions: MutableList<() -> Unit> = mutableListOf()
    var afterScanActions: MutableList<() -> Unit> = mutableListOf()

    /** True when the manager is performing the scan */
    private var scanning = false

    private val handler = Handler(Looper.getMainLooper())

    init {
        beforeScanActions.add(beforeScanAction)
        afterScanActions.add(afterScanAction)
    }

    @SuppressLint("MissingPermission")
    private fun startScan() {
        handler.postDelayed({ stopScan() }, scanPeriod)

        executeBeforeScanActions()

        scanning = true
        bleScanner.startScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        scanning = false
        bleScanner.stopScan(scanCallback)

        executeAfterScanActions()
    }

    /**
     * Scans for Bluetooth LE devices and restarts the scan after [scanPeriod] milliseconds.
     * Does not checks the required permissions are granted, check must be done beforehand.
     */
    fun scanBleDevices() {
        // scans for bluetooth LE devices
        if (scanning) {
            stopScan()
        } else {
            // execute all the functions to execute before scanning
            startScan()
        }
    }

    private fun executeBeforeScanActions() {
        executeListOfFunctions(beforeScanActions)
    }

    private fun executeAfterScanActions() {
        executeListOfFunctions(afterScanActions)
    }

    companion object {
        const val DEFAULT_SCAN_PERIOD: Long = 5000

        private fun executeListOfFunctions(toExecute: List<() -> Unit>) {
            toExecute.forEach {
                it()
            }
        }
    }
}
