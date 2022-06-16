package com.android.filmlibrary.domain.local.contact

import com.android.filmlibrary.CallbackMy
import com.android.filmlibrary.data.model.Contact

interface RepositoryLocalContact {
    fun getListOfContact(withPhone: Boolean, callbackMy: CallbackMy<List<Contact>>)
}