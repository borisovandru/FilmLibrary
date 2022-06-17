package com.android.filmlibrary.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBar(
    text: String,
    actionTextId: Int,
    action: (View) -> Unit

) {
    Snackbar.make(
        this,
        text,
        Snackbar.LENGTH_INDEFINITE
    )
        .setAction(resources.getString(actionTextId), action).show()
}

fun View.show(): View {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
    return this
}

fun View.hide(): View {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
    return this
}
