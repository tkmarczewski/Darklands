package com.grimreich.ui

import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.grimreich.R

object UiUtils {
    
    fun showNarrativePopup(
        context: Context, 
        title: String, 
        message: String, 
        onDismiss: (() -> Unit)? = null
    ) {
        val dialog = AlertDialog.Builder(context, R.style.Theme_GrimReich).create()
        val view = android.view.LayoutInflater.from(context).inflate(R.layout.dialog_narrative, null)
        
        view.findViewById<TextView>(R.id.dialogTitle).text = title.uppercase()
        view.findViewById<TextView>(R.id.dialogMessage).text = message
        
        val btn = view.findViewById<Button>(R.id.btnDismiss)
        btn.text = "ROZUMIEM"
        btn.setOnClickListener {
            dialog.dismiss()
            onDismiss?.invoke()
        }
        
        dialog.setView(view)
        dialog.setCancelable(false) // Force click
        dialog.show()
    }

    fun showChoicePopup(
        context: Context,
        title: String,
        message: String,
        positiveText: String = "TAK",
        negativeText: String = "NIE",
        onPositive: () -> Unit
    ) {
        val dialog = AlertDialog.Builder(context, R.style.Theme_GrimReich).create()
        val view = android.view.LayoutInflater.from(context).inflate(R.layout.dialog_narrative, null)
        
        view.findViewById<TextView>(R.id.dialogTitle).text = title.uppercase()
        view.findViewById<TextView>(R.id.dialogMessage).text = message
        
        val btnPos = view.findViewById<Button>(R.id.btnDismiss)
        btnPos.text = positiveText
        btnPos.setOnClickListener {
            dialog.dismiss()
            onPositive()
        }
        
        val btnNeg = view.findViewById<Button>(R.id.btnCancel)
        btnNeg.visibility = View.VISIBLE
        btnNeg.text = negativeText
        btnNeg.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.setView(view)
        dialog.setCancelable(false)
        dialog.show()
    }

    fun Button.styleToGrim() {
        this.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.grimGold))
        this.setBackgroundColor(android.graphics.Color.parseColor("#80000000"))
        this.setPadding(16, 16, 16, 16)
        val params = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 8)
        this.layoutParams = params
    }
}
