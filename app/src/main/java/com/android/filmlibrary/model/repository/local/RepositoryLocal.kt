package com.android.filmlibrary.model.repository.local

import com.android.filmlibrary.model.data.Contact

interface RepositoryLocal {
    fun getListOfContact(withPhone: Boolean): List<Contact>
}