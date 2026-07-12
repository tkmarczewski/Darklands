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
            if (target.maxHp <= 0) return@CombatSkill SkillResult(message = "Cel jest już pokonany.")
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
            user.armor = (user.armor + 5).coerceAtMost(50)
            SkillResult(statusApplied = true, message = "Boska osłona wzmacnia pancerz o +5.")
        },

        CombatSkill(
            id = "echo_step",
            name = "Krok Echa",
            type = SkillType.ECHO,
            echoCost = 0.05f,
            description = "Nagasz rzeczywistość, by uniknąć trafienia. Zwiększa obronę kosztem echa."
        ) { user, _ ->
            user.armor += 10
            SkillResult(statusApplied = true, message = "Rzeczywistość rozmywa się wokół Ciebie. +10 Pancerza.")
        },

        CombatSkill(
            id = "righteous_fury",
            name = "Słuszny Gniew",
            type = SkillType.PRAYER,
            favorCost = 15,
            description = "Potężne uderzenie nasycone wiarą."
        ) { user, target ->
            // BALANCE FIX: Scaled with Piety instead of just attackBase
            val dmg = user.attackBase + user.piety
            target.hp = (target.hp - dmg).coerceAtLeast(0)
            SkillResult(damage = dmg, message = "Święty blask poraża wroga za $dmg obrażeń.")
        },

        CombatSkill(
            id = "mind_collapse",
            name = "Zapaść Umysłu",
            type = SkillType.ECHO,
            echoCost = 0.15f,
            description = "Przelewasz mrok prosto do umysłu wroga. Ogromne obrażenia, ale destabilizuje świat."
        ) { user, target ->
            // BALANCE FIX: Fixed damage based on Intelligence to avoid boss one-shots
            val dmg = 15 + user.intelligence
            target.hp = (target.hp - dmg).coerceAtLeast(0)
            SkillResult(damage = dmg, message = "Przerażający szept echa rozdziera jaźń przeciwnika. Zadano $dmg obrażeń.")
        },

        CombatSkill(
            id = "system_defend",
            name = "Obrona",
            type = SkillType.MELEE
        ) { _, _ ->
            SkillResult(message = "Przyjmuje postawę obronną.")
        }
    )
}
