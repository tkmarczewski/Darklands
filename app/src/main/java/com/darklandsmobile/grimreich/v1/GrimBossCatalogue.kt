package com.darklandsmobile.grimreich.v1

object GrimBossCatalogue {
    val prophetOfSeaMist = FactionBoss("Prorok Morskiej Mgły", "Zakon Świtu", "archpriest", listOf("visionary", "fanatic", "broken"), emptyList(), listOf("proroctwo", "burza_mgielna", "otwarcie_rozdarcia"), emptyList(), 80, 60)
    val mirrorKing = FactionBoss("Lustrzany Król", "Trybunał", "tribune", listOf("paranoid", "occult", "strategic"), emptyList(), listOf("dwor", "miasto_lustro", "trybunal_cieni"), emptyList(), 90, 70)
    val bloodAvatarRegional = FactionBoss("Awatar Krwi", "Gildia", "grandmaster", listOf("cruel", "visionary", "fanatic"), emptyList(), listOf("krwawe_pola", "organiczna_katedra", "mutacja_regionalna"), emptyList(), 95, 85)
    val symmetryDemiurgeRegional = FactionBoss("Demiurg Symetrii", "Trybunał", "tribune", listOf("logical", "occult", "vengeful"), emptyList(), listOf("kaniony", "burza_lustrzana", "arena_symetryczna"), emptyList(), 85, 75)
    val goldenWarden = FactionBoss("Złoty Strażnik", "Bractwo Cienia", "inquisitor", listOf("fanatic", "occult", "paranoid"), emptyList(), listOf("nekropolia", "krew_i_mgla", "echo_smierci"), emptyList(), 80, 80)
    val fullnessAvatarRegional = FactionBoss("Awatar Pełni", "None", "chosen", listOf("visionary", "broken", "occult"), emptyList(), listOf("szczyty", "trzy_warstwy", "pelen_bog"), emptyList(), 100, 90)
    val riftBeastRegional = FactionBoss("Bestia Rozdarcia", "None", "chieftain", listOf("feral", "chaotic", "vengeful"), emptyList(), listOf("zwierz_rozpad", "anomalia", "czyste_rozdarcie"), emptyList(), 95, 100)
    val vorthemTri = TriLayerBoss("Arcymistrz Vorthem", 800, 800, 800, "mist", true, 10, true, true, true, true, "pelna_transformacja_wszystkich_regionow", "zakonczenie_pelni_swiata")
    val mirrorTribuneTri = TriLayerBoss("Trybun Lustrzany", 600, 600, 900, "reflection", true, 8, true, false, true, false, "pelna_lustrzana_mutacja_serca_i_pogranicza", "zakonczenie_lustra")
    val xyrelTri = TriLayerBoss("Xyrel, Awatar Krwi", 400, 1100, 500, "blood", true, 10, false, true, false, true, "organiczna_transformacja_rownin_koronnych", "zakonczenie_krwi")
    val aelionTri = TriLayerBoss("Aelion, Prorok Mgły", 1000, 400, 500, "mist", true, 7, true, false, true, false, "mgielna_transformacja_wybrzeza_i_snow_swiata", "zakonczenie_mgly")
    val serethTri = TriLayerBoss("Sereth, Rycerz Pełni", 700, 700, 700, "mist", true, 9, true, true, true, true, "trzy_style_wojny_odcisniete_na_regionach", "mieszane_zakonczenia_pelni_krwi_mgly_lustra")
    val allRegionalBosses = listOf(prophetOfSeaMist, mirrorKing, bloodAvatarRegional, symmetryDemiurgeRegional, goldenWarden, fullnessAvatarRegional, riftBeastRegional)
    val allTriLayerBosses = listOf(vorthemTri, mirrorTribuneTri, xyrelTri, aelionTri, serethTri)
}
