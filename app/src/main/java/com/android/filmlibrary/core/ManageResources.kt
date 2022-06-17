package com.android.filmlibrary.core

import android.content.Context
import androidx.annotation.StringRes

/**
 * @author Borisov Andrey on 16.06.2022
 **/
interface ManageResources {

    fun string(@StringRes id: Int): String

    class Base(private val context: Context) : ManageResources {
        override fun string(id: Int) = context.getString(id)
    }
}