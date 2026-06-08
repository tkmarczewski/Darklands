package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository

class PartyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party)

        findViewById<Button>(R.id.btnBackFromParty).setOnClickListener {
            finish()
        }
        
        // TODO: Implement RecyclerView Adapter for rvParty
    }
}
