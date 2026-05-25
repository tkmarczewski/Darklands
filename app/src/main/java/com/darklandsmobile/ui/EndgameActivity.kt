package com.darklandsmobile.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.PartyRepository
import com.darklandsmobile.systems.BossBattleSystem
import com.darklandsmobile.systems.BossState
import com.darklandsmobile.systems.EndgameQuestChain
import com.darklandsmobile.systems.EndgameQuestStatus
import com.darklandsmobile.systems.EndingSystem

// Ekran finalu: lancuch koncowych questow + walka z bossem. Korzysta wylacznie z GameRepository.state
// i systems/* - bez zaleznosci od skasowanych modulow Event*.
class EndgameActivity : AppCompatActivity() {

    private var bossState: BossState? = null
    private val log = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gs = GameRepository.state

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val tvStatus = TextView(this)
        val tvLog = TextView(this)
        val tvEnding = TextView(this)

        fun refreshStatus() {
            val boss = bossState
            val hero = PartyRepository.activeHero()
            tvStatus.text = if (boss != null) {
                "Boss HP: ${boss.hp} | Faza: ${boss.phase} | Morale: ${boss.morale}\n" +
                "Twoje HP: ${hero?.hp ?: 0}/${hero?.maxHp ?: 0}"
            } else {
                "Finalowe questy:\n" + EndgameQuestChain.quests.joinToString("\n") {
                    "  ${it.title}: ${it.status}"
                }
            }
            tvLog.text = log.takeLast(6).joinToString("\n")
        }

        // Helper: czy quest spelnia wymagania w aktualnym stanie gry.
        fun isEligible(req: com.darklandsmobile.systems.EndgameRequirements): Boolean {
            val cityRep = gs.reputation.city.values.sum()
            val factionRep = 0
            return gs.prayer.faith >= req.minFaith &&
            gs.prayer.virtue >= req.minVirtue &&
            cityRep >= req.minCityReputation &&
            factionRep >= req.minFactionReputation &&
            req.requiredQuestIds.all { id ->
            EndgameQuestChain.quests.find { it.id == id }?.status == EndgameQuestStatus.COMPLETED
        }
}}

        val btnStartChain = Button(this).apply {
            text = "Rozpocznij finalowy watek"
            setOnClickListener {
                EndgameQuestChain.quests.forEach { q ->
                    if (isEligible(q.requirements) && q.status == EndgameQuestStatus.LOCKED) {
                        q.status = EndgameQuestStatus.AVAILABLE
                        log.add("Quest dostepny: ${q.title}")
                    }
                }
                refreshStatus()
            }
        }

        val btnCompleteQuest = Button(this).apply {
            text = "Ukoncz aktywny quest"
            setOnClickListener {
                val q = EndgameQuestChain.quests.firstOrNull { it.status == EndgameQuestStatus.AVAILABLE }
                if (q != null) {
                    q.status = EndgameQuestStatus.COMPLETED
                    gs.gold += q.rewards.gold
                    gs.prayer.faith = (gs.prayer.faith + q.rewards.faithBonus).coerceAtMost(100)
                    // Nagroda reputacyjna trafia do pierwszego znanego miasta - upraszczamy.
                    gs.reputation.city.keys.firstOrNull()?.let { key ->
                        gs.reputation.city[key] = (gs.reputation.city[key]!! + q.rewards.reputationBonus)
                            .coerceIn(-100, 100)
                    }
                    gs.prayer.blessings += q.rewards.divineFavorBonus
                    log.add("Ukonczono: ${q.title} | +${q.rewards.gold} zlota")
                    EndgameQuestChain.quests.forEach { nq ->
                        if (isEligible(nq.requirements) && nq.status == EndgameQuestStatus.LOCKED) {
                            nq.status = EndgameQuestStatus.AVAILABLE
                            log.add("Odblokowano: ${nq.title}")
                        }
                    }
                } else {
                    log.add("Brak dostepnych questow.")
                }
                refreshStatus()
            }
        }

        val btnBoss = Button(this).apply {
            text = "Zmierz sie z bossem"
            setOnClickListener {
                val lastQuest = EndgameQuestChain.quests.last()
                if (lastQuest.status == EndgameQuestStatus.COMPLETED) {
                    bossState = BossBattleSystem.startBoss(gs)
                    log.add("Boss battle rozpoczety!")
                } else {
                    log.add("Najpierw ukoncz wszystkie finalowe questy.")
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
                    } else if (BossBattleSystem.isPlayerDefeated(gs)) {
                        tvEnding.text = "=== Porazka ===\nZostales pokonany przez mroczne sily."
                        log.add("Gracz pokonany.")
                    }
                } else {
                    log.add("Nie ma aktywnego bossa.")
                }
                refreshStatus()
            }
        }

        val btnEnding = Button(this).apply {
            text = "Pokaz zakonczenie"
            setOnClickListener {
                val ending = EndingSystem.resolveEnding(gs)
                tvEnding.text = "=== ${ending.title} ===\n${ending.description}"
            }
        }

        listOf(
            TextView(this).apply { text = "=== FINAL ===" },
            tvStatus, btnStartChain, btnCompleteQuest, btnBoss, btnAttack, btnEnding,
            tvLog, tvEnding
        ).forEach { layout.addView(it) }

        setContentView(ScrollView(this).apply { addView(layout) })
        refreshStatus()
    }
}
