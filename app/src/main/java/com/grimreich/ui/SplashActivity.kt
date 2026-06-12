package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.EchoSystem
import com.grimreich.world.CityCatalogue

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // INITIALIZE PERSISTENT SYSTEMS
        EchoSystem.init(this)
        CityCatalogue.seedCanonical()

        // Show splash for 2 seconds then move to Main Menu
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }, 2000)
    }
}
