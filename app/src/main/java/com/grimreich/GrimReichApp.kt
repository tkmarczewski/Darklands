package com.grimreich

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GrimReichApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
