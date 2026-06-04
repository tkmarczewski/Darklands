package com.darklandsmobile.grimreich.v1

object GrimNpcCatalogue {
    val aelion = NPCLifePath("Aelion", "mgielny_prorok", "krwawe_proroctwa", "lustrzane_echo", 7, 4, listOf("wizja", "wędrówka"), "region_impuls", "ending_impuls")
    val xyrel = NPCLifePath("Xyrel", "krwawy_wódz", "mutacja_ciała", "lustrzany_cień", 8, 9, listOf("wojna", "upadek"), "region_impuls", "ending_impuls")
    val sereth = NPCLifePath("Sereth", "mgielny_rycerz", "żelazna_wola", "odbicie_serca", 6, 5, listOf("próba", "przysięga"), "region_impuls", "ending_impuls")
    val prophetOfRift = NPCLifePath("Prorok Rozdarcia", "wizje_krzyku", "pęknięte_kości", "złamana_dusza", 9, 10, listOf("rozpad", "szaleństwo"), "region_impuls", "ending_impuls")
    val fallenPriest = NPCLifePath("Upadły Kapłan", "cisza", "szpetota", "wyrzut_sumienia", 4, 6, listOf("pokuta", "zdrada"), "region_impuls", "ending_impuls")
    val wanderingWarrior = NPCLifePath("Wędrowny Wojownik", "droga", "blizna", "echo_war", 5, 3, listOf("bitwa", "wędrówka"), "region_impuls", "ending_impuls")
    val cultHunter = NPCLifePath("Łowca Kultów", "polowanie", "fanatyzm", "pustka", 6, 7, listOf("łowy", "trop"), "region_impuls", "ending_impuls")
    val all = listOf(aelion, xyrel, sereth, prophetOfRift, fallenPriest, wanderingWarrior, cultHunter)
}
