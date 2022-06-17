package com.android.filmlibrary.domain.local.contact

import com.android.filmlibrary.utils.CallbackMy
import com.android.filmlibrary.data.model.Contact

interface RepositoryLocalContact {
    fun getListOfContact(withPhone: Boolean, callbackMy: CallbackMy<List<Contact>>)
}