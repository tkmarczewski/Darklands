package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.InventorySystem

class EquipmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipment)

        render()

        findViewById<Button>(R.id.btnBackEquip).setOnClickListener { finish() }
    }

    private fun render() {
        val hero = GameRepository.state.party.firstOrNull() ?: return
        val gear = InventorySystem.getEquippedItems(hero)
        val ivWeapon = findViewById<ImageView>(R.id.ivWeaponSlot)
        val ivArmor = findViewById<ImageView>(R.id.ivArmorSlot)
        val tvStats = findViewById<TextView>(R.id.tvEquipmentStats)

        tvStats.text = buildString {
            appendLine("Całkowity Atak: ${gear.totalAttack()}")
            appendLine("Całkowita Obrona: ${gear.totalDefense()}")
            appendLine("Waga Ekwipunku: ${gear.totalWeight()} kg")
        }
        
        // Visual indicator if item is equipped
        if (gear.weapon != null) ivWeapon.alpha = 1.0f else ivWeapon.alpha = 0.3f
        if (gear.bodyArmor != null) ivArmor.alpha = 1.0f else ivArmor.alpha = 0.3f
    }
}
