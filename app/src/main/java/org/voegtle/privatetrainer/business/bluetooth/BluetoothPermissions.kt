package org.voegtle.privatetrainer.business.bluetooth

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

class BluetoothPermissions {
    val code = 1

    fun permissions() = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) permissionsApiLevel31 else permissionsApiLevel26

    private val permissionsApiLevel26 = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_ADMIN,
    )

    @RequiresApi(Build.VERSION_CODES.S)
    private val permissionsApiLevel31 = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_ADMIN,
    )

}
