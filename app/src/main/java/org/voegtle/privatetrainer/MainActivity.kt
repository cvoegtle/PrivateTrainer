package org.voegtle.privatetrainer

import android.annotation.SuppressLint
import android.bluetooth.*
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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.voegtle.privatetrainer.business.BluetoothConnectionStatus.*
import org.voegtle.privatetrainer.business.BluetoothState
import org.voegtle.privatetrainer.business.DeviceSettings
import org.voegtle.privatetrainer.business.DeviceStore
import org.voegtle.privatetrainer.business.PrivateTrainerCommand
import org.voegtle.privatetrainer.business.PrivateTrainerDevice
import org.voegtle.privatetrainer.business.PrivateTrainerDeviceContainer
import org.voegtle.privatetrainer.business.SettingsStore
import org.voegtle.privatetrainer.business.bluetooth.*
import org.voegtle.privatetrainer.ui.theme.PrivateTrainerTheme

class MainActivity : ComponentActivity() {

    private val permissionsManager = PermissionManager(this)
    var bluetoothCaller: BluetoothCaller? = null

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ensureThreeDevices()
        setContent {
            PrivateTrainerTheme {
                val windowSize = calculateWindowSizeClass(this)
                val displayFeatures = calculateDisplayFeatures(this)
                val settingsStore = SettingsStore(this)

                PrivateTrainerApp(
                    windowSize = windowSize,
                    displayFeatures = displayFeatures,
                    savedDeviceSettings = settingsStore.retrieveFavoriteSettings(),
                    onSearchDeviceClicked = fun(bluetoothState: MutableState<BluetoothState>, devices: MutableState<PrivateTrainerDeviceContainer>) {
                        determineBluetoothState(bluetoothState, devices)
                    },
                    onSendToDeviceClicked = fun(
                        command: PrivateTrainerCommand,
                        settings: DeviceSettings
                    ) {
                        sendCommandToDevice(command, settings)
                    }
                )
            }
        }
    }

    private fun sendCommandToDevice(
        command: PrivateTrainerCommand,
        settings: DeviceSettings
    ) {
        bluetoothCaller!!.sendToDevice(command, settings)
    }

    private fun determineBluetoothState(bluetoothState: MutableState<BluetoothState>, devices: MutableState<PrivateTrainerDeviceContainer>) {

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
                checkPermissions(BluetoothPermissions().permissions())
                doOnDenied {
                    bluetoothState.value.connectionStatus = permission_denied
                }
                doOnGranted {
                    scanBluetoothDeviceStatus(bluetoothManager, bluetoothState, devices)
                }
            }
        }
        permissionsManager.checkRequestAndDispatch(BluetoothPermissions().code)
    }

    @SuppressLint("MissingPermission")
    private fun scanBluetoothDeviceStatus(
        bluetoothManager: BluetoothManager,
        bluetoothState: MutableState<BluetoothState>,
        devices: MutableState<PrivateTrainerDeviceContainer>
    ) {
        resetDevices(devices)

        BluetoothScanner(bluetoothManager, bluetoothState.value).scanForPrivateTrainer {
            MainScope().launch {
                bluetoothState.value = bluetoothState.value.copy(connectionStatus = device_found)

                val updatedDevices = devices.value.copy()
                updatedDevices.found(it.address)
                devices.value = updatedDevices
            }
        }
    }

    private fun resetDevices(devices: MutableState<PrivateTrainerDeviceContainer>) {
        MainScope().launch {
            val resettedDevices = devices.value.copy()
            resettedDevices.resetAvailabilty()
            devices.value = resettedDevices
        }
    }

    fun isBluetoothDeviceConnected() = bluetoothCaller?.isConnected() ?: false

    private fun ensureThreeDevices() {
        val devices = DeviceStore(this).retrieveDevices()
        for (i in devices.size() .. 2) {
            val device = PrivateTrainerDevice(givenName = "Device $i", address = "address $i")
            DeviceStore(this).store(device)
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


