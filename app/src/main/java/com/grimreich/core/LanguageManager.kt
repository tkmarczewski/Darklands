package com.grimreich.core

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {
    private const val PREFS_NAME = "grimreich_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    enum class Language(val code: String, val displayName: String) {
        PL("pl", "Polski"),
        EN("en", "English"),
        DE("de", "Deutsch")
    }

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSavedLanguage(): Language {
        val code = prefs?.getString(KEY_LANGUAGE, "pl") ?: "pl"
        return Language.entries.find { it.code == code } ?: Language.PL
    }

    fun setLanguage(language: Language) {
        prefs?.edit()?.putString(KEY_LANGUAGE, language.code)?.apply()
    }

    fun applyLanguage(context: Context, language: Language) {
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        
        // This is deprecated but often necessary for immediate UI updates in legacy views
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    /**
     * Wraps context with the current locale for Compose/UI support.
     */
    fun wrapContext(context: Context): Context {
        val language = getSavedLanguage()
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun getAvailableLanguages(): List<Language> = Language.entries.toList()
}
