package org.voegtle.privatetrainer.business.bluetooth

import android.Manifest

class BluetoothPermissions {
    val code = 1
    val permissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_ADMIN,
    )

}
