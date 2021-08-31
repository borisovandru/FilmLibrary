package com.android.filmlibrary.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.CallbackMy
import com.android.filmlibrary.model.data.Contact
import com.android.filmlibrary.model.repository.local.contact.RepositoryLocalContactImpl

class ProfileViewModel : ViewModel() {

    private val liveDataToObserverContact: MutableLiveData<AppState> = MutableLiveData()

    private val repositoryLocal = RepositoryLocalContactImpl()

    private var contacts: List<Contact> = ArrayList()

    fun getContactsStart(): LiveData<AppState> {
        return liveDataToObserverContact
    }

    private val callBackMy = object :
        CallbackMy<List<Contact>> {
        override fun onSuccess(result: List<Contact>) {

            liveDataToObserverContact.postValue(AppState.SuccessGetContacts(result))
        }
    }

    fun getContacts(withPhone: Boolean) {

        liveDataToObserverContact.value = AppState.Loading
        repositoryLocal.getListOfContact(withPhone, callBackMy)
    }
}