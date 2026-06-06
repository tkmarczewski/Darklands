package com.grimreich.systems

import com.grimreich.core.CombatSkill
import com.grimreich.core.SkillType
import com.grimreich.core.StatusEffect
import com.grimreich.core.StatusEffectType
import kotlin.random.Random

object SkillCatalogue {
    
    val allSkills = listOf(
        CombatSkill("bash", "Taran", SkillType.MELEE, staminaCost = 10, description = "Silny cios tarczą. Szansa na ogłuszenie.") { user, target ->
            val dmg = 5 + user.strength / 2
            target.hp -= dmg
            if (Random.nextFloat() < 0.3f) {
                target.morale -= 15
                "Bohater uderza taranem! $dmg obrażeń. Przeciwnik traci morale."
            } else {
                "Bohater uderza taranem! $dmg obrażeń."
            }
        },
        CombatSkill("holy_strike", "Święte Pchnięcie", SkillType.PRAYER, favorCost = 20, description = "Atak pobłogosławioną bronią.") { user, target ->
            val dmg = 10 + user.intelligence / 2
            target.hp -= dmg
            target.activeEffects.add(StatusEffect(StatusEffectType.FIRE, 2, 5))
            "Święty ogień oczyszcza wroga! $dmg obrażeń i podpalenie."
        },
        CombatSkill("poison_blade", "Zatrucie ostrza", SkillType.ALCHEMY, staminaCost = 5, description = "Nakłada trzustkę na broń.") { _, target ->
            target.activeEffects.add(StatusEffect(StatusEffectType.POISON, 4, 3))
            "Broń ocieka trucizną! Wróg zostaje otruty."
        }
    )
}
