package org.voegtle.privatetrainer.business

import android.content.Context
import com.google.gson.Gson

class DeviceStore(context: Context) {
    val devicePreferences = context.getSharedPreferences(DEVICE_STORE, Context.MODE_PRIVATE)
    val gson: Gson = Gson()

    fun store(device:PrivateTrainerDevice) {
        val editor = devicePreferences.edit()
        val cleanedDevice = device.copy(available = false)
        editor.putString(device.address, devices2json(cleanedDevice))
        editor.apply()
    }

    fun retrieveDevices(): PrivateTrainerDeviceContainer {
         val devices = devicePreferences
             .all
             .map { entry -> json2device(entry.value as String) }
             .toList()
        val deviceMap = HashMap<String, PrivateTrainerDevice>()
        devices.forEach { entry -> deviceMap.put(entry.address, entry)}
        return PrivateTrainerDeviceContainer(deviceMap)
    }

    private fun json2device(deviceString: String): PrivateTrainerDevice {
        return gson.fromJson(deviceString, PrivateTrainerDevice::class.java)
    }

    private fun devices2json(device: PrivateTrainerDevice): String {
        return gson.toJson(device)
    }

    companion object {
        private const val DEVICE_STORE = "DEVICE_STORE"
    }

}
