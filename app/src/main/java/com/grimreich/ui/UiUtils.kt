package com.grimreich.ui

import android.content.Context
import androidx.appcompat.app.AlertDialog

object UiUtils {
    fun showNarrativePopup(context: Context, title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("ROZUMIEM", null)
            .show()
    }
}
