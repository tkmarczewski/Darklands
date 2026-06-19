package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.grimreich.R
import com.grimreich.core.*
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.QuestSystem
import com.grimreich.world.CityCatalogue
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class CharacterCreatorActivity : AppCompatActivity() {

    @Inject lateinit var gameRepository: GameRepository
    @Inject lateinit var questSystem: QuestSystem
    @Inject lateinit var dialogueManager: DialogueManager
    @Inject lateinit var gameBootstrapper: GameBootstrapper

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

    private val randomNames = listOf(
        "Heinrich", "Elias", "Sigmund", "Lotte", "Gerda",
        "Wilhelm", "Ulrich", "Greta", "Knut", "Otto",
        "Kurt", "Bertha", "Helga", "Erich", "Bruno",
        "Marta", "Stefan", "Viktor", "Klara", "Emil",
        "Karl", "Rosa", "Adler", "Berta", "Gunter",
        "Hilda", "Karin", "Ludwig", "Olga", "Rolf"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_creator)

        setupCareerSelection()
        setupAttributeControls()
        setupSkillSpecializations()

        findViewById<Button>(R.id.btnRandomizeName).setOnClickListener {
            findViewById<EditText>(R.id.etName).setText(randomAllowedName())
        }

        findViewById<Button>(R.id.btnAutoAllocate).setOnClickListener {
            randomizeCharacterSheet()
        }

        findViewById<Button>(R.id.btnRandomAndStart)?.setOnClickListener {
            randomizeAndStart()
        }

        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString().trim()
            val playerName = gameRepository.currentState().playerName
            
            if (name.isBlank()) {
                Toast.makeText(this, "Podaj imię swojej Kotwicy", Toast.LENGTH_SHORT).show()
            } else if (playerName != null && name.equals(playerName, ignoreCase = true)) {
                Toast.makeText(this, "Imię bohatera nie może być takie samo jak Twoje imię.", Toast.LENGTH_SHORT).show()
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
            setupSkillSpecializations()
        }
        findViewById<RadioButton>(R.id.rbKnight).isChecked = true
    }

    private fun applyCareerBonuses() {
        attributes.keys.forEach { attributes[it] = 10 }
        pointsRemaining = 20

        when (selectedCareer) {
            Career.KNIGHT -> { attributes["Str"] = 13; attributes["End"] = 12 }
            Career.ALCHEMIST -> { attributes["Int"] = 14; attributes["Cha"] = 12 }
            Career.GUARD -> { attributes["Per"] = 13; attributes["Agi"] = 12 }
            Career.SCHOLAR -> { attributes["Int"] = 15; attributes["Str"] = 8 }
            else -> {}
        }
        
        specializedSkills.clear()
        specializationPointsRemaining = 3
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

    private fun availableSkillsForCareer(career: Career): List<HeroSkill> {
        val allSkills = HeroSkill.entries
        return when (career) {
            Career.KNIGHT -> allSkills.filter { it.group == SkillGroup.WEAPON || it.group == SkillGroup.ARMOR }
            Career.ALCHEMIST -> allSkills.filter { it.group == SkillGroup.ACADEMIC || it.name == "ALCH" }
            Career.GUARD -> allSkills.filter { it.group == SkillGroup.WEAPON || it.group == SkillGroup.SURVIVAL }
            Career.SCHOLAR -> allSkills.filter { it.group == SkillGroup.ACADEMIC }
            else -> allSkills.toList()
        }
    }

    private fun setupSkillSpecializations() {
        val container = findViewById<LinearLayout>(R.id.llSkillSpecializations)
        container.removeAllViews()

        val availableSkills = availableSkillsForCareer(selectedCareer)

        availableSkills.forEach { skill ->
            val cb = CheckBox(this).apply {
                text = skill.displayName
                setTextColor(ContextCompat.getColor(context, R.color.grimTextPrimary))
                isChecked = specializedSkills.contains(skill)
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

    private fun randomAllowedName(): String {
        return randomNames
            .filter { name -> !forbiddenNames.any { it.equals(name, ignoreCase = true) } }
            .random()
    }

    private fun careerRadioId(career: Career): Int {
        return when (career) {
            Career.KNIGHT -> R.id.rbKnight
            Career.ALCHEMIST -> R.id.rbAlchemist
            Career.GUARD -> R.id.rbRanger
            Career.SCHOLAR -> R.id.rbMage
            else -> R.id.rbKnight
        }
    }

    private fun randomizeCharacterSheet() {
        val rolledCareer = listOf(Career.KNIGHT, Career.ALCHEMIST, Career.GUARD, Career.SCHOLAR).random()
        findViewById<RadioGroup>(R.id.rgCareers).check(careerRadioId(rolledCareer))
        selectedCareer = rolledCareer
        applyCareerBonuses()

        val preferredStats = when (rolledCareer) {
            Career.KNIGHT -> listOf("Str", "End", "Agi")
            Career.ALCHEMIST -> listOf("Int", "Cha", "Per")
            Career.GUARD -> listOf("Per", "Agi", "End")
            Career.SCHOLAR -> listOf("Int", "Pie", "Cha")
            else -> listOf("Str", "Agi", "Per", "Int", "End", "Cha", "Pie")
        }

        while (pointsRemaining > 0) {
            val pick = if (Random.nextInt(100) < 70) preferredStats.random() else attributes.keys.random()
            attributes[pick] = (attributes[pick] ?: 10) + 1
            pointsRemaining--
        }

        val rolledSpecializations = availableSkillsForCareer(rolledCareer).shuffled().take(3)
        specializedSkills.clear()
        specializedSkills.addAll(rolledSpecializations)
        specializationPointsRemaining = 0

        findViewById<EditText>(R.id.etName).setText(randomAllowedName())
        updateUi()
        setupSkillSpecializations() // Re-sync checkboxes
    }

    private fun randomizeAndStart() {
        randomizeCharacterSheet()
        val name = findViewById<EditText>(R.id.etName).text.toString().trim()
        finalizeCharacter(name)
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
        gameBootstrapper.bootstrapFreshWorld(seed = 1)

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
            portraitRes = dialogueManager.getPortrait(selectedCareer.name)
        )
        
        specializedSkills.forEach { skill ->
            hero.skills[skill.name] = com.grimreich.core.GrimConstants.Character.SPECIALIZED_SKILL_BASE_VALUE
        }
        
        val state = gameRepository.currentState()
        state.playerName = gameRepository.currentState().playerName
        state.characterNameLocked = true
        state.party.clear()
        state.party.add(hero)
        state.activeHeroId = hero.id
        state.gold = 100
        state.logEntries.add("Kotwica $name wkroczyła do Grimreich.")
        state.grimCurrentRegion = "wybrzeze_polnocne"
        state.world.location = "wybrzeze_polnocne"

        gameRepository.persistCurrentState()
        
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
