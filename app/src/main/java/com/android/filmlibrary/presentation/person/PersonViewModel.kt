package com.android.filmlibrary.presentation.person

import com.android.filmlibrary.data.retrofit.PersonAPI
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.Constant
import com.android.filmlibrary.AppState
import com.android.filmlibrary.data.model.Person
import com.android.filmlibrary.domain.remote.RepositoryRemoteImpl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

                val person = Person(
                    serverResponse.adult,
                    serverResponse.alsoKnownAs,
                    serverResponse.biography,
                    serverResponse.birthday,
                    serverResponse.deathday,
                    serverResponse.gender,
                    serverResponse.homepage,
                    serverResponse.id,
                    serverResponse.imdbId,
                    serverResponse.knownForDepartment,
                    serverResponse.name,
                    serverResponse.placeOfBirth,
                    serverResponse.popularity,
                    serverResponse.profilePath
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