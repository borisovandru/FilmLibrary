package com.android.filmlibrary.model.repository.local.contact

import com.android.filmlibrary.model.data.CallbackMy
import com.android.filmlibrary.model.data.Contact

interface RepositoryLocalContact {
    fun getListOfContact(withPhone: Boolean, callbackMy: CallbackMy<List<Contact>>)
}