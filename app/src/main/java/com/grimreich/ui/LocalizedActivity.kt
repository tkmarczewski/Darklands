package com.grimreich.ui

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.core.LanguageManager
import java.util.Locale

abstract class LocalizedActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val wrappedContext = LanguageManager.wrapContext(newBase)
        super.attachBaseContext(wrappedContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LanguageManager.applyLanguage(this, LanguageManager.getSavedLanguage())
    }
}
