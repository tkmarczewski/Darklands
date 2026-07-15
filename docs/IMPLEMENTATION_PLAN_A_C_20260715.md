# PLAN IMPLEMENTACJI: SYSTEM TRAUMY (A) ORAZ RYTUALNY CRAFTING (C)

## 1. System Traumy i Blizn Ontologicznych (Funkcjonalność A)

### A.1. Struktura Danych
Dodanie klasy `Trauma` oraz rozszerzenie modelu `Character`.

```kotlin
// Nowa klasa definiująca traumę
data class Trauma(
    val id: String,
    val name: String,
    val description: String,
    val statModifiers: Map<String, Int>, // np. "magic" to 10, "social" to -15
    val severity: Int // 1-3 (Lekka, Głęboka, Nieodwracalna)
)

// Rozszerzenie Character.kt (do dodania w encji bohatera)
val traumaMarks: MutableList<Trauma> = mutableListOf()
var ontologicalStability: Float = 100f // 0 - 100
```

### A.2. Logika Pozyskiwania (Combat/Story)
W `CombatSystem.kt`, po walce z przeciwnikiem typu `PAST_SHADE_ELITE` lub otrzymaniu obrażeń od "Echa":

```kotlin
fun checkForTrauma(character: Character, source: Enemy) {
    if (source.type == EnemyType.PAST_SHADE_ELITE && Random.nextFloat() < 0.25f) {
        val newTrauma = TraumaCatalog.getRandomTrauma()
        character.traumaMarks.add(newTrauma)
        character.ontologicalStability -= 10f
        // Wywołanie efektu wizualnego "Pęknięcia Duszy"
    }
}
```

### A.3. Wpływ na UI i Dialogi
*   **UI:** Blizny widoczne w menu "Kroniki" jako znaki wypalone na portrecie.
*   **Social:** W `DialogueManager.kt` dodanie modyfikatora reakcji NPC:
    `if (player.traumaMarks.isNotEmpty()) response.fearLevel += 20`

---

## 2. Rytualny Crafting - Alchemia Krwi (Funkcjonalność C)

### C.1. Mechanika "Szyfru"
Zamiast kliknięcia "Craft", gracz musi ułożyć sekwencję symboli (Szyfr) w określonym czasie.

```kotlin
data class RitualRecipe(
    val targetItemId: String,
    val requiredIngredients: List<String>,
    val requiredCipher: List<SymbolType>, // Sekwencja np. [KRZYŻ, OKO, PĘKNIĘCIE]
    val sacrificeHp: Int = 10
)
```

### C.2. Interaktywne UI (Compose)
Nowy widok `RitualScreen.kt`:
1.  **Okrąg Rytualny:** Gracz przeciąga symbole na okrąg.
2.  **Pulsowanie:** Im bliżej końca rytuału, tym mocniej ekran drży (Shaking FX).
3.  **Ryzyko:** Jeśli sekwencja zostanie przerwana lub pomylona, wywoływany jest `EncounterSystem.triggerAmbush(EnemyType.BLOOD_WRAITH)`.

### C.3. Koszt Krwi
W momencie rozpoczęcia rytuału:
`player.currentHp -= recipe.sacrificeHp`
`if (player.currentHp <= 0) triggerDeath("Ofiara była zbyt wielka")`

---

## 3. Harmonogram Prac
1.  **Faza 1:** Implementacja `TraumaCatalog` i rozszerzenie `Character`. (2 dni)
2.  **Faza 2:** Integracja traumy z systemem walki i dialogów. (3 dni)
3.  **Faza 3:** Budowa widoku `RitualScreen` i logiki Szyfrów. (5 dni)

---
*Opracowano na polecenie Użytkownika - Agent Stabilizacji GrimReich.*
