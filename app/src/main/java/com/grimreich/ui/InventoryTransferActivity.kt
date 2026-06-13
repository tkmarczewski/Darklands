package com.grimreich.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.InventorySystem

class InventoryTransferActivity : AppCompatActivity() {
    private var fromHeroId: String? = null
    private var toHeroId: String? = null
    private var selectedItemId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_transfer)

        render()

        findViewById<Button>(R.id.btnDoTransfer).setOnClickListener {
            if (fromHeroId != null && toHeroId != null && selectedItemId != null) {
                if (fromHeroId == toHeroId) {
                    Toast.makeText(this, "Nie możesz przesłać przedmiotu do tej samej osoby.", Toast.LENGTH_SHORT).show()
                } else {
                    // Logic: moving item in shared inventory implies changing ownership or equipment
                    // Since inventory is shared, transferItem primarily handles un-equipping if needed.
                    val res = InventorySystem.transferItem(fromHeroId!!, toHeroId!!, selectedItemId!!)
                    Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
                    render()
                }
            } else {
                Toast.makeText(this, "Wybierz nadawcę, odbiorcę i przedmiot.", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnExitTransfer).setOnClickListener {
            finish()
        }
    }

    private fun render() {
        val party = GameRepository.state.party
        val statusTv = findViewById<TextView>(R.id.transferStatus)
        val transferUi = findViewById<View>(R.id.llTransferUI)

        if (party.size < 2) {
            statusTv.text = "Potrzebujesz co najmniej dwóch bohaterów do transferu przedmiotów."
            transferUi.visibility = View.GONE
        } else {
            statusTv.text = "Transfer przedmiotów w obrębie drużyny."
            transferUi.visibility = View.VISIBLE
            setupHeroRadios()
            setupItems()
        }
    }

    private fun setupHeroRadios() {
        val rgFrom = findViewById<RadioGroup>(R.id.rgFrom)
        val rgTo = findViewById<RadioGroup>(R.id.rgTo)
        rgFrom.removeAllViews()
        rgTo.removeAllViews()

        GameRepository.state.party.forEach { hero ->
            val rbFrom = RadioButton(this).apply {
                text = hero.name
                id = View.generateViewId()
                tag = hero.id
                setTextColor(android.graphics.Color.WHITE)
            }
            val rbTo = RadioButton(this).apply {
                text = hero.name
                id = View.generateViewId()
                tag = hero.id
                setTextColor(android.graphics.Color.WHITE)
            }
            rgFrom.addView(rbFrom)
            rgTo.addView(rbTo)
        }

        rgFrom.setOnCheckedChangeListener { group, checkedId ->
            fromHeroId = group.findViewById<RadioButton>(checkedId)?.tag as? String
        }
        rgTo.setOnCheckedChangeListener { group, checkedId ->
            toHeroId = group.findViewById<RadioButton>(checkedId)?.tag as? String
        }
    }

    private fun setupItems() {
        val container = findViewById<LinearLayout>(R.id.llItemRadioContainer)
        container.removeAllViews()
        val rg = RadioGroup(this)
        container.addView(rg)

        val inventory = GameRepository.state.inventory
        if (inventory.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "Brak przedmiotów w plecaku."
                setTextColor(android.graphics.Color.GRAY)
            }
            container.addView(emptyTv)
            return
        }

        inventory.forEach { item ->
            val rb = RadioButton(this).apply {
                text = "${item.name} (${item.type})"
                id = View.generateViewId()
                tag = item.id
                setTextColor(android.graphics.Color.WHITE)
                setOnClickListener {
                    selectedItemId = item.id
                }
            }
            rg.addView(rb)
        }
    }
}
