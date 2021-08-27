package com.android.filmlibrary.viewmodel.person

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.android.filmlibrary.Constant
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Person
import com.android.filmlibrary.model.repository.remote.RepositoryRemoteImpl
import com.android.filmlibrary.model.retrofit.PersonAPI

class PersonViewModel : ViewModel() {

    private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()
    private val repository = RepositoryRemoteImpl()

    fun getPersonStart(): LiveData<AppState> {
        return liveDataToObserver
    }

    private val callBackPerson = object :
        Callback<PersonAPI> {

        override fun onResponse(call: Call<PersonAPI>, response: Response<PersonAPI>) {
            val serverResponse: PersonAPI? = response.body()
            liveDataToObserver.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(Constant.SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<PersonAPI>, t: Throwable) {
            liveDataToObserver.postValue(
                AppState.Error(
                    Throwable(
                        t.message ?: Constant.REQUEST_ERROR
                    )
                )
            )
        }

        private fun checkResponse(serverResponse: PersonAPI): AppState {
            return if (serverResponse.id == -1) {
                AppState.Error(Throwable(Constant.CORRUPTED_DATA))
            } else {

                val personAPI = serverResponse
                val person = Person(
                    personAPI.adult,
                    personAPI.alsoKnownAs,
                    personAPI.biography,
                    personAPI.birthday,
                    personAPI.deathday,
                    personAPI.gender,
                    personAPI.homepage,
                    personAPI.id,
                    personAPI.imdbId,
                    personAPI.knownForDepartment,
                    personAPI.name,
                    personAPI.placeOfBirth,
                    personAPI.popularity,
                    personAPI.profilePath
                )
                AppState.SuccessGetPerson(
                    person
                )
            }
        }
    }

    fun getPersonFromRemoteSource(personId: Int) {
        liveDataToObserver.value = AppState.Loading
        repository.getPersonFromRemoteServerRetroFit(
            personId,
            Constant.LANG_VALUE, callBackPerson
        )
    }
}