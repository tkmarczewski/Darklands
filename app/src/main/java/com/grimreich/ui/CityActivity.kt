package com.grimreich.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * DEPRECATED: City functionality migrated to Single Activity flow in MainActivity.
 * This class exists only for backward compatibility during transition.
 */
class CityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Redirect to MainActivity to force the new flow
        finish()
    }
}
