package com.darklandsmobile.core

import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.world.CityCatalogue
import com.darklandsmobile.grimreich.v1.*

object GameBootstrap {
    fun init(state: GameState) {
        CityCatalogue.clear()
        WorldMap.clear()
        QuestSystem.clear()
        seedGrimWorld(state.grimEngine)
    }

    private fun seedGrimWorld(engine: GrimWorldEngine) {
        // Region Schwarzwald
        val schwarzwald = RegionConsciousness(
            regionName      = "Schwarzwald",
            mistMind        = "Stara pamięć lasu, pełna zaginionych rycerzy",
            bloodBody       = "Gleba przesiąknięta krwią wojen pogranicznych",
            reflectionSoul  = "Echo modlitw z opuszczonych kaplic",
            emotionalState  = "gniew",
            memory          = listOf("bitwa_1247", "klasztorna_rzez_1301"),
            reactions       = listOf("atak_na_wedrowcow", "nocne_zjawy"),
            regionImpact    = "izolacja_gracza",
            endingImpact    = "chaos_finalny"
        )
        val schwarzwaldTime = NonlinearTime(
            regionName            = "Schwarzwald",
            mistTimeLevel         = 3,
            bloodTimeLevel        = 2,
            reflectionTimeLevel   = 1,
            fullnessTimeLevel     = 4,
            chaosTimeLevel        = 5,
            activeTimeEffects     = listOf("czas_skrocony", "echo_przeszlosci"),
            regionImpact          = "spowalnia_podroz",
            npcImpact             = "halucynacje_npc",
            monsterImpact         = "agresja_potworow",
            endingImpact          = "katastrofa"
        )
        val schwarzwaldArch = FullnessArchitecture(
            structureName  = "Wieza_Nicosci",
            mistForm       = "mglisty_filar",
            bloodForm      = "krwawy_fundament",
            reflectionForm = "odbite_luki",
            fullnessEffect = "pochlania_czas",
            chaosEffect    = "niszczy_mape",
            regionImpact   = "blokuje_podroz",
            endingImpact   = "upadek_regionu"
        )
        engine.loadRegion(schwarzwald, schwarzwaldTime, schwarzwaldArch)

        // Region Regensburg
        val regensburg = RegionConsciousness(
            regionName      = "Regensburg",
            mistMind        = "Pamiec targow i ukladow kupieckich",
            bloodBody       = "Brukowane ulice procesji inkwizycji",
            reflectionSoul  = "Cien katedry rzucony na cale miasto",
            emotionalState  = "napiecie",
            memory          = listOf("targ_1289", "inkwizycja_1318"),
            reactions       = listOf("podejrzliwosc_wobec_obcych", "wzrost_cen"),
            regionImpact    = "utrudniony_handel",
            endingImpact    = "sojusz_lub_zdrada"
        )
        val regensburgTime = NonlinearTime(
            regionName            = "Regensburg",
            mistTimeLevel         = 1,
            bloodTimeLevel        = 3,
            reflectionTimeLevel   = 2,
            fullnessTimeLevel     = 2,
            chaosTimeLevel        = 3,
            activeTimeEffects     = listOf("przyspieszony_czas_handlu"),
            regionImpact          = "skrocony_odpoczynek",
            npcImpact             = "pospiech_npc",
            monsterImpact         = "brak",
            endingImpact          = "kryzys_handlowy"
        )
        val regensburgArch = FullnessArchitecture(
            structureName  = "Katedra_Sw_Petra",
            mistForm       = "mgliste_witraze",
            bloodForm      = "fundamenty_z_ofiarnego_kamienia",
            reflectionForm = "wieze_odbiajace_niebiosa",
            fullnessEffect = "blogoslawienstwo_w_walce",
            chaosEffect    = "klatwa_heretyka",
            regionImpact   = "centrum_wladzy",
            endingImpact   = "koscielny_sojusz"
        )
        engine.loadRegion(regensburg, regensburgTime, regensburgArch)

        // NPC: Inkwizytor Hagen
        val inquisitor = NPCLifePath(
            npcName         = "Inkwizytor Hagen",
            mistFate        = "Zaginąl w lesie podczas polowania na czarownice",
            bloodFate       = "Zginal od wlasnego ostrza przy egzekucji heretyka",
            reflectionF     = "Wstapil do zakonu cieni, by walczyc z prawdziwym zlem",
            fullnessLevel   = 6,
            chaosLevel      = 4,
            timelineEvents  = listOf("pierwsze_sledztwo_1290", "spalenie_koloni_1305", "znikniecie_1318"),
            npcImpact       = "gracz_moze_pozyskac_lub_stracic_sojusznika",
            endingImpact    = "wplywa_na_zakonczenie_inkwizycyjne"
        )
        val inquisitorRel = TriLayerRelationship(
            npcName              = "Inkwizytor Hagen",
            mistRelation         = "podejrzliwy",
            bloodRelation        = "wrogi",
            reflectionRelation   = "sojusznik_z_rezerwy",
            fullnessRelationLevel = 3,
            chaosRelationLevel   = 7,
            emotionalImpact      = "strach_i_szacunek",
            regionImpact         = "wzrost_inkwizycji_w_miescie",
            endingImpact         = "smierc_lub_odkupienie"
        )
        engine.registerNPC(inquisitor, listOf(inquisitorRel))

        // NPC: Alchemik Wulfram
        val alchemist = NPCLifePath(
            npcName         = "Alchemik Wulfram",
            mistFate        = "Odkryl recepture odrodzenia, lecz ja utracil",
            bloodFate       = "Poswiecil krew, by stworzyc kamien filozoficzny",
            reflectionF     = "Zyje w trzech cialach jednoczesnie",
            fullnessLevel   = 8,
            chaosLevel      = 6,
            timelineEvents  = listOf("nauka_w_Bolonii_1280", "pierwszy_eksperyment_1295", "katastrofa_1310"),
            npcImpact       = "mentor_i_potencjalny_wrog",
            endingImpact    = "klucz_do_zakonczenia_alchemicznego"
        )
        engine.registerNPC(alchemist, emptyList())

        // Religie
        engine.registerReligion(
            PhenomenonReligion(
                religionName = "Kult_Pustego_Tronu",
                phenomenon   = "Bog opuscil swiat, tron jest pusty",
                dogma        = "Kazdy grzech wzmacnia nicosc; tylko ofiara przywroci Boga",
                rituals      = listOf("post_trzydniowy", "rytual_krwi_przymierza"),
                prophets     = listOf("Hildegarda_z_Moguncji", "Brat_Konrad"),
                artifacts    = listOf("Zlamane_Berlo"),
                regionImpact = "wzrost_fanatyzmu",
                npcImpact    = "Inkwizytor Hagen",
                endingImpact = "zakonczenie_apokaliptyczne"
            )
        )
        engine.registerReligion(
            PhenomenonReligion(
                religionName = "Bracia_Mgly",
                phenomenon   = "Mgla to zaslona miedzy zywymi a umarlymi",
                dogma        = "Smierc jest tylko progiem; wiedza jest wieczna",
                rituals      = listOf("seanse_o_polnocy", "zapis_snow"),
                prophets     = listOf("Mistrz_Eckhart_Cieni"),
                artifacts    = listOf("Ksiega_Mgly"),
                regionImpact = "zjawiska_nadprzyrodzone_nasilaja_sie",
                npcImpact    = "Alchemik Wulfram",
                endingImpact = "zakonczenie_mistyczne"
            )
        )

        // Artefakty
        engine.registerArtifact(
            FullnessArtifact(
                artifactName     = "Zlamane_Berlo",
                mistEffect       = "Otwiera_przejscie_przez_mgle",
                bloodEffect      = "Pochlania_krew_uzytkownika",
                reflectionEffect = "Pokazuje_alternatywne_losy",
                fullnessEffect   = "Przywraca_punkt_pelni",
                chaosEffect      = "Chaos_plus_3_w_regionie",
                regionImpact     = "destabilizuje_region",
                npcImpact        = "NPC_chca_go_ukrasc",
                endingImpact     = "klucz_do_zakonczenia_apokaliptycznego"
            )
        )

        // Avatar
        engine.setAvatar(
            FullnessAvatar(
                avatarName     = "Cien_Rycerza",
                mistForm       = "Bezksztaltna_sylwetka_we_mgle",
                bloodForm      = "Zakrwawiony_rycerz",
                reflectionForm = "Idealny_odbit_wizerunek_gracza",
                fullnessLevel  = 1,
                chaosLevel     = 0,
                abilities      = listOf("mglisty_ruch", "krwawy_cios", "refleksja_tarcza"),
                regionImpact   = "avatar_zmienia_atmosfere_regionu",
                npcImpact      = "NPC_reaguja_inaczej_na_kazda_forme",
                endingImpact   = "forma_avatara_determinuje_zakonczenie"
            )
        )
    }
}
