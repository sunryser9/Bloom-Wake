package com.bloomwake.utils

data class Mission(
    val id: String,
    val title: String,
    val instruction: String,
    val type: MissionType,
    val durationSeconds: Int = 0,
    val promptText: String = "",
    val insight: String,
    val scienceFact: String = ""
)

enum class MissionType { BREATHING, JOURNAL, AFFIRMATION, MOVEMENT }

object MissionGenerator {

    private val menstrualMissions = listOf(
        Mission("men_1", "Warming Breath",
            "Place both hands on your belly. Breathe in for 4 counts, hold for 4, out for 6. Repeat 5 times.",
            MissionType.BREATHING, 70,
            insight = "Slow exhales activate your parasympathetic nervous system — your body's built-in calm.",
            scienceFact = "Extended exhales increase vagal tone by up to 20%, reducing inflammation."
        ),
        Mission("men_2", "One Gentle Intention",
            "Write one soft intention for today — something you want to feel, not achieve.",
            MissionType.JOURNAL, promptText = "Today I want to feel…",
            insight = "Intentions guide energy without pressure. You're doing beautifully.",
            scienceFact = "Setting process goals vs outcome goals reduces cortisol by activating the prefrontal cortex."
        ),
        Mission("men_3", "Body Gratitude",
            "Think of one thing your body did for you this week — even just breathing through it.",
            MissionType.AFFIRMATION,
            insight = "Your body is wise. Menstruation is a sign of vitality, not weakness.",
            scienceFact = "Gratitude activates the hypothalamus, directly improving sleep and metabolism."
        ),
        Mission("men_4", "Morning Knee Rock",
            "Gently draw both knees to chest. Rock side to side for 30 seconds. Release tension slowly.",
            MissionType.MOVEMENT, 30,
            insight = "Even 30 seconds of movement shifts your nervous system into ease.",
            scienceFact = "Gentle spinal mobilization reduces prostaglandin-related cramp intensity."
        )
    )

    private val follicularMissions = listOf(
        Mission("fol_1", "5 Power Breaths",
            "Sit up tall. Inhale deeply for 5 counts, exhale for 5. Let each breath charge you.",
            MissionType.BREATHING, 60,
            insight = "Your estrogen is rising — you have more capacity for focus and creativity today.",
            scienceFact = "Estradiol peaks in follicular phase, increasing dopamine receptor sensitivity by up to 30%."
        ),
        Mission("fol_2", "Morning Spark",
            "Write one thing you're excited to try or learn today. Let it be bold.",
            MissionType.JOURNAL, promptText = "One bold thing I'm starting today…",
            insight = "Follicular phase is your brain's 'spring' — ideas flow more easily now.",
            scienceFact = "Rising estrogen boosts serotonin and acetylcholine, enhancing memory formation and learning."
        ),
        Mission("fol_3", "Power Affirmation",
            "Say out loud: 'I am building momentum. Today I begin what matters.'",
            MissionType.AFFIRMATION,
            insight = "Follicular phase is perfect for planting seeds that will bloom at ovulation.",
            scienceFact = "Self-affirmation activates the ventromedial prefrontal cortex, reducing threat response."
        ),
        Mission("fol_4", "Wake-Up Shake",
            "Stand and gently shake your hands, arms, then whole body for 20 seconds. Smile.",
            MissionType.MOVEMENT, 20,
            insight = "Shaking releases stored tension and primes your body for bold action.",
            scienceFact = "Somatic shaking (TRE) reduces stress hormones and activates the sympathetic nervous system positively."
        )
    )

    private val ovulatoryMissions = listOf(
        Mission("ovu_1", "Radiance Breath",
            "5 bold, full-body breaths. In through the nose slowly, sigh out through the mouth loudly.",
            MissionType.BREATHING, 45,
            insight = "You're at your hormonal peak. You're magnetic, articulate, and capable.",
            scienceFact = "LH and estrogen peak simultaneously at ovulation — the only time both are high at once."
        ),
        Mission("ovu_2", "Connect with Purpose",
            "Write one person you want to reach out to today and exactly what you'd like to say.",
            MissionType.JOURNAL, promptText = "I want to connect with… because…",
            insight = "Ovulatory phase boosts communication skills and empathy. Use that gift.",
            scienceFact = "Oxytocin surges at ovulation, increasing pro-social behaviour and trust-building."
        ),
        Mission("ovu_3", "Confidence Mantra",
            "Repeat three times, louder each time: 'I show up fully. I am heard. I belong here.'",
            MissionType.AFFIRMATION,
            insight = "Your LH surge peaks energy and confidence — your body is cheering you on.",
            scienceFact = "Vocal affirmations during high-estrogen phases show amplified self-efficacy effects."
        ),
        Mission("ovu_4", "Power Pose",
            "Feet wide, hands on hips, chin up for 30 seconds. Own every inch of the morning.",
            MissionType.MOVEMENT, 30,
            insight = "Embodied confidence cues your brain to feel more capable.",
            scienceFact = "Open posture held for 2+ minutes reduces cortisol and increases testosterone-adjacent confidence signals."
        )
    )

    private val lutealMissions = listOf(
        Mission("lut_1", "4-7-8 Calm",
            "Breathe in for 4 counts, hold for 7, breathe out for 8. Repeat 4 times. Slowly.",
            MissionType.BREATHING, 76,
            insight = "The 4-7-8 technique lowers cortisol quickly — especially helpful in luteal phase.",
            scienceFact = "4-7-8 breathing activates the parasympathetic nervous system within 60 seconds."
        ),
        Mission("lut_2", "Gratitude Reset",
            "Write one thing you're genuinely grateful for — no matter how small or obvious.",
            MissionType.JOURNAL, promptText = "Today I'm grateful for…",
            insight = "Gratitude practice reduces PMS-related irritability by shifting neural focus.",
            scienceFact = "Gratitude journaling increases hypothalamic activity, improving sleep and reducing PMS symptoms."
        ),
        Mission("lut_3", "Self-Compassion Pause",
            "Hand on heart. Say: 'I am enough. I am doing enough. I deserve rest.'",
            MissionType.AFFIRMATION,
            insight = "Progesterone makes you more self-critical. Counter it deliberately.",
            scienceFact = "Self-compassion practices reduce progesterone-linked anxiety by activating the insula."
        ),
        Mission("lut_4", "Gentle Flow",
            "Roll shoulders back 5 times. Gently roll your neck side to side. Slowly. Kindly.",
            MissionType.MOVEMENT, 40,
            insight = "Gentle movement reduces luteal-phase fatigue better than rest alone.",
            scienceFact = "Low-intensity movement in luteal phase increases serotonin without spiking cortisol."
        )
    )

    fun getMissionsForPhase(phase: CyclePhase): List<Mission> = when (phase) {
        CyclePhase.MENSTRUAL -> menstrualMissions
        CyclePhase.FOLLICULAR -> follicularMissions
        CyclePhase.OVULATORY -> ovulatoryMissions
        CyclePhase.LUTEAL -> lutealMissions
    }

    fun getRandomMission(phase: CyclePhase): Mission = getMissionsForPhase(phase).random()

    fun getMissionById(id: String): Mission? =
        (menstrualMissions + follicularMissions + ovulatoryMissions + lutealMissions)
            .firstOrNull { it.id == id }
}
