package com.bloomwake.utils

// ═══════════════════════════════════════════════════════════════════════════
//  BLOOMWAKE PHASE ACTION ENGINE
//  The ONE thing Flo/Clue never built: tells you WHAT TO DO today, not just
//  what phase you're in. Every recommendation is specific, actionable, single.
// ═══════════════════════════════════════════════════════════════════════════

data class TodayAction(
    val priority: String,        // One bold sentence — the hero
    val why: String,             // Science reason in plain English
    val meal: MealSuggestion,
    val workout: WorkoutSuggestion,
    val mindset: String,         // One mindset tip
    val avoid: String,           // One thing NOT to do today
    val socialTip: String,       // Partner/friend tip
    val workTip: String,         // Career/productivity tip
    val sleepTip: String,        // Tonight's sleep tip
    val hydration: String,       // Specific drink/hydration
    val skinTip: String          // Skin care for this phase
)

data class MealSuggestion(
    val emoji: String,
    val name: String,
    val why: String,
    val ingredients: List<String>
)

data class WorkoutSuggestion(
    val emoji: String,
    val type: String,
    val duration: String,
    val why: String,
    val examples: List<String>
)

object PhaseActionEngine {

    fun getTodayAction(phase: CyclePhase, dayOfCycle: Int): TodayAction {
        return when (phase) {
            CyclePhase.MENSTRUAL -> menstrualAction(dayOfCycle)
            CyclePhase.FOLLICULAR -> follicularAction(dayOfCycle)
            CyclePhase.OVULATORY -> ovulatoryAction(dayOfCycle)
            CyclePhase.LUTEAL -> lutealAction(dayOfCycle)
        }
    }

    // ── MENSTRUAL ───────────────────────────────────────────────────────────
    private fun menstrualAction(day: Int) = TodayAction(
        priority = when {
            day <= 2 -> "Rest without guilt today — your body is doing its hardest work"
            day <= 4 -> "One gentle walk outside is enough. Don't push harder than that"
            else -> "You're almost through. Warmth, iron, and patience today"
        },
        why = "Prostaglandins peak in days 1–2 causing cramps. Iron drops as you bleed. " +
              "Pushing through high-intensity activity increases cortisol and worsens pain.",
        meal = MealSuggestion(
            "🍲", "Iron Recovery Bowl",
            "Rebuilds iron lost during bleeding + ginger reduces prostaglandins (cramp hormones)",
            listOf("Red lentils", "Spinach", "Ginger", "Turmeric", "Dark chocolate (70%+)")
        ),
        workout = WorkoutSuggestion(
            "🧘", "Yin Yoga or Slow Walk", "20–30 min",
            "Light movement reduces cramps via endorphins without spiking cortisol",
            listOf("Child's pose", "Supine twist", "10-min walk outside", "Light stretching")
        ),
        mindset = "Your intuition is sharpest now — journal one honest thought you've been avoiding",
        avoid = "Alcohol, caffeine, and cold foods — all worsen prostaglandin inflammation",
        socialTip = "Tell one person you need quiet today. You don't owe anyone your full energy",
        workTip = "Use this time for deep solo thinking — review, reflect, plan. Avoid new pitches",
        sleepTip = "Sleep with a hot water bottle on your lower abdomen — reduces cramp intensity",
        hydration = "Raspberry leaf tea + water with lemon — raspberry leaf tones the uterus",
        skinTip = "Your skin is most sensitive now — skip exfoliation, use calming aloe or rose water"
    )

    // ── FOLLICULAR ──────────────────────────────────────────────────────────
    private fun follicularAction(day: Int) = TodayAction(
        priority = when {
            day <= 10 -> "Start the project you've been delaying — your brain is building new neural paths"
            day <= 12 -> "Pitch the idea, send the email, say the thing — estrogen peak incoming"
            else -> "You're approaching peak power — stack your hardest tasks today"
        },
        why = "Rising estrogen boosts dopamine, serotonin, and neuroplasticity. Your brain literally " +
              "forms new connections faster now than any other phase. Ideas stick.",
        meal = MealSuggestion(
            "🥗", "Brain-Building Power Bowl",
            "Supports estrogen metabolism + feeds dopamine for creativity and focus",
            listOf("Eggs", "Leafy greens", "Flaxseeds", "Avocado", "Blueberries")
        ),
        workout = WorkoutSuggestion(
            "💪", "Strength Training or HIIT", "40–60 min",
            "Rising estrogen improves muscle recovery — your best gains happen this week",
            listOf("Weight lifting", "HIIT class", "Running", "CrossFit", "Try a new sport")
        ),
        mindset = "Write down one bold goal that scares you slightly — your risk tolerance is highest now",
        avoid = "Saying yes to everything — your enthusiasm peaks but your bandwidth doesn't",
        socialTip = "This is your best week to have important relationship conversations — you're articulate and warm",
        workTip = "Schedule your most creative work, presentations, and difficult conversations this week",
        sleepTip = "You may need slightly less sleep — 6.5–7 hours feels complete. Don't force 8",
        hydration = "Green smoothie with spirulina + water — chlorophyll supports estrogen detox via liver",
        skinTip = "Your skin is clearest now — great time to try new products. Glow is real, not imaginary"
    )

