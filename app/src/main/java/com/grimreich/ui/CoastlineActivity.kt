package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestSystem

class CoastlineActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coastline)

        val tvDescription = findViewById<TextView>(R.id.tvCoastlineDescription)
        val btnAelion = findViewById<Button>(R.id.btnTalkToAelion)
        val btnExplore = findViewById<Button>(R.id.btnExploreCoastline)
        val btnExit = findViewById<Button>(R.id.btnExitCoastline)

        tvDescription.text = "Wybrzeże - szaroniebieskie fale rozbijają się o kamienisty brzeg. Gęsta mgła spoczywa nad wodą."

        // Check if Aelion quest is active
        val aelionQuestActive = GameRepository.state.quest.activeQuests.contains("quest_north_mist_vision")
        btnAelion.isEnabled = aelionQuestActive

        if (!aelionQuestActive) {
            btnAelion.text = "Porozmawiaj z Aelionem (brak aktywnego questu)"
        }

        btnAelion.setOnClickListener {
            showAelionDialogue()
        }

        // Check if echo quest is active
        val echoQuestActive = GameRepository.state.quest.activeQuests.contains("quest_north_lost_echo")
        btnExplore.isEnabled = echoQuestActive

        if (!echoQuestActive) {
            btnExplore.text = "Zbadaj linię brzegową (brak aktywnego questu)"
        }

        btnExplore.setOnClickListener {
            exploreCoastline()
        }

        btnExit.setOnClickListener {
            finish()
        }
    }

    private fun showAelionDialogue() {
        AlertDialog.Builder(this)
            .setTitle("Aelion")
            .setMessage("Stary mnich wychyla się z mgły.\n\n\"Czekałem na ciebie, wędrowcze. Wiedziałem, że ktoś przyjdzie, gdy zobaczyłem znak we mgle. Oto co musisz wiedzieć: ciemność zbiera siły na północy. Ale nie jesteś sam - święci cię obserwują.\"\n\nAelion wręcza ci starożytny amulet.")
            .setPositiveButton("Ukończ quest") { _, _ ->
                QuestSystem.complete("quest_north_mist_vision")
                GameRepository.state.logEntries.add(0, "Ukończono: Rozmowa z Aelionem - otrzymano Amulet Świętych")
                finish()
            }
            .setNegativeButton("Zamknij", null)
            .show()
    }

    private fun exploreCoastline() {
        AlertDialog.Builder(this)
            .setTitle("Linia brzegowa")
            .setMessage("Wędrując wzdłuż kamienistego brzegu, nagle słyszysz dziwne echo - jakby odległe głosy z przeszłości. Znajdujesz starą tablicę z runicznymi inskrypcjami, która wskazuje na zapomnianą świątynię w głębi lasu.")
            .setPositiveButton("Ukończ quest") { _, _ ->
                QuestSystem.complete("quest_north_lost_echo")
                GameRepository.state.logEntries.add(0, "Ukończono: Zbadano linię brzegową - odkryto runiczne znaki")
                finish()
            }
            .setNegativeButton("Zamknij", null)
            .show()
    }
}
