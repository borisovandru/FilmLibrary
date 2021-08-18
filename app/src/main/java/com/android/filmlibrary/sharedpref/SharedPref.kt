package com.android.filmlibrary.sharedpref

import android.content.Context
import android.content.SharedPreferences
import com.android.filmlibrary.Constant.NAME_SHARED_PREFERENCE
import com.android.filmlibrary.model.Settings


class SharedPref(context: Context) {
    private val sharedPreferences: SharedPreferences = context
        .getSharedPreferences(NAME_SHARED_PREFERENCE, Context.MODE_PRIVATE)

    // Чтение настроек
    fun loadSettings(): Settings {
        return Settings(sharedPreferences.getBoolean("Adult", false))
    }

    // Сохранение настроек
    fun saveSettings(settings: Settings) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("Adult", settings.adult)
        editor.apply()
    }
}