package com.grimreich.ui

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.core.LanguageManager
import java.util.Locale

abstract class LocalizedActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val language = LanguageManager.getSavedLanguage()
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val newContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(newContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val language = LanguageManager.getSavedLanguage()
        val locale = Locale(language.code)
        val config = Configuration(newConfig)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
