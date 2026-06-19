package com.grimreich

import android.app.Application
import com.grimreich.core.EchoSystem
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GrimReichApp : Application() {

    @Inject lateinit var echoSystem: EchoSystem

    override fun onCreate() {
        super.onCreate()
        echoSystem.init(this)
    }
}
