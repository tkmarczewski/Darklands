package com.darklandsmobile.core

import com.darklandsmobile.grimreich.v1.*

object GrimSeed {
    fun initialize(): GrimGameState {
        val state = GrimGameState()
        seedWorld(state.grimEngine)
        GrimGameRepository.state = state
        return state
    }

    private fun seedWorld(engine: GrimWorldEngine) {
        engine.loadRegion(
            region = RegionConsciousness(
                "Schwarzwald", "mgla pamieci", "krew przodkow", "echo przyszlosci",
                "gniew", listOf("bitwa 1247"), listOf("atak na wedrowcow"),
                "izolacja", "chaos"
            ),
            time = NonlinearTime(
                "Schwarzwald", 3, 2, 1, 4, 5,
                listOf("czas_skrocony", "echo_przeszlosci"),
                "spowalnia podroz", "halucynacje NPC", "agresja potworow", "katastrofa"
            ),
            arch = FullnessArchitecture(
                "Wieza Nicości", "mglisty filar", "krwawy fundament", "odbite luki",
                "pochlania czas", "niszczy mape", "blokuje podroz", "upadek regionu"
            )
        )
        engine.loadRegion(
            region = RegionConsciousness(
                "Regensburg", "szept kupcow", "kamienne zyly", "odbicie winy",
                "niepokoj", listOf("proces 1310"), listOf("zamkniete bramy"),
                "duszne centrum", "pekajace przymierze"
            ),
            time = NonlinearTime(
                "Regensburg", 2, 3, 2, 3, 4,
                listOf("martwy_zegar", "krag_echa"),
                "zamraza handel", "podejrzliwosc NPC", "mutacje w kanałach", "erozja porzadku"
            ),
            arch = FullnessArchitecture(
                "Brama Popiolu", "szary luk", "krwawa krata", "lustrzany portal",
                "wypacza kierunki", "wysysa wolę", "zamyka trakty", "rozszczelnienie miasta"
            )
        )

        engine.registerNPC(
            NPCLifePath("Inkwizytor Hagen", "sluzba", "blizny", "przysiega", 13, 4, listOf("sąd płomienia"), "presja", "prawda za cenę"),
            listOf(TriLayerRelationship("Inkwizytor Hagen", "ostrożność", "dyscyplina", "kontrola", 8, 2, "napięcie", "twarde rządy", "wina"))
        )
        engine.registerNPC(
            NPCLifePath("Alchemik Wulfram", "głód wiedzy", "skażenie", "rozpad", 7, 9, listOf("eksperyment lustrzany"), "skażenie", "cena poznania"),
            listOf(TriLayerRelationship("Alchemik Wulfram", "ciekawość", "żądza", "pęknięcie", 4, 7, "obsesja", "destabilizacja", "odłam"))
        )

        engine.registerReligion(
            PhenomenonReligion(
                "Kult Pustego Tronu", "nicość", "Każdy grzech wzmacnia nicość",
                listOf("czarna msza"), listOf("Arcycien"), listOf("Korona Pustki"),
                "erozja miast", "Inkwizytor Hagen, Alchemik Wulfram", "upadek"
            )
        )
        engine.registerReligion(
            PhenomenonReligion(
                "Bracia Mgly", "mgla", "Mgla przechowuje prawde",
                listOf("procesja cieni"), listOf("Stary Mar"), listOf("Lustro Pielgrzyma"),
                "zasnucie drog", "Inkwizytor Hagen", "rozproszenie"
            )
        )

        engine.registerArtifact(FullnessArtifact(
            "Zlamane Berlo", "szepcze", "krwawi", "odbija", "poszerza pustke", "sieje chaos", "peknięcia", "kuszenie", "zly omen"
        ))
        engine.registerArtifact(FullnessArtifact(
            "Lustro Drugiej Strony", "zamgla", "rani", "rozszczepia", "odsłania", "przyzywa", "brama", "uwodzi", "podwojne zakonczenie"
        ))

        engine.setAvatar(FullnessAvatar(
            "Cien Rycerza", "mgliste oblicze", "zbroja z blizn", "lustrzany oddech",
            12, 6, listOf("krok przez welon", "cios z echa"), "trwoga", "wezwanie", "powrot długu"
        ))

        engine.recordHistory(
            AlternateHistory(
                "Zaraza nie nadeszla", "wioski trwaja", "armie nie oslably", "sumienia pekly",
                "przeludnienie", "ukryta wojna", "spieta granica", "tlumione herezje", "późny kataklizm"
            ),
            WorldChronicle(
                "Kronika Czarnych Lat", "mgla zapisala", "krew pamieta", "lustra klamia", "pelnia glodnieje", "chaos wraca", "ciemnieje", "szepczą", "rozwarcie"
            )
        )
    }
}
