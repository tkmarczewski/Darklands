package com.darklandsmobile.core

import kotlin.random.Random

// ==================== SKLADNIKI ====================
enum class AlchemyIngredient(val displayName: String, val basePrice: Int) {
    BRIMSTONE("Siarka",         3),
    MANGANES("Mangan",          4),
    NAPTHA("Nafta",             5),
    AQUA_REGIA("Woda Kroleska", 8),
    ZINCBLENDE("Blenda cynkowa",4),
    ANTIMONI("Antymon",         6),
    ORPIMENT("Aurypigment",     7),
    WHITE_CINNABAR("Bial cynob",9),
    BASE_CHOLERIC("Baza cholery",    5),
    BASE_PHLEGMATIC("Baza flegmy",   5),
    BASE_SANGUINE("Baza sangwinii",  5),
    BASE_MELANCHOLIC("Baza melancholii", 5),
    SALT("Sol",                 2),
    MERCURY("Rtec",             10),
    SULFUR_CRUDE("Siarka surowa",3)
}

// ==================== JAKOSC MIKSTURY ====================
enum class PotionQuality(val label: String, val qualityValue: Int, val priceMultiplier: Float) {
    LOW   ("Niska (25q)",  25, 1.0f),
    MEDIUM("Srednia (35q)",35, 1.8f),
    HIGH  ("Wysoka (45q)", 45, 3.5f)
}

// ==================== 22 MIKSTURY ====================
enum class PotionType(
    val displayName: String,
    val description: String,
    val ingredients: List<AlchemyIngredient>,
    val basePrice: Int,
    val group: PotionGroup
) {
    // OFENSYWNE
    NOXIOUS_AROMA("Smrodliwy Arom",    "Oslabia wroga.",          listOf(AlchemyIngredient.BRIMSTONE, AlchemyIngredient.SALT),                10, PotionGroup.OFFENSIVE),
    EYEBURN("Pieczenie Oczu",          "Oslepia tymczasowo.",      listOf(AlchemyIngredient.ANTIMONI, AlchemyIngredient.AQUA_REGIA),           15, PotionGroup.OFFENSIVE),
    BLACK_CLOUD("Czarna Chmura",       "Zasania pole bitwy.",     listOf(AlchemyIngredient.NAPTHA, AlchemyIngredient.BASE_CHOLERIC),           20, PotionGroup.OFFENSIVE),
    FLEADUST("Pyl Pchlowy",            "Rozprasza wrogich.",      listOf(AlchemyIngredient.ORPIMENT, AlchemyIngredient.SALT),                  12, PotionGroup.OFFENSIVE),
    EATER_WATER("Woda Trawiaca",       "Niszczy zbroje.",         listOf(AlchemyIngredient.AQUA_REGIA, AlchemyIngredient.MERCURY),             35, PotionGroup.OFFENSIVE),
    BREATH_OF_DEATH("Oddech Smierci",  "Silna trucizna.",         listOf(AlchemyIngredient.WHITE_CINNABAR, AlchemyIngredient.MERCURY),         40, PotionGroup.OFFENSIVE),
    SUNBURST("Rozblysk Slonca",        "Oslepia i pali.",         listOf(AlchemyIngredient.WHITE_CINNABAR, AlchemyIngredient.NAPTHA),          38, PotionGroup.OFFENSIVE),
    THUNDERBOLT("Grom",                "Piorun w butelce.",       listOf(AlchemyIngredient.BRIMSTONE, AlchemyIngredient.MANGANES),             30, PotionGroup.OFFENSIVE),
    ARABIAN_FIRE("Ogien Arabski",      "Grecki ogien.",           listOf(AlchemyIngredient.NAPTHA, AlchemyIngredient.SULFUR_CRUDE),            45, PotionGroup.OFFENSIVE),
    STONE_TAR("Smolisty Kamien",       "Unieruchamia wroga.",     listOf(AlchemyIngredient.ZINCBLENDE, AlchemyIngredient.BASE_MELANCHOLIC),    18, PotionGroup.OFFENSIVE),
    // WZMOCNIENIA
    DEADLY_BLADE("Smiercionosne Ostrze","Zwieksza obrazenia bronia.",listOf(AlchemyIngredient.MANGANES, AlchemyIngredient.BRIMSTONE),          22, PotionGroup.BUFF),
    STRONGEDGE("Mocne Ostrze",         "+penetracja zbrojowa.",   listOf(AlchemyIngredient.ANTIMONI, AlchemyIngredient.ZINCBLENDE),           20, PotionGroup.BUFF),
    GREATPOWER("Wielka Sila",          "+Sila na 3 rundy.",       listOf(AlchemyIngredient.BASE_CHOLERIC, AlchemyIngredient.MANGANES),         25, PotionGroup.BUFF),
    TRUEFLIGHT("Pewny Lot",            "+Celnosc rzutow.",        listOf(AlchemyIngredient.BASE_SANGUINE, AlchemyIngredient.SALT),             18, PotionGroup.BUFF),
    HARDARMOR("Twarda Zbroja",         "+Ochrona zbroi.",         listOf(AlchemyIngredient.ZINCBLENDE, AlchemyIngredient.MANGANES),            28, PotionGroup.BUFF),
    TRANSFORMATION("Transformacja",    "Zmienia wygladpostaci.", listOf(AlchemyIngredient.MERCURY, AlchemyIngredient.WHITE_CINNABAR),          50, PotionGroup.BUFF),
    TRUESIGHT("Prawdziwy Wzrok",       "+Percepcja i Obserwacja.",listOf(AlchemyIngredient.BASE_PHLEGMATIC, AlchemyIngredient.ORPIMENT),      22, PotionGroup.BUFF),
    NEW_WIND("Nowy Wiatr",             "Przywraca endurance.",   listOf(AlchemyIngredient.BASE_SANGUINE, AlchemyIngredient.SALT),              20, PotionGroup.BUFF),
    IRONARM("Zelazne Ramie",           "+Sila i wytrzymalosc.",  listOf(AlchemyIngredient.MANGANES, AlchemyIngredient.BASE_CHOLERIC),          30, PotionGroup.BUFF),
    QUICKMOVE("Szybki Ruch",           "+Zwinnosc i szybkosc.",  listOf(AlchemyIngredient.BASE_SANGUINE, AlchemyIngredient.ANTIMONI),          24, PotionGroup.BUFF),
    ESSENCE_GRACE("Esencja Laski",     "Leczy HP i przywraca morale.",listOf(AlchemyIngredient.MERCURY, AlchemyIngredient.BASE_PHLEGMATIC),   35, PotionGroup.HEALING),
    FIREWALL("Sciana Ognia",           "Bariera ogniowa.",        listOf(AlchemyIngredient.NAPTHA, AlchemyIngredient.BRIMSTONE),               40, PotionGroup.OFFENSIVE)
}

