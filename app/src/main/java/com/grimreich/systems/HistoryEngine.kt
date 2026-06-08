package com.grimreich.systems

data class HistoryTimeline(
    val id: String,
    val name: String,
    var active: Boolean = true,
)

object HistoryEngine {
    private val timelines = mutableListOf(HistoryTimeline("prime", "Główna Oś"))
    
    fun splitHistory(name: String) {
        val newId = "split_${timelines.size}"
        timelines.add(HistoryTimeline(newId, name))
        ChronicleSystem.record("Historia rozszczepiła się: $name")
    }
    
    fun getActiveTimelines(): List<HistoryTimeline> = timelines.asSequence().filter { it.active }.toList()
}
