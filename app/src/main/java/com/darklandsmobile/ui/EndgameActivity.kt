package com.darklandsmobile.ui

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.core.GameState
import com.darklandsmobile.systems.*

class EndgameActivity : AppCompatActivity() {
    private lateinit var gs: GameState
    private var bossState: BossState? = null
    private val log = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gs = GameState.load(this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val tvStatus = TextView(this)
        val tvLog = TextView(this)
        val tvEnding = TextView(this)

        fun refreshStatus() {
            val boss = bossState
            tvStatus.text = if (boss != null) {
                "Boss HP: ${boss.hp} | Faza: ${boss.phase} | Morale: ${boss.morale}\n" +
                "Twoje HP: ${gs.battle.hp} | Morale: ${gs.battle.morale}"
            } else {
                "Finałowe questy:\n" + EndgameQuestChain.quests.joinToString("\n") {
                    "  ${it.title}: ${it.status}"
                }
            }
            tvLog.text = log.takeLast(6).joinToString("\n")
        }

        val btnStartChain = Button(this).apply {
            text = "Rozpocznij finałowy wątek"
            setOnClickListener {
                EndgameQuestChain.quests.forEach { q ->
                    val req = q.requirements
                    val eligible = gs.religion.faith >= req.minFaith &&
                        gs.religion.virtue >= req.minVirtue &&
                        gs.reputation.cityReputation >= req.minCityReputation &&
                        gs.reputation.factionReputation >= req.minFactionReputation &&
                        req.requiredQuestIds.all { id ->
                            EndgameQuestChain.quests.find { it.id == id }?.status == EndgameQuestStatus.COMPLETED
                        }
                    if (eligible && q.status == EndgameQuestStatus.LOCKED) {
                        q.status = EndgameQuestStatus.AVAILABLE
                        log.add("Quest dostępny: ${q.title}")
                    }
                }
                refreshStatus()
            }
        }

        val btnCompleteQuest = Button(this).apply {
            text = "Ukończ aktywny quest"
            setOnClickListener {
                val q = EndgameQuestChain.quests.firstOrNull { it.status == EndgameQuestStatus.AVAILABLE }
                if (q != null) {
                    q.status = EndgameQuestStatus.COMPLETED
                    gs.gold += q.rewards.gold
                    gs.religion.faith += q.rewards.faithBonus
                    gs.reputation.cityReputation += q.rewards.reputationBonus
                    gs.religion.divineFavor += q.rewards.divineFavorBonus
                    log.add("Ukończono: ${q.title} | +${q.rewards.gold} złota")
                    EndgameQuestChain.quests.forEach { nq ->
                        val req = nq.requirements
                        val eligible = gs.religion.faith >= req.minFaith &&
                            gs.religion.virtue >= req.minVirtue &&
                            gs.reputation.cityReputation >= req.minCityReputation &&
                            gs.reputation.factionReputation >= req.minFactionReputation &&
                            req.requiredQuestIds.all { id ->
                                EndgameQuestChain.quests.find { it.id == id }?.status == EndgameQuestStatus.COMPLETED
                            }
                        if (eligible && nq.status == EndgameQuestStatus.LOCKED) {
                            nq.status = EndgameQuestStatus.AVAILABLE
                            log.add("Odblokowano: ${nq.title}")
                        }
                    }
                } else {
                    log.add("Brak dostępnych questów.")
                }
                refreshStatus()
            }
        }

        val btnBoss = Button(this).apply {
            text = "Zmierz się z bossem"
            setOnClickListener {
                val lastQuest = EndgameQuestChain.quests.last()
                if (lastQuest.status == EndgameQuestStatus.COMPLETED) {
                    bossState = BossBattleSystem.startBoss(gs)
                    log.add("Boss battle rozpoczęty!")
                } else {
                    log.add("Najpierw ukończ wszystkie finałowe questy.")
                }
                refreshStatus()
            }
        }

        val btnAttack = Button(this).apply {
            text = "Atakuj bossa"
            setOnClickListener {
                val boss = bossState
                if (boss != null) {
                    log.add(BossBattleSystem.attackBoss(boss, gs))
                    if (!BossBattleSystem.isDefeated(boss)) {
                        log.add(BossBattleSystem.bossTurn(boss, gs))
                    }
                    if (BossBattleSystem.isDefeated(boss)) {
                        val ending = EndingSystem.resolveEnding(gs)
                        tvEnding.text = "=== ${ending.title} ===\n${ending.description}"
                        log.add("Koniec gry: ${ending.type}")
                        gs.save(this@EndgameActivity)
                    } else if (BossBattleSystem.isPlayerDefeated(gs)) {
                        tvEnding.text = "=== Porażka ===\nZostałeś pokonany przez mroczne siły."
                        log.add("Gracz pokonany.")
                    }
                } else {
                    log.add("Nie ma aktywnego bossa.")
                }
                refreshStatus()
            }
        }

        val btnEnding = Button(this).apply {
            text = "Pokaż zakończenie"
            setOnClickListener {
                val ending = EndingSystem.resolveEnding(gs)
                tvEnding.text = "=== ${ending.title} ===\n${ending.description}"
            }
        }

        listOf(
            TextView(this).apply { text = "=== FINAŁ ===" },
            tvStatus, btnStartChain, btnCompleteQuest, btnBoss, btnAttack, btnEnding,
            tvLog, tvEnding
        ).forEach { layout.addView(it) }

        setContentView(ScrollView(this).apply { addView(layout) })
        refreshStatus()
    }
}