enum class PotionGroup { OFFENSIVE, BUFF, HEALING }

// ==================== WYNIK PARZENIA ====================
data class BrewResult(
    val success: Boolean,
    val potionType: PotionType?,
    val quality: PotionQuality,
    val quantity: Int,
    val message: String
)

// ==================== SYSTEM ALCHEMII ====================
object AlchemyCore {
    // Szansa sukcesu wg oryginalnej formuly: k + PhStone + Int + Alch - MagicNumber
    fun brewChance(alchSkill: Int, intelligence: Int, philosopherStone: Int = 0): Int =
        (alchSkill + intelligence / 2 + philosopherStone - 10).coerceIn(5, 95)

    fun brew(
        hero: Hero,
        potion: PotionType,
        quality: PotionQuality,
        batchSize: Int = 1
    ): BrewResult {
        val alchSkill = SkillSystem.getSkill(hero, HeroSkill.ALCH)
        val chance    = brewChance(alchSkill, hero.intelligence)
        val actualBatch = batchSize.coerceIn(1, 10)
        // Kara za duze partie (oryginalna mechanika)
        val adjustedChance = (chance - (actualBatch - 3).coerceAtLeast(0) * 5).coerceIn(1, 95)
        val roll = Random.nextInt(100)
        val success = roll < adjustedChance
        if (success) {
            // Learn-by-doing
            SkillSystem.practiceSkill(hero, HeroSkill.ALCH, true)
        }
        return BrewResult(
            success    = success,
            potionType = if (success) potion else null,
            quality    = quality,
            quantity   = if (success) actualBatch else 0,
            message    = if (success)
                "Sukces! Uwarzone: ${potion.displayName} x$actualBatch (${quality.label}). Roll: $roll/$adjustedChance%"
            else
                "Porazka! Mikstura wybuchla. Roll: $roll/$adjustedChance%"
        )
    }

    fun ingredientCost(potion: PotionType, quality: PotionQuality, qty: Int): Int =
        (potion.ingredients.sumOf { it.basePrice } * quality.priceMultiplier * qty).toInt()

    fun saleValue(potion: PotionType, quality: PotionQuality): Int =
        (potion.basePrice * quality.priceMultiplier).toInt()
}
