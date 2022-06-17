package com.android.filmlibrary.data

import android.content.Context
import android.content.SharedPreferences
import com.android.filmlibrary.utils.Constant.NAME_SHARED_PREFERENCE
import com.android.filmlibrary.R
import com.android.filmlibrary.utils.Settings

class SharedPref(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(NAME_SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val contextLoc = context

    // Чтение настроек
    fun readSettings(): Settings {
        return Settings(
            sharedPreferences.getBoolean(contextLoc.getString(R.string.settingsAdult), false),
            sharedPreferences.getBoolean(contextLoc.getString(R.string.withPhoneShared), false),
            sharedPreferences.getBoolean(contextLoc.getString(R.string.geoFenceShared), false),
        )
    }
    // Сохранение настроек
    fun saveSettings(settings: Settings) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(contextLoc.getString(R.string.settingsAdult), settings.adult)
        editor.putBoolean(contextLoc.getString(R.string.withPhoneShared), settings.withPhone)
        editor.putBoolean(contextLoc.getString(R.string.geoFenceShared), settings.geoFence)
        editor.apply()
    }
}