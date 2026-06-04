package com.darklandsmobile.grimreich.v1

object GrimRegionCatalogue {
    data class RegionEntry(val regionName:String,val phenomenon:String,val faction:String,val bossName:String,val time:NonlinearTime,val architecture:FullnessArchitecture)
    val regions = listOf(
        RegionEntry("Wybrzeże Północne","Mgła","Zakon Świtu","Prorok Morskiej Mgły",GrimBuilders.defaultNonlinearTime("Wybrzeże Północne"), FullnessArchitecture("Latarnia Trzech Przyszłości","mgła","krew","odbicie","pełnia","rozdarcie","przepowiednie_na_morzu","wzmacnia_zakonczenie_mgly")),
        RegionEntry("Serce Krainy","Odbicie","Trybunał","Lustrzany Król",GrimBuilders.defaultNonlinearTime("Serce Krainy"), FullnessArchitecture("Dwór Złoty","mgła","krew","odbicie","pełnia","rozdarcie","centrum_trybunalu","wezel_zakonczenia_lustra")),
        RegionEntry("Równiny Koronne","Krew","Gildia","Awatar Krwi",GrimBuilders.defaultNonlinearTime("Równiny Koronne"), FullnessArchitecture("Katedra Jednego Ciała","mgła","krew","odbicie","pełnia","rozdarcie","centrum_rytualow_krwi","klucz_do_zakonczenia_krwi")),
        RegionEntry("Pogranicze Stepowe","Rozdarcie","Bractwo Cienia","Złoty Strażnik",GrimBuilders.defaultNonlinearTime("Pogranicze Stepowe"), FullnessArchitecture("Fort Rozłamu","mgła","krew","odbicie","pełnia","rozdarcie","centrum_bursztynowych_pustkowi","wzmacnia_zakonczenie_rozdarcia")),
        RegionEntry("Południowe Ruiny","Pełnia","Trybunał","Demiurg Symetrii",GrimBuilders.defaultNonlinearTime("Południowe Ruiny"), FullnessArchitecture("Zrujnowany Trybunał","mgła","krew","odbicie","pełnia","rozdarcie","centrum_ruin_pełni","wzmacnia_zakonczenie_pelni")),
        RegionEntry("Góry Południowe","Absolut","None","Bestia Rozdarcia",GrimBuilders.defaultNonlinearTime("Góry Południowe"), FullnessArchitecture("Szczyt Zaniku","mgła","krew","odbicie","pełnia","rozdarcie","szczyt_zaniku","wzmacnia_zakonczenie_absolutu")),
        RegionEntry("Ziemie Dzikie","Mgła","Bractwo Cienia","Trybun Lustrzany",GrimBuilders.defaultNonlinearTime("Ziemie Dzikie"), FullnessArchitecture("Krąg Łowów","mgła","krew","odbicie","pełnia","rozdarcie","centrum_dzikich_pól","wzmacnia_zakonczenie_mieszane"))
    )

    val allRegions = regions.map { it.regionName }
    val all = regions
}
