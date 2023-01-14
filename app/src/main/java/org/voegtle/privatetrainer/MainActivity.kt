package org.voegtle.privatetrainer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.lorenzofelletti.permissions.PermissionManager
import com.lorenzofelletti.permissions.dispatcher.dsl.checkPermissions
import com.lorenzofelletti.permissions.dispatcher.dsl.doOnDenied
import com.lorenzofelletti.permissions.dispatcher.dsl.doOnGranted
import com.lorenzofelletti.permissions.dispatcher.dsl.withRequestCode
import org.voegtle.privatetrainer.business.BluetoothConnectionStatus.*
import org.voegtle.privatetrainer.business.BluetoothState
import org.voegtle.privatetrainer.business.bluetooth.*
import org.voegtle.privatetrainer.ui.theme.PrivateTrainerTheme

class MainActivity : ComponentActivity() {

    private val permissionsManager = PermissionManager(this)

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val bluetoothState: MutableState<BluetoothState> =
                remember { mutableStateOf(BluetoothState()) }
            determineBluetoothState(bluetoothState)
            PrivateTrainerTheme {
                val windowSize = calculateWindowSizeClass(this)
                val displayFeatures = calculateDisplayFeatures(this)

                PrivateTrainerApp(
                    windowSize = windowSize,
                    displayFeatures = displayFeatures,
                    detectedBluetoothState = bluetoothState.value
                )
            }
        }
    }

    private fun determineBluetoothState(bluetoothState: MutableState<BluetoothState>) {

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        bluetoothState.value.connectionStatus = when {
            bluetoothAdapter == null -> not_supported
            bluetoothAdapter.isEnabled -> not_connected
            !bluetoothAdapter.isEnabled -> disabled
            else -> not_supported
        }

        permissionsManager.buildRequestResultsDispatcher {
            withRequestCode(BluetoothPermissions().code) {
                checkPermissions(BluetoothPermissions().permissions)
                doOnDenied {
                    bluetoothState.value.connectionStatus = permission_denied
                }
                doOnGranted {
                    scanBluetoothDeviceStatus(bluetoothState, bluetoothManager)
                }
            }
        }
        permissionsManager.checkRequestAndDispatch(BluetoothPermissions().code)
    }

    @SuppressLint("MissingPermission")
    private fun scanBluetoothDeviceStatus(
        bluetoothState: MutableState<BluetoothState>,
        bluetoothManager: BluetoothManager
    ) {
        if (bluetoothState.value.connectionStatus > permission_denied) {
            BleScanManager(bluetoothManager, scanCallback = BleScanCallback({
                val name = it?.device?.name
                if (name.isNullOrBlank()) return@BleScanCallback

                val device = BleDevice(name)
                if (!bluetoothState.value.visibleDevices.contains(device)) {
                    bluetoothState.value.visibleDevices.add(device)
                    if (device.name == BleDevice.NAME_PRIVATETRAINER) {
                        bluetoothState.value =
                            bluetoothState.value.copy(selectedDevice = device)
                        val connectGatt = it.device.connectGatt(this, true, BleGattCallback())
                    }
                }
            })).scanBleDevices()
        }
    }
}


@Composable
fun BluetoothStatus(enabled: Boolean) {
    val indicatorColor =
        if (enabled) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.errorContainer
    val statusText = if (enabled) "aktiv" else "ausgeschaltet"
    Surface(color = indicatorColor) {
        Text(
            text = "Bluetooth ist $statusText!",
            modifier = Modifier.padding(all = 10.dp),
            fontSize = 24.sp
        )
    }
}

@SuppressLint("MissingPermission")
@Composable
fun BluetoothDeviceLine(device: BluetoothDevice) {
    Text(
        text = device.name,
        modifier = Modifier.padding(all = 8.dp),
        fontSize = 16.sp
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PrivateTrainerTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            BluetoothStatus(enabled = false)
        }
    }
}
