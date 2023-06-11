package org.voegtle.privatetrainer.business

import android.content.Context
import com.google.gson.Gson

class SettingsStore(context: Context) {
    val shorttimePreferences = context.getSharedPreferences(CURRENT_STORE, Context.MODE_PRIVATE)
    val longtimetimePreferences = context.getSharedPreferences(FAVORITE_STORE, Context.MODE_PRIVATE)
    val gson: Gson = Gson()

    fun storeSettings(deviceSettings: DeviceSettings) {
        storeCurrentSettings(deviceSettings)
        if (deviceSettings.isFavorite()) {
            storeFavoriteSettings(deviceSettings)
        }
    }
    fun retrieveCurrentSettings(): DeviceSettings {
        val jsonSettings = shorttimePreferences.getString(CURRENT_SETTINGS, null)
        return if (jsonSettings != null) json2settings(jsonSettings) else DeviceSettings()
    }

    private fun storeCurrentSettings(deviceSettings: DeviceSettings) {
        val editor = shorttimePreferences.edit()
        editor.putString(CURRENT_SETTINGS, settings2json(deviceSettings))
        editor.apply()
    }

    private fun storeFavoriteSettings(deviceSettings: DeviceSettings) {
        assert(deviceSettings.id != null)
        val editor = longtimetimePreferences.edit()
        editor.putString(deviceSettings.id, settings2json(deviceSettings))
        editor.apply()
    }

    fun retrieveFavoriteSettings(): List<DeviceSettings> {
        return longtimetimePreferences.all
            .map { entry -> json2settings(entry.value as String)}
            .toList()
    }



    private fun json2settings(settingsString: String): DeviceSettings {
        return gson.fromJson(settingsString, DeviceSettings::class.java)
    }

    private fun settings2json(deviceSettings: DeviceSettings): String {
        return gson.toJson(deviceSettings)
    }

    companion object {
        private const val CURRENT_STORE = "CURRENT_STORE"
        private const val FAVORITE_STORE = "FAVORITE_STORE"
        private const val CURRENT_SETTINGS = "CURRENT_SETTINGS"
    }
}
