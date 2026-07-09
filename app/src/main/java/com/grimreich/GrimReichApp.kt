package com.grimreich

import android.app.Application
import com.grimreich.core.LanguageManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GrimReichApp : Application() {

    override fun onCreate() {
        super.onCreate()
        LanguageManager.init(this)
    }
}
