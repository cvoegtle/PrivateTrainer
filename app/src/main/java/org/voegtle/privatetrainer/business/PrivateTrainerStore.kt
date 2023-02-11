package org.voegtle.privatetrainer.business

import android.content.Context
import com.google.gson.Gson

private const val STORE = "PRIVATE_TRAINER_STORE"
private const val SAVED_SETTINGS = "SAVED_SETTINGS"
private const val CURRENT_SETTINGS = "CURRENT_SETTINGS"

class PrivateTrainerStore(context: Context) {
    val sharedPreferences = context.getSharedPreferences(STORE, Context.MODE_PRIVATE)
    val gson: Gson = Gson()

    fun retrieveCurrentSettings(): DeviceSettings {
        val jsonSettings = sharedPreferences.getString(CURRENT_SETTINGS, null)
        return if (jsonSettings != null) json2settings(jsonSettings) else DeviceSettings()
    }

    fun storeCurrentSettings(deviceSettings: DeviceSettings) {
        val editor = sharedPreferences.edit()
        editor.putString(CURRENT_SETTINGS, settings2json(deviceSettings))
        editor.apply()
    }



    private fun json2settings(settingsString: String): DeviceSettings {
        return gson.fromJson(settingsString, DeviceSettings::class.java)
    }

    private fun settings2json(deviceSettings: DeviceSettings): String {
        return gson.toJson(deviceSettings)
    }
}
