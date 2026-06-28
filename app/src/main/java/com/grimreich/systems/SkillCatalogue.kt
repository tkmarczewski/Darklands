package com.grimreich.systems

import com.grimreich.core.CombatSkill
import com.grimreich.core.SkillType
import com.grimreich.core.SkillResult
import com.grimreich.core.StatusEffect
import com.grimreich.core.StatusEffectType

object SkillCatalogue {

    val allSkills = listOf(
        CombatSkill(
            id = "poison_blade",
            name = "Ostrze Trucizny",
            type = SkillType.MELEE,
            staminaCost = 5
        ) { _, target ->
            target.activeEffects.add(StatusEffect(StatusEffectType.POISON, 4, 3))
            SkillResult(damage = 0, statusApplied = true, message = "Broń ocieka trucizną! Wróg zostaje otruty.")
        },

        CombatSkill(
            id = "bash",
            name = "Taran",
            type = SkillType.MELEE,
            staminaCost = 8
        ) { user, target ->
            val dmg = (user.strength / 2) + 6
            target.hp = (target.hp - dmg).coerceAtLeast(0)
            SkillResult(damage = dmg, message = "Potężne uderzenie tarczą: $dmg obrażeń.")
        },

        CombatSkill(
            id = "prayer_shield",
            name = "Modlitwa Ochrony",
            type = SkillType.PRAYER,
            favorCost = 5
        ) { user, _ ->
            user.armor += 5
            SkillResult(statusApplied = true, message = "Boska osłona wzmacnia pancerz o +5.")
        }
    )
}
