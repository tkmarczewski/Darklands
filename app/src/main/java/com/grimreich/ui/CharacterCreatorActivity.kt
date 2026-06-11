package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.*
import com.grimreich.core.AbilityRegistry
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.core.HeroSkill
import com.grimreich.core.Trait
import com.grimreich.systems.GameLoopController
import java.util.UUID

class CharacterCreatorActivity : AppCompatActivity() {

    private var pointsRemaining = GrimConstants.Character.STARTING_POINTS
    private val attributes = mutableMapOf(
        "str" to GrimConstants.Character.MIN_ATTRIBUTE_VALUE, "agi" to GrimConstants.Character.MIN_ATTRIBUTE_VALUE, "per" to GrimConstants.Character.MIN_ATTRIBUTE_VALUE, 
        "int" to GrimConstants.Character.MIN_ATTRIBUTE_VALUE, "end" to GrimConstants.Character.MIN_ATTRIBUTE_VALUE, "cha" to GrimConstants.Character.MIN_ATTRIBUTE_VALUE, "pie" to GrimConstants.Character.MIN_ATTRIBUTE_VALUE
    )
    private var selectedTrait: Trait? = null
    private val specializedSkills = mutableSetOf<HeroSkill>()

    private val names = listOf("Friedrich", "Hildegard", "Gunter", "Elsa", "Ulrich", "Marta", "Klaus", "Sigrid")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_creator)

        setupAttributeControls()
        setupTraitSelection()
        setupSkillSpecializations()

        val etName = findViewById<EditText>(R.id.etHeroName)
        
        findViewById<Button>(R.id.btnRandomizeName).setOnClickListener {
            etName.setText(names.random())
            hideKeyboard()
        }

        findViewById<Button>(R.id.btnAutoAllocate).setOnClickListener {
            autoAllocatePoints()
        }

        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            val nameText = etName.text.toString().trim()
            if (nameText.isEmpty()) {
                Toast.makeText(this, "Wpisz imię bohatera!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedTrait == null) {
                Toast.makeText(this, "Wybierz cechę!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (specializedSkills.size != GrimConstants.Character.SKILL_SPECIALIZATION_REQUIRED) {
                Toast.makeText(this, "Wybierz dokładnie ${GrimConstants.Character.SKILL_SPECIALIZATION_REQUIRED} specjalizacje!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            finalizeCharacter(nameText)
        }
        
        updateUi()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
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
            hideKeyboard()
        }
    }

    private fun setupSkillSpecializations() {
        val container = findViewById<LinearLayout>(R.id.llSkillSpecializations)
        HeroSkill.values().forEach { skill ->
            val cb = CheckBox(this).apply {
                text = skill.displayName
                setTextColor(androidx.core.content.ContextCompat.getColor(this@CharacterCreatorActivity, R.color.grimTextPrimary))
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
        val current = attributes[attr] ?: GrimConstants.Character.MIN_ATTRIBUTE_VALUE
        if (delta > 0 && pointsRemaining <= 0) return
        if (delta > 0 && current >= GrimConstants.Character.MAX_ATTRIBUTE_VALUE) return
        if (delta < 0 && current <= GrimConstants.Character.MIN_ATTRIBUTE_VALUE) return

        attributes[attr] = current + delta
        pointsRemaining -= delta
        updateUi()
    }

    private fun autoAllocatePoints() {
        // Reset to base
        attributes.keys.forEach { attributes[it] = 10 }
        pointsRemaining = 20
        
        val keys = attributes.keys.toList()
        repeat(20) {
            val randomKey = keys.random()
            attributes[randomKey] = attributes[randomKey]!! + 1
            pointsRemaining--
        }
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

        // Add starting abilities based on trait
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
        
        val intent = Intent(this, HubActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
