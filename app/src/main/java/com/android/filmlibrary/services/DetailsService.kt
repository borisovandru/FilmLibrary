package com.android.filmlibrary.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.filmlibrary.Constant.DETAILS_INTENT_EMPTY_EXTRA
import com.android.filmlibrary.Constant.DETAILS_INTENT_FILTER
import com.android.filmlibrary.Constant.DETAILS_LOAD_RESULT_EXTRA
import com.android.filmlibrary.Constant.DETAILS_REQUEST_ERROR_EXTRA
import com.android.filmlibrary.Constant.DETAILS_REQUEST_ERROR_MESSAGE_EXTRA
import com.android.filmlibrary.Constant.DETAILS_RESPONSE_SUCCESS_EXTRA
import com.android.filmlibrary.Constant.SETTINGS_TMDB_IMAGE_SECURE_URL
import com.android.filmlibrary.Constant.SETTINGS_TMDB_IMAGE_URL
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.SettingsTMDB
import com.android.filmlibrary.model.repository.RepositoryImpl

class DetailsService(name: String = "DetailService") : IntentService(name) {

    private val broadcastIntent = Intent(DETAILS_INTENT_FILTER)
    private val repository = RepositoryImpl()

    override fun onHandleIntent(intent: Intent?) {
        Log.v("Debug1", "DetailsService onHandleIntent")
        if (intent == null) {
            onEmptyIntent()
        } else {
            loadSettings()
        }
    }

    private fun loadSettings() {
        Log.v("Debug1", "DetailsService loadSettings")
        try {
            val appState = repository.getSettingsFromRemoteServer()
            onResponse(appState)
        } catch (e: Exception) {
            onErrorRequest(e.message ?: "Empty error")
        }
    }


    private fun onResponse(appState: AppState) {
        Log.v("Debug1", "DetailsService onResponse")
        when (appState) {
            is AppState.SuccessSettings -> {
                val settingsData = appState.settingsTMDB
                onSuccessResponse(settingsData)
            }
            is AppState.Error -> {
                appState.error.message?.let { onErrorRequest(it) }
            }
        }
    }

    private fun onSuccessResponse(settingsData: SettingsTMDB) {
        Log.v("Debug1", "DetailsService onSuccessResponse")
        putLoadResult(DETAILS_RESPONSE_SUCCESS_EXTRA)
        broadcastIntent.putExtra(SETTINGS_TMDB_IMAGE_URL, settingsData.imageBaseURL)
        broadcastIntent.putExtra(SETTINGS_TMDB_IMAGE_SECURE_URL, settingsData.imageSecureBaseURL)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun onErrorRequest(error: String) {
        Log.v("Debug1", "DetailsService onErrorRequest")
        putLoadResult(DETAILS_REQUEST_ERROR_EXTRA)
        broadcastIntent.putExtra(DETAILS_REQUEST_ERROR_MESSAGE_EXTRA, error)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun onEmptyIntent() {
        Log.v("Debug1", "DetailsService onEmptyIntent")
        putLoadResult(DETAILS_INTENT_EMPTY_EXTRA)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun putLoadResult(result: String) {
        Log.v("Debug1", "DetailsService putLoadResult")
        broadcastIntent.putExtra(DETAILS_LOAD_RESULT_EXTRA, result)

    }


}