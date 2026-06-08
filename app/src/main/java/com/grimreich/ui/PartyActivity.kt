package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grimreich.R
import com.grimreich.core.GameRepository

class PartyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party)

        findViewById<Button>(R.id.btnBackFromParty).setOnClickListener {
            finish()
        }

        val rvParty = findViewById<RecyclerView>(R.id.rvParty)
        rvParty.layoutManager = LinearLayoutManager(this)
        rvParty.adapter = PartyAdapter(GameRepository.state.party)
    }
}
