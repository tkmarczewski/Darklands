package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.AbilityRegistry
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.core.HeroSkill
import com.grimreich.core.Trait
import com.grimreich.systems.GameLoopController
import java.util.UUID

class CharacterCreatorActivity : AppCompatActivity() {

    private var pointsRemaining = 20
    private val attributes = mutableMapOf(
        "str" to 10, "agi" to 10, "per" to 10, 
        "int" to 10, "end" to 10, "cha" to 10, "pie" to 10
    )
    private var selectedTrait: Trait? = null
    private val specializedSkills = mutableSetOf<HeroSkill>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_creator)

        setupAttributeControls()
        setupTraitSelection()
        setupSkillSpecializations()

        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            val name = findViewById<EditText>(R.id.etHeroName).text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Wpisz imię bohatera!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedTrait == null) {
                Toast.makeText(this, "Wybierz cechę!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (specializedSkills.size != 3) {
                Toast.makeText(this, "Wybierz dokładnie 3 specjalizacje!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            finalizeCharacter(name)
        }
        
        updateUi()
    }

    private fun setupAttributeControls() {
        setupAttrRow(R.id.layoutStr, "str", "Siła")
        setupAttrRow(R.id.layoutAgi, "agi", "Zręczność")
        setupAttrRow(R.id.layoutPer, "per", "Percepcja")
        setupAttrRow(R.id.layoutInt, "int", "Inteligencja")
        setupAttrRow(R.id.layoutEnd, "end", "Wytrzymałość")
        setupAttrRow(R.id.layoutCha, "cha", "Charyzma")
        setupAttrRow(R.id.layoutPie, "pie", "Pobożność")
    }

    private fun setupAttrRow(layoutId: Int, key: String, label: String) {
        val layout = findViewById<View>(layoutId)
        layout.findViewById<TextView>(R.id.tvAttrLabel).text = label
        layout.findViewById<Button>(R.id.btnPlus).setOnClickListener { changeAttr(key, 1) }
        layout.findViewById<Button>(R.id.btnMinus).setOnClickListener { changeAttr(key, -1) }
    }

    private fun setupTraitSelection() {
        val rgTraits = findViewById<RadioGroup>(R.id.rgTraits)
        val tvTraitDesc = findViewById<TextView>(R.id.tvTraitDesc)

        rgTraits.setOnCheckedChangeListener { _, checkedId ->
            selectedTrait = when (checkedId) {
                R.id.rbTrait1 -> Trait.GIFT_OF_MIST
                R.id.rbTrait2 -> Trait.IRON_HEART
                R.id.rbTrait3 -> Trait.SOLAR_EYE
                else -> null
            }
            tvTraitDesc.text = selectedTrait?.description ?: "Wybierz cechę, aby zobaczyć opis."
        }
    }

    private fun setupSkillSpecializations() {
        val container = findViewById<LinearLayout>(R.id.llSkillSpecializations)
        HeroSkill.values().forEach { skill ->
            val cb = CheckBox(this).apply {
                text = skill.displayName
                setTextColor(resources.getColor(R.color.grimTextPrimary))
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (specializedSkills.size >= 3) {
                            this.isChecked = false
                            Toast.makeText(context, "Możesz wybrać tylko 3 specjalizacje!", Toast.LENGTH_SHORT).show()
                        } else {
                            specializedSkills.add(skill)
                        }
                    } else {
                        specializedSkills.remove(skill)
                    }
                }
            }
            container.addView(cb)
        }
    }

    private fun changeAttr(attr: String, delta: Int) {
        val current = attributes[attr] ?: 10
        if (delta > 0 && pointsRemaining <= 0) return
        if (delta < 0 && current <= 5) return

        attributes[attr] = current + delta
        pointsRemaining -= delta
        updateUi()
    }

    private fun updateUi() {
        findViewById<TextView>(R.id.tvPointsRemaining).text = "Punkty do rozdania: $pointsRemaining"
        updateAttrValue(R.id.layoutStr, "str")
        updateAttrValue(R.id.layoutAgi, "agi")
        updateAttrValue(R.id.layoutPer, "per")
        updateAttrValue(R.id.layoutInt, "int")
        updateAttrValue(R.id.layoutEnd, "end")
        updateAttrValue(R.id.layoutCha, "cha")
        updateAttrValue(R.id.layoutPie, "pie")
    }

    private fun updateAttrValue(layoutId: Int, key: String) {
        findViewById<View>(layoutId).findViewById<TextView>(R.id.tvValue).text = attributes[key].toString()
    }

    private fun finalizeCharacter(name: String) {
        val hero = Hero(
            id = UUID.randomUUID().toString(),
            name = name,
            age = 18,
            strength = attributes["str"]!!,
            agility = attributes["agi"]!!,
            perception = attributes["per"]!!,
            intelligence = attributes["int"]!!,
            endurance = attributes["end"]!!,
            charisma = attributes["cha"]!!,
            piety = attributes["pie"]!!,
            trait = selectedTrait
        )

        // Boost specialized skills
        specializedSkills.forEach { skill ->
            hero.skills[skill.name] = 15
        }

        // Add starting abilities based on trait or default
        hero.abilities.add(AbilityRegistry.IRON_SKIN)
        if (selectedTrait == Trait.SOLAR_EYE) {
            hero.abilities.add(AbilityRegistry.SOLARIAN_STRIKE)
        } else if (selectedTrait == Trait.GIFT_OF_MIST) {
            hero.abilities.add(AbilityRegistry.SHADOW_VEIL)
        }

        // Initialize Game
        GameLoopController.bootstrap(seed = 1)
        GameRepository.state.party.clear()
        GameRepository.state.party.add(hero)
        GameRepository.state.activeHeroId = hero.id
        
        startActivity(Intent(this, HubActivity::class.java))
        finish()
    }
}
