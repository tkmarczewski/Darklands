package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository

object QuestSystem {
    private val questTemplates = mapOf(
        "forest_hermit" to "Znajdz pustelnika w lesie",
        "bandit_camp"   to "Rozprosz oboz bandytow",
        "lost_relic"    to "Odzyskaj zagubiona relikwie"
    )
    fun start(questId: String): String {
        val q = GameRepository.state.quest
        if (q.activeQuests.contains(questId)) return "Quest $questId juz aktywny"
        q.activeQuests.add(questId)
        q.questProgress[questId] = 0
        val title = questTemplates[questId] ?: questId
        GameRepository.log("Nowy quest: $title")
        return "Rozpoczeto quest: $title"
    }
    fun advance(questId: String, amount: Int = 1): String {
        val q = GameRepository.state.quest
        if (!q.activeQuests.contains(questId)) return "Quest $questId nie jest aktywny"
        val current = (q.questProgress.getOrDefault(questId, 0)) + amount
        q.questProgress[questId] = current
        return if (current >= 3) {
            q.activeQuests.remove(questId); q.completedQuests.add(questId)
            GameRepository.log("Quest ukonczony: $questId")
            "Quest $questId ukonczony!"
        } else "Quest $questId: postep $current/3"
    }
    fun activeList() = GameRepository.state.quest.activeQuests.toList()
}
