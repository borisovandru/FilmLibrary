package com.android.filmlibrary.presentation.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.utils.GlobalVariables.Companion.contactsCache
import com.android.filmlibrary.utils.AppState
import com.android.filmlibrary.utils.CallbackMy
import com.android.filmlibrary.data.model.Contact
import com.android.filmlibrary.domain.local.contact.RepositoryLocalContactImpl

class ProfileViewModel : ViewModel() {

    private val liveDataToObserverContact: MutableLiveData<AppState> = MutableLiveData()

    private val repositoryLocal = RepositoryLocalContactImpl()

    private var contacts: List<Contact> = ArrayList()

    fun getContactsStart(): LiveData<AppState> {
        Log.v("Debug1", "ProfileViewModel getContactsStart")
        return liveDataToObserverContact
    }

    private val callBackMy = object :
        CallbackMy<List<Contact>> {
        override fun onSuccess(result: List<Contact>) {
            Log.v("Debug1", "ProfileViewModel onSuccess")
            contactsCache = result
            liveDataToObserverContact.postValue(AppState.SuccessGetContacts(result))
        }
    }

    fun getContacts(withPhone: Boolean) {
        Log.v("Debug1", "ProfileViewModel getContacts")
        if (contactsCache.isNotEmpty()) {
            contacts = contactsCache
            liveDataToObserverContact.postValue(AppState.SuccessGetContacts(contacts))
        } else {
            liveDataToObserverContact.value = AppState.Loading
            repositoryLocal.getListOfContact(withPhone, callBackMy)
        }
    }
}