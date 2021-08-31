package com.android.filmlibrary.model.repository.local.contact

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import com.android.filmlibrary.ContextProvider
import com.android.filmlibrary.IContextProvider
import com.android.filmlibrary.model.data.CallbackMy
import com.android.filmlibrary.model.data.Contact
import java.util.concurrent.Executors

class RepositoryLocalContactImpl(contextProvider: IContextProvider = ContextProvider) :
    RepositoryLocalContact {
    private val contentResolver: ContentResolver = contextProvider.context.contentResolver
    private val executor = Executors.newCachedThreadPool()

    override fun getListOfContact(withPhone: Boolean, callbackMy: CallbackMy<List<Contact>>) {
        executor.execute {
            val selection = ContactsContract.Contacts.DISPLAY_NAME + " IS NOT NULL " +
                    if (withPhone) {
                        "AND " + ContactsContract.Data.HAS_PHONE_NUMBER + " = '" + 1 + "'"
                    } else {
                        ""
                    }

            val cursorWithContacts: Cursor? = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                selection,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )
            val answer = mutableListOf<Contact>()

            cursorWithContacts?.let { cursor ->
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {

                    val contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                    var hasPhone =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    hasPhone = if (hasPhone == "1") "true" else "false"

                    val phoneNumbers = mutableListOf<String>()

                    val idArray: Array<String> = arrayOf()

                    if (hasPhone.toBoolean()) {

                        val cursorPhones: Cursor? = contentResolver.query(
                            Phone.CONTENT_URI,
                            null,
                            Phone.CONTACT_ID + " = " + contactId,
                            idArray,
                            null
                        )

                        cursorPhones?.let { cursorPhoneLoc ->
                            cursorPhoneLoc.moveToFirst()
                            while (!cursorPhoneLoc.isAfterLast) {
                                val numb =
                                    cursorPhoneLoc.getString(cursorPhoneLoc.getColumnIndex(Phone.NUMBER))
                                phoneNumbers.add(numb)
                                cursorPhoneLoc.moveToNext()
                            }
                            cursorPhones.close()
                        }
                    }
                    answer.add(
                        Contact(
                            name,
                            phoneNumbers
                        )
                    )
                    cursor.moveToNext()
                }
                cursorWithContacts.close()
            }
            callbackMy.onSuccess(answer)
        }
    }
}