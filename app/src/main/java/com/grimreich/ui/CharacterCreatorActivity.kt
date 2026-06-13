package com.grimreich.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.*
import com.grimreich.systems.DialogueManager
import java.util.*

class CharacterCreatorActivity : AppCompatActivity() {

    private var pointsRemaining = 20
    private var specializationPointsRemaining = 3
    private val attributes = mutableMapOf(
        "Str" to 10, "Agi" to 10, "Per" to 10, "Int" to 10, "End" to 10, "Cha" to 10, "Pie" to 10
    )
    private val specializedSkills = mutableSetOf<HeroSkill>()
    private var selectedCareer: Career = Career.KNIGHT

    private val forbiddenNames = setOf(
        "Ralwing", "Aelion", "Xyrel", "Mira", "Sereth", "Ferrun", "Noctyros",
        "Aldric", "Lorelei", "Silas", "Klaus", "Hildegard", "Friedrich", "Borg", "Elara", "Hans"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_creator)

        setupCareerSelection()
        setupAttributeControls()
        setupSkillSpecializations()

        findViewById<Button>(R.id.btnRandomizeName).setOnClickListener {
            val availableNames = listOf(
                "Heinrich", "Elias", "Sigmund", "Lotte", "Gerda",
                "Wilhelm", "Ulrich", "Greta", "Knut", "Otto",
                "Kurt", "Bertha", "Helga", "Erich", "Bruno",
                "Marta", "Stefan", "Viktor", "Klara", "Emil",
                "Karl", "Rosa", "Adler", "Berta", "Gunter",
                "Hilda", "Karin", "Ludwig", "Olga", "Rolf"
            ).filter { name -> !forbiddenNames.any { it.equals(name, ignoreCase = true) } }
            findViewById<EditText>(R.id.etName).setText(availableNames.random())
        }

        findViewById<Button>(R.id.btnAutoAllocate).setOnClickListener {
            autoAllocatePoints()
        }

        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString().trim()
            if (name.isBlank()) {
                Toast.makeText(this, "Podaj imię swojej Kotwicy", Toast.LENGTH_SHORT).show()
            } else if (forbiddenNames.any { it.equals(name, ignoreCase = true) }) {
                Toast.makeText(this, "To imię jest zarezerwowane dla sił wyższych...", Toast.LENGTH_SHORT).show()
            } else if (specializedSkills.size < 3) {
                Toast.makeText(this, "Wybierz 3 specjalizacje", Toast.LENGTH_SHORT).show()
            } else {
                finalizeCharacter(name)
            }
        }

        updateUi()
    }

    private fun setupCareerSelection() {
        val rg = findViewById<RadioGroup>(R.id.rgCareers)
        rg.setOnCheckedChangeListener { _, checkedId ->
            selectedCareer = when (checkedId) {
                R.id.rbKnight -> Career.KNIGHT
                R.id.rbAlchemist -> Career.ALCHEMIST
                R.id.rbRanger -> Career.GUARD
                R.id.rbMage -> Career.SCHOLAR
                else -> Career.KNIGHT
            }
            applyCareerBonuses()
        }
        findViewById<RadioButton>(R.id.rbKnight).isChecked = true
    }

    private fun applyCareerBonuses() {
        // Reset to base 10
        attributes.keys.forEach { attributes[it] = 10 }
        pointsRemaining = 20

        when (selectedCareer) {
            Career.KNIGHT -> { attributes["Str"] = 13; attributes["End"] = 12 }
            Career.ALCHEMIST -> { attributes["Int"] = 14; attributes["Cha"] = 12 }
            Career.GUARD -> { attributes["Per"] = 13; attributes["Agi"] = 12 }
            Career.SCHOLAR -> { attributes["Int"] = 15; attributes["Str"] = 8 }
            else -> {}
        }
        updateUi()
    }

    private fun setupAttributeControls() {
        setupAttrRow(R.id.layoutStr, "Str", "SIŁA")
        setupAttrRow(R.id.layoutAgi, "Agi", "ZRĘCZNOŚĆ")
        setupAttrRow(R.id.layoutPer, "Per", "PERCEPCJA")
        setupAttrRow(R.id.layoutInt, "Int", "INTELIGENCJA")
        setupAttrRow(R.id.layoutEnd, "End", "WYTRZYMAŁOŚĆ")
        setupAttrRow(R.id.layoutCha, "Cha", "CHARYZMA")
        setupAttrRow(R.id.layoutPie, "Pie", "POBOŻNOŚĆ")
    }

    private fun setupAttrRow(layoutId: Int, key: String, label: String) {
        val layout = findViewById<LinearLayout>(layoutId)
        layout.findViewById<TextView>(R.id.tvAttrLabel).text = label
        layout.findViewById<Button>(R.id.btnMinus).setOnClickListener { changeAttr(key, -1) }
        layout.findViewById<Button>(R.id.btnPlus).setOnClickListener { changeAttr(key, 1) }
    }

    private fun changeAttr(key: String, delta: Int) {
        val current = attributes[key] ?: 10
        if (delta > 0 && pointsRemaining > 0) {
            attributes[key] = current + 1
            pointsRemaining--
        } else if (delta < 0 && current > 5) {
            attributes[key] = current - 1
            pointsRemaining++
        }
        updateUi()
    }

    private fun autoAllocatePoints() {
        while (pointsRemaining > 0) {
            val key = attributes.keys.random()
            attributes[key] = (attributes[key] ?: 10) + 1
            pointsRemaining--
        }
        updateUi()
    }

    private fun setupSkillSpecializations() {
        val container = findViewById<LinearLayout>(R.id.llSkillSpecializations)
        val allSkills = HeroSkill.values()
        allSkills.forEach { skill ->
            val cb = CheckBox(this).apply {
                text = skill.displayName
                setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.grimTextPrimary))
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (specializationPointsRemaining > 0) {
                            specializedSkills.add(skill)
                            specializationPointsRemaining--
                        } else {
                            this.isChecked = false
                        }
                    } else {
                        if (specializedSkills.contains(skill)) {
                            specializedSkills.remove(skill)
                            specializationPointsRemaining++
                        }
                    }
                    updateUi()
                }
            }
            container.addView(cb)
        }
    }

    private fun updateUi() {
        findViewById<TextView>(R.id.tvPointsRemaining).text = "Punkty do rozdania: $pointsRemaining"
        findViewById<TextView>(R.id.tvSkillsTitle).text = "SPECJALIZACJA (WYBIERZ $specializationPointsRemaining)"

        updateAttrValue(R.id.layoutStr, "Str")
        updateAttrValue(R.id.layoutAgi, "Agi")
        updateAttrValue(R.id.layoutPer, "Per")
        updateAttrValue(R.id.layoutInt, "Int")
        updateAttrValue(R.id.layoutEnd, "End")
        updateAttrValue(R.id.layoutCha, "Cha")
        updateAttrValue(R.id.layoutPie, "Pie")
    }

    private fun updateAttrValue(layoutId: Int, key: String) {
        findViewById<LinearLayout>(layoutId).findViewById<TextView>(R.id.tvValue).text = attributes[key].toString()
    }

    private fun finalizeCharacter(name: String) {
        val hero = Hero(
            id = UUID.randomUUID().toString(),
            name = name,
            age = 25,
            strength = attributes["Str"]!!,
            agility = attributes["Agi"]!!,
            perception = attributes["Per"]!!,
            intelligence = attributes["Int"]!!,
            endurance = attributes["End"]!!,
            charisma = attributes["Cha"]!!,
            piety = attributes["Pie"]!!,
            hp = attributes["End"]!! * 2 + 20,
            maxHp = attributes["End"]!! * 2 + 20,
            currentCareer = selectedCareer,
            portraitRes = DialogueManager.getPortrait(selectedCareer.name)
        )
        
        // Apply skill specializations
        specializedSkills.forEach { skill ->
            hero.skills[skill.name] = 30 // Higher base for specialized skills
        }
        
        GameRepository.state = GameState().apply {
            party.clear()
            party.add(hero)
            activeHeroId = hero.id
        }
        
        startActivity(android.content.Intent(this, HubActivity::class.java))
        finish()
    }
}
