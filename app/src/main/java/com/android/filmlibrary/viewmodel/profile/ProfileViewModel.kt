package com.android.filmlibrary.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.android.filmlibrary.Constant
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.SettingsTMDB
import com.android.filmlibrary.model.repository.RepositoryImpl
import com.android.filmlibrary.model.retrofit.ConfigurationAPI

class ProfileViewModel(private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel() {

    private val repository = RepositoryImpl()

    fun getData(): LiveData<AppState> {
        Log.v("Debug1", "ProfileViewModel getData")
        return liveDataToObserver
    }

    private val callBack = object :
        Callback<ConfigurationAPI> {

        override fun onResponse(
            call: Call<ConfigurationAPI>,
            response: Response<ConfigurationAPI>,
        ) {
            Log.v("Debug1", "MovieInfoViewModel onResponse")
            val serverResponse: ConfigurationAPI? = response.body()
            liveDataToObserver.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(Constant.SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<ConfigurationAPI>, t: Throwable) {
            Log.v("Debug1", "MovieInfoViewModel onFailure")
            liveDataToObserver.postValue(
                AppState.Error(
                    Throwable(
                        t.message
                            ?: Constant.REQUEST_ERROR
                    )
                )
            )
        }

        private fun checkResponse(serverResponse: ConfigurationAPI): AppState {
            Log.v("Debug1", "MovieInfoViewModel checkResponse")
            return if (serverResponse.images.baseURL == "") {
                AppState.Error(Throwable(Constant.CORRUPTED_DATA))
            } else {

                val settingsData = SettingsTMDB(

                    serverResponse.images.baseURL,
                    serverResponse.images.secureBaseURL
                )

                AppState.SuccessSettings(
                    settingsData
                )
            }
        }
    }

    fun getDataFromRemoteSource() {
        Log.v("Debug1", "ProfileViewModel getDataFromRemoteSource")
        liveDataToObserver.value = AppState.Loading
        repository.getSettingsFromRemoteServerRetroFit(
            Constant.LANG_VALUE,
            callBack
        )
    }
}