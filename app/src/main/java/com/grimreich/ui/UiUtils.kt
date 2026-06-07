package com.grimreich.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.grimreich.R

object UiUtils {
    
    fun showNarrativePopup(context: Context, title: String, message: String, onDismiss: (() -> Unit)? = null) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_narrative, null)
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(view)
            .setCancelable(false)
            .create()

        view.findViewById<TextView>(R.id.dialogTitle).text = title
        view.findViewById<TextView>(R.id.dialogMessage).text = message
        
        view.findViewById<Button>(R.id.btnDismiss).setOnClickListener {
            dialog.dismiss()
            onDismiss?.invoke()
        }

        dialog.show()
    }
}
