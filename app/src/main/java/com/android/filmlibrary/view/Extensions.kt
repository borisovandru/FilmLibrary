package com.android.filmlibrary.view

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