package com.bloomwake.utils

import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class CyclePhase(
    val displayName: String,
    val tagline: String,
    val emoji: String,
    val color: Long,
    val gradientStart: Long,
    val gradientEnd: Long,
    val description: String,
    val superpower: String,
    val avoid: String,
    val energyScore: Int,
    val moodScore: Int,
    val focusScore: Int,
    val socialScore: Int
) {
    MENSTRUAL(
        "Menstrual", "Rest & Restore", "🌙",
        0xFFE57373, 0xFF8B1A1A, 0xFFFFB3B3,
        "Your body is releasing. Honour rest, warmth, and gentle nourishment.",
        "Deep intuition & clarity", "Over-scheduling and big decisions",
        4, 5, 7, 3
    ),
    FOLLICULAR(
        "Follicular", "High Energy Day", "🌱",
        0xFF81C784, 0xFF1B5E20, 0xFFC8E6C9,
        "Estrogen rises. Brain is sharp, energy builds. Best phase for starting things.",
        "Creativity & bold decisions", "Playing it safe",
        8, 8, 9, 7
    ),
    OVULATORY(
        "Ovulatory", "Peak Power", "✨",
        0xFFFFD54F, 0xFFE65100, 0xFFFFF9C4,
        "You're at your hormonal peak. Communication, connection, and charisma are maxed.",
        "Magnetism & persuasion", "Isolation and self-doubt",
        10, 10, 8, 10
    ),
    LUTEAL(
        "Luteal", "Reflect & Recharge", "🍂",
        0xFFBA68C8, 0xFF4A148C, 0xFFE1BEE7,
        "Progesterone rises. Your body asks for slowdown and nourishment.",
        "Detail-oriented & empathetic", "Caffeine overload and people-pleasing",
        5, 5, 6, 4
    )
}

data class CycleState(
    val phase: CyclePhase,
    val dayOfCycle: Int,
    val daysUntilNextPhase: Int,
    val tomorrowPhase: CyclePhase,
    val tomorrowIsTransition: Boolean,
    val cycleProgressPercent: Float
)

data class PhaseInsight(val headline: String, val body: String, val action: String)

object CycleCalculator {

    fun getCurrentPhase(lastPeriodDate: LocalDate, avgCycleLength: Int = 28): CycleState {
        val today = LocalDate.now()
        val daysSince = ChronoUnit.DAYS.between(lastPeriodDate, today).toInt()
        val dayOfCycle = (daysSince % avgCycleLength) + 1
        val tomorrowDay = (dayOfCycle % avgCycleLength) + 1
        val phase = phaseForDay(dayOfCycle)
        val tomorrowPhase = phaseForDay(tomorrowDay)
        return CycleState(
            phase = phase,
            dayOfCycle = dayOfCycle,
            daysUntilNextPhase = daysUntilNextPhase(dayOfCycle, phase, avgCycleLength),
            tomorrowPhase = tomorrowPhase,
            tomorrowIsTransition = tomorrowPhase != phase,
            cycleProgressPercent = dayOfCycle.toFloat() / avgCycleLength
        )
    }

    fun phaseForDay(day: Int): CyclePhase = when {
        day <= 5 -> CyclePhase.MENSTRUAL
        day <= 13 -> CyclePhase.FOLLICULAR
        day <= 15 -> CyclePhase.OVULATORY
        else -> CyclePhase.LUTEAL
    }

    fun daysUntilNextPhase(day: Int, phase: CyclePhase, cycleLength: Int): Int = when (phase) {
        CyclePhase.MENSTRUAL -> 5 - day + 1
        CyclePhase.FOLLICULAR -> 13 - day + 1
        CyclePhase.OVULATORY -> 15 - day + 1
        CyclePhase.LUTEAL -> cycleLength - day + 1
    }

    fun phaseFromName(name: String): CyclePhase =
        CyclePhase.entries.firstOrNull { it.name == name } ?: CyclePhase.FOLLICULAR

    fun getDailyInsight(phase: CyclePhase, dayOfCycle: Int): PhaseInsight = when (phase) {
        CyclePhase.MENSTRUAL -> PhaseInsight(
            "Your body is wise. Let it lead.",
            "Day $dayOfCycle is a day for inward magic. The veil is thin between you and your deepest knowing.",
            "Drink something warm. Cancel one thing that isn't essential."
        )
        CyclePhase.FOLLICULAR -> PhaseInsight(
            "The world is yours to shape today.",
            "Day $dayOfCycle — your estrogen peak is building neural connections faster than any other phase. Ideas born now have legs.",
            "Start the project. Send the email. Say the thing."
        )
        CyclePhase.OVULATORY -> PhaseInsight(
            "You are magnetic right now.",
            "Day $dayOfCycle — this is your evolutionary peak. Your voice carries further. Your presence is felt.",
            "Have the important conversation. Show up. Be seen."
        )
        CyclePhase.LUTEAL -> PhaseInsight(
            "The critic in your head isn't truth.",
            "Day $dayOfCycle — progesterone whispers hard stories. Name them, then return to what you know is real.",
            "Move gently. Eat magnesium-rich food. Sleep before midnight."
        )
    }

    fun getTomorrowTeaser(tomorrowPhase: CyclePhase, isTransition: Boolean): String {
        if (!isTransition) return "More ${tomorrowPhase.displayName} energy tomorrow"
        return when (tomorrowPhase) {
            CyclePhase.FOLLICULAR -> "Tomorrow: Follicular begins 🌱 — energy starts rising"
            CyclePhase.OVULATORY -> "Tomorrow: Ovulatory peak ✨ — prepare to feel unstoppable"
            CyclePhase.LUTEAL -> "Tomorrow: Luteal begins 🍂 — schedule gentleness in"
            CyclePhase.MENSTRUAL -> "Tomorrow: Bleed may begin 🌙 — prepare comfort rituals"
        }
    }

    fun getShareCaption(phase: CyclePhase, streak: Int, dayOfCycle: Int): String = when (phase) {
        CyclePhase.MENSTRUAL -> "Day $dayOfCycle 🌙 In my rest era. Not lazy — regenerating. $streak day streak with #BloomWake #CycleSync #InMyMenstrualEra"
        CyclePhase.FOLLICULAR -> "Follicular activated 🌱✨ Brain at its sharpest and I'm here for it. $streak days cycle-synced. #BloomWake #FollicularPhase"
        CyclePhase.OVULATORY -> "Ovulation szn 🔥 Peak confidence, peak energy, peak ME. $streak days strong. #BloomWake #OvulatoryPhase #HerPeakSeason"
        CyclePhase.LUTEAL -> "Luteal check-in 🍂 Slower, softer, wiser. Gentle reset complete. $streak days strong. #BloomWake #LutealPhase #CycleAware"
    }
}
