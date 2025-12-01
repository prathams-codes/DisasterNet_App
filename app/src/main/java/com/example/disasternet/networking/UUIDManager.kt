package com.example.disasternet.networking

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object UUIDManager {

    private const val PREFS_NAME = "DisasterNetPrefs"
    private const val PREF_KEY_UUID = "device_uuid"
    private var deviceUUID: String? = null

    fun getUUID(context: Context): String {
        if (deviceUUID != null) {
            return deviceUUID!!
        }

        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var uuid = prefs.getString(PREF_KEY_UUID, null)

        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            prefs.edit().putString(PREF_KEY_UUID, uuid).apply()
        }

        deviceUUID = uuid
        return uuid
    }
}