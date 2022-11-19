package org.voegtle.privatetrainer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.adaptive.calculateDisplayFeatures
import org.voegtle.privatetrainer.business.PrivateTrainerState
import org.voegtle.privatetrainer.business.PrivateTrainerViewModel
import org.voegtle.privatetrainer.ui.theme.PrivateTrainerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PrivateTrainerViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalLifecycleComposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
//            val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
//            if (bluetoothAdapter == null) {
//                // Device doesn't support Bluetooth
//            }
//            val bluetoothEnabled = bluetoothAdapter!!.isEnabled
//
//            val requestPermissionLauncher =
//                registerForActivityResult(
//                    ActivityResultContracts.RequestPermission()
//                ) { isGranted: Boolean ->
//                    if (isGranted) {
//                        val bondedDevices = bluetoothAdapter?.bondedDevices
//                        showBondedDevices(bluetoothEnabled, bondedDevices)
//                    } else {
//                        showBluetoothPermissionDenied()
//                    }
//                }
//
//            if (ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.BLUETOOTH_CONNECT
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                requestPermissionLauncher.launch(
//                    Manifest.permission.BLUETOOTH_CONNECT
//                )
//            } else {
//                val bondedDevices = bluetoothAdapter?.bondedDevices
//                showBondedDevices(bluetoothEnabled, bondedDevices)
//            }

            PrivateTrainerTheme {
                val windowSize = calculateWindowSizeClass(this)
                val displayFeatures = calculateDisplayFeatures(this)
                val uiState by viewModel.privateTrainerState.collectAsStateWithLifecycle()

                PrivateTrainerApp(
                    windowSize = windowSize,
                    displayFeatures = displayFeatures,
                    privateTrainerState = uiState,
                    closeDetailScreen = {
                        viewModel.closeDetailScreen()
                    },
                    navigateToDetail = { emailId, pane -> {}
                    }
                )
            }
        }
    }

}

//    private fun showBondedDevices(
//        bluetoothEnabled: Boolean,
//        bondedDevices: MutableSet<BluetoothDevice>
//    ) {
//        setContent {
//            PrivateTrainerTheme {
//                Column(
//                    modifier = Modifier.fillMaxSize(),
//                ) {
//                    BluetoothStatus(enabled = bluetoothEnabled)
//                    bondedDevices?.forEach { device ->
//                        BluetoothDeviceLine(device = device)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun showBluetoothPermissionDenied() {
//        setContent {
//            PrivateTrainerTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                ) {
//                    BluetoothPermissionRequired()
//                }
//            }
//        }
//    }
//}

@Composable
fun BluetoothPermissionRequired() {
    Surface(color = MaterialTheme.colorScheme.errorContainer) {
        Text(
            text = "Sorry, ohne Bluetooth kann ich nichts für Dich tun",
            modifier = Modifier.padding(all = 10.dp),
            fontSize = 24.sp
        )
    }

}

@Composable
fun BluetoothStatus(enabled: Boolean) {
    val indicatorColor = if (enabled) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.errorContainer
    val statusText = if (enabled) "aktiv" else "ausgeschaltet"
    Surface(color = indicatorColor) {
        Text(
            text = "Bluetooth ist $statusText!",
            modifier = Modifier.padding(all = 10.dp),
            fontSize = 24.sp
        )
    }
}

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
