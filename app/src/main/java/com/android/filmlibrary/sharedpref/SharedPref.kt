package com.android.filmlibrary.sharedpref

import android.content.Context
import android.content.SharedPreferences
import com.android.filmlibrary.Constant.NAME_SHARED_PREFERENCE
import com.android.filmlibrary.R
import com.android.filmlibrary.model.Settings

class SharedPref(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(NAME_SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val contextLoc = context

    // Чтение настроек
    fun loadSettings(): Settings {
        return Settings(
            sharedPreferences.getBoolean(contextLoc.getString(R.string.settingsAdult), false),
            sharedPreferences.getBoolean(contextLoc.getString(R.string.withPhoneShared), false),
        )
    }

    // Сохранение настроек
    fun saveSettings(settings: Settings) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(contextLoc.getString(R.string.settingsAdult), settings.adult)
        editor.putBoolean(contextLoc.getString(R.string.withPhoneShared), settings.withPhone)
        editor.apply()
    }
}