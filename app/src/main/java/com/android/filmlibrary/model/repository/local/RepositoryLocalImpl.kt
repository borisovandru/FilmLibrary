package com.android.filmlibrary.model.repository.local

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import com.android.filmlibrary.ContextProvider
import com.android.filmlibrary.IContextProvider

class RepositoryLocalImpl(contextProvider: IContextProvider = ContextProvider) : RepositoryLocal {

    private val contentResolver: ContentResolver = contextProvider.context.contentResolver

    override fun getListOfContact(): List<String> {
        val cursorWithContacts: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        val answer = mutableListOf<String>()

        cursorWithContacts?.let { cursor ->
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                answer.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)))
                cursor.moveToNext()
            }
            cursorWithContacts.close()
        }
        return answer
    }
}
