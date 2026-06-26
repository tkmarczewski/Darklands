package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SocialEventSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun cityAudience(cityId: String, currentStability: Int?): String {
        val stability = currentStability ?: gameRepository.currentState().world.globalStability
        val rng = Random(cityId.hashCode() + stability)
        
        return when {
            stability < 20 -> listOf(
                "Mieszkańcy błąkają się po ulicach jak bezmyślne procesy. Ich oczy są matowe, a z gardeł wydobywa się tylko statyczny szum. Lokacja dusi się we własnym Echo.",
                "Powietrze smakuje metalem. Budynki drżą, gdy rzeczywistość próbuje zwolnić sektory pamięci tego regionu. Nikt nie patrzy Ci w oczy, bo nikt już nie pamięta, kim jesteś."
            ).random(rng)
            stability < 40 -> listOf(
                "Mieszkańcy szepczą w ciemnych zaułkach o nadchodzącym Epilogu. Każde puknięcie do drzwi budzi przerażenie – ludzie boją się, że to Garbage Collector przyszedł po nich.",
                "Zapach spalonego Echa unosi się nad miastem. Strażnicy są nerwowi, ich dłonie zaciskają się na broni, jakby walczyli z niewidzialnymi glitchami we własnym umyśle."
            ).random(rng)
            stability < 70 -> listOf(
                "Miasto żyje w cieniu Pęknięcia. Ludzie przywykli do anomalii, traktując mrugające tekstury rzeczywistości jak zwykłą pogodę. Handel trwa, ale każdy woli złoto od obietnic.",
                "Gwar targu miesza się z dzwonami pobliskiej kaplicy. Jest w tym wszystkim jednak nuta fałszu, jakby muzyka sfer była puszczona z uszkodzonego nośnika."
            ).random(rng)
            else -> listOf(
                "Rzeczywistość w tym miejscu wydaje się niezwykle solidna. Ludzie uśmiechają się do siebie, nieświadomi, że ich świat jest tylko kruchym Zwojem Stanu.",
                "Spokój i porządek. Można niemal zapomnieć o Drugiej Stronie. Jedynie Kotwica w Twoich dłoniach przypomina, że to tylko cisza przed burzą danych."
            ).random(rng)
        }
    }
}