    // ── OVULATORY ───────────────────────────────────────────────────────────
    private fun ovulatoryAction(day: Int) = TodayAction(
        priority = "This is your most magnetic day of the month — show up, speak up, be seen",
        why = "Estrogen + testosterone both peak at ovulation. Vocal pitch rises, face symmetry increases, " +
              "confidence soars. Science confirms you are objectively more persuasive right now.",
        meal = MealSuggestion(
            "🥙", "Anti-Inflammatory Peak Plate",
            "Supports LH surge + reduces inflammation from ovulation itself",
            listOf("Salmon or sardines", "Quinoa", "Raw vegetables", "Pumpkin seeds", "Fresh fruit")
        ),
        workout = WorkoutSuggestion(
            "🏋️", "Heavy Lifting or Competitive Sport", "45–60 min",
            "Testosterone peak = maximum strength. You'll break personal records this week",
            listOf("Max effort lifting", "Competitive game", "Group fitness class", "Sprint intervals")
        ),
        mindset = "You are at 100% today — act like it. No shrinking, no apologising, no waiting",
        avoid = "Isolating yourself — this is the one time your social energy truly refills others",
        socialTip = "Plan a date, important meeting, or social event this week. You'll absolutely shine",
        workTip = "Negotiate today. Ask for the raise, the deal, the favour — success rate is highest now",
        sleepTip = "You may feel wired — wind down with magnesium glycinate and dimmed lights by 9pm",
        hydration = "Coconut water + electrolytes — you sweat more at ovulation, replace minerals",
        skinTip = "Natural glow is real — minimal makeup needed. Highlight your features, don't hide them"
    )

    // ── LUTEAL ──────────────────────────────────────────────────────────────
    private fun lutealAction(day: Int) = TodayAction(
        priority = when {
            day <= 21 -> "Finish existing projects — your detail focus is exceptional right now"
            day <= 24 -> "Begin slowing down intentionally — this is self-care, not laziness"
            else -> "You're in PMS territory — nourish hard, expect less from yourself today"
        },
        why = "Progesterone rises then crashes before your period. Serotonin drops, body temperature " +
              "rises slightly, and your brain enters an inward, detail-focused mode.",
        meal = MealSuggestion(
            "🍫", "PMS-Beating Comfort Bowl",
            "Magnesium reduces cramping + B6 raises serotonin + complex carbs stabilise mood",
            listOf("Sweet potato", "Dark chocolate", "Walnuts", "Chickpeas", "Chamomile tea")
        ),
        workout = WorkoutSuggestion(
            "🚶", "Pilates, Yoga or Long Walk", "30–45 min",
            "Progesterone raises body temp — high intensity feels twice as hard, not weakness",
            listOf("Pilates", "Restorative yoga", "Nature walk", "Swimming", "Light cycling")
        ),
        mindset = "The critical voice in your head is louder now — notice it, don't believe it",
        avoid = "Sugar, alcohol, and salty foods — all worsen bloating and mood crash",
        socialTip = "Lower your social commitments — saying no now prevents resentment later",
        workTip = "Perfect time for editing, reviewing, finishing, and detailed analytical work",
        sleepTip = "You need 30–60 minutes more sleep than usual — your body temperature runs higher",
        hydration = "Herbal teas: chamomile (reduces anxiety), peppermint (reduces bloating), ginger (cramps)",
        skinTip = "Breakouts increase with progesterone — switch to salicylic acid cleanser, avoid heavy creams"
    )

    // ── DAILY MANTRA — unique per day so app feels alive ───────────────────
    fun getDailyMantra(phase: CyclePhase, dayOfCycle: Int): String {
        val mantras = when (phase) {
            CyclePhase.MENSTRUAL -> listOf(
                "Rest is productive. Your body is rebuilding.",
                "You don't have to perform today.",
                "Slowness is strength in disguise.",
                "Honour the pause. Spring comes after winter.",
                "Your sensitivity today is data, not weakness."
            )
            CyclePhase.FOLLICULAR -> listOf(
                "The idea that excites you most is the right one.",
                "Begin before you're ready — that's the Follicular way.",
                "Your brain is building. Feed it challenges.",
                "This energy is a gift. Use it on what matters.",
                "Confidence isn't absence of doubt. It's action despite it."
            )
            CyclePhase.OVULATORY -> listOf(
                "You are magnetic today. Walk in knowing that.",
                "Say the thing out loud. Today it lands.",
                "Your peak is now. You earned this.",
                "Connection is your superpower this week.",
                "Speak first. The room wants to hear you."
            )
            CyclePhase.LUTEAL -> listOf(
                "Finishing is as brave as starting.",
                "Rest is the work today.",
                "Your inner world needs attention. Give it.",
                "Boundaries now prevent breakdowns later.",
                "Quiet and withdrawing is biological, not failure."
            )
        }
        return mantras[dayOfCycle % mantras.size]
    }

    // ── PARTNER TIP — for sharing with partners ─────────────────────────────
    fun getPartnerTip(phase: CyclePhase): String = when (phase) {
        CyclePhase.MENSTRUAL ->
            "She needs warmth and quiet today, not fixing. Ask 'what do you need?' then just do it."
        CyclePhase.FOLLICULAR ->
            "She's full of ideas and energy. Match her enthusiasm. This is a great week to make plans together."
        CyclePhase.OVULATORY ->
            "She's at her most social and confident. Plan something fun — she'll light up the room."
        CyclePhase.LUTEAL ->
            "She may be quieter or more critical — it's biology, not you. Extra patience goes a long way."
    }
}
