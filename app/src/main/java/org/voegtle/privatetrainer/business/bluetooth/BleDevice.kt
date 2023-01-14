package org.voegtle.privatetrainer.business.bluetooth

data class BleDevice(val name: String) {
    companion object {
        val NAME_PRIVATETRAINER = "TD5322A_V2.1.3BLE"
        fun createBleDevicesSet(): MutableSet<BleDevice> {
            return mutableSetOf()
        }
    }
}
