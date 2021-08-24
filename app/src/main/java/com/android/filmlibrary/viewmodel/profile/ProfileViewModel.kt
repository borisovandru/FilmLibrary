package com.android.filmlibrary.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.android.filmlibrary.Constant
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.SettingsIMDB
import com.android.filmlibrary.model.repository.local.RepositoryLocalImpl
import com.android.filmlibrary.model.repository.remote.RepositoryRemoteImpl
import com.android.filmlibrary.model.retrofit.ConfigurationAPI

class ProfileViewModel(private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel() {

    private val repositoryRemoteDB = RepositoryRemoteImpl()
    private val repositoryLocal = RepositoryLocalImpl()
    val contacts: MutableLiveData<AppState> = MutableLiveData()

    fun getData(): LiveData<AppState> {
        return liveDataToObserver
    }

    private val callBack = object :
        Callback<ConfigurationAPI> {

        override fun onResponse(
            call: Call<ConfigurationAPI>,
            response: Response<ConfigurationAPI>,
        ) {
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
            return if (serverResponse.images.baseURL == "") {
                AppState.Error(Throwable(Constant.CORRUPTED_DATA))
            } else {

                val settingsData = SettingsIMDB(

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
        liveDataToObserver.value = AppState.Loading
        repositoryRemoteDB.getSettingsFromRemoteServerRetroFit(
            Constant.LANG_VALUE,
            callBack
        )
    }

    fun getContacts(withPhone: Boolean) {
        contacts.value = AppState.Loading
        val answer = repositoryLocal.getListOfContact(withPhone)
        contacts.value = AppState.SuccessGetContacts(answer)
    }
}