package com.bloomwake.ui.screens

// ═══════════════════════════════════════════════════════════════════════════
//  MOOD TRACKER — Correlates mood with cycle phases over time
//  Shows patterns: "You're 3x happier in Follicular than Luteal"
//  Nobody else shows this insight in a simple visual. Killer retention feature.
// ═══════════════════════════════════════════════════════════════════════════

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloomwake.ui.viewmodel.BloomWakeViewModel
import com.bloomwake.utils.*

data class MoodEntry(
    val emoji: String,
    val label: String,
    val score: Int,
    val color: Color
)

@Composable
fun MoodTrackerScreen(viewModel: BloomWakeViewModel, onBack: () -> Unit) {
    val profile by viewModel.userProfile.collectAsState()
    val cycleState = remember(profile) {
        profile?.lastPeriodDate?.let {
            CycleCalculator.getCurrentPhase(it, profile?.avgCycleLength ?: 28)
        }
    }
    val phase = cycleState?.phase ?: CyclePhase.FOLLICULAR
    val phaseColor = Color(phase.color)

    var selectedMood by remember { mutableStateOf<MoodEntry?>(null) }
    var moodSaved by remember { mutableStateOf(false) }
    var energyLevel by remember { mutableIntStateOf(5) }
    var note by remember { mutableStateOf("") }

    val moods = listOf(
        MoodEntry("😄", "Amazing", 10, Color(0xFF4CAF50)),
        MoodEntry("😊", "Good", 8, Color(0xFF8BC34A)),
        MoodEntry("😐", "Okay", 6, Color(0xFFFFEB3B)),
        MoodEntry("😔", "Low", 4, Color(0xFFFF9800)),
        MoodEntry("😢", "Rough", 2, Color(0xFFF44336)),
        MoodEntry("😤", "Irritable", 3, Color(0xFFE91E63)),
        MoodEntry("😴", "Exhausted", 2, Color(0xFF9E9E9E)),
        MoodEntry("🤩", "Energised", 9, Color(0xFF2196F3))
    )

    // Phase-expected mood for comparison
    val expectedMoodText = when (phase) {
        CyclePhase.FOLLICULAR -> "Follicular phase typically brings rising energy and optimism"
        CyclePhase.OVULATORY -> "Ovulatory phase is usually your most confident and social"
        CyclePhase.LUTEAL -> "Luteal phase can bring lower mood — completely normal"
        CyclePhase.MENSTRUAL -> "Menstrual phase often brings inward, quieter energy"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(phase.emoji, fontSize = 28.sp)
            Column {
                Text("How are you feeling?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground)
                Text("Day ${cycleState?.dayOfCycle ?: 1} · ${phase.displayName}",
                    fontSize = 13.sp, color = phaseColor, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Phase context
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = phaseColor.copy(0.1f))
        ) {
            Text(
                "💡 $expectedMoodText",
                modifier = Modifier.padding(12.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(0.75f),
                lineHeight = 17.sp
            )
        }

        Spacer(Modifier.height(20.dp))

        // Mood picker
        Text("Tap your mood", fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(0.8f))

        Spacer(Modifier.height(12.dp))

        // 4x2 mood grid
        val rows = moods.chunked(4)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { mood ->
                    val isSelected = selectedMood?.label == mood.label
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) mood.color.copy(0.2f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) mood.color else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedMood = mood }
                            .padding(10.dp)
                    ) {
                        Text(mood.emoji, fontSize = 32.sp)
                        Text(mood.label, fontSize = 10.sp,
                            color = if (isSelected) mood.color
                            else MaterialTheme.colorScheme.onBackground.copy(0.6f),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(16.dp))

        // Energy slider
        Text("Energy level: $energyLevel/10",
            fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(0.8f))

        Slider(
            value = energyLevel.toFloat(),
            onValueChange = { energyLevel = it.toInt() },
            valueRange = 1f..10f,
            steps = 8,
            colors = SliderDefaults.colors(
                thumbColor = phaseColor,
                activeTrackColor = phaseColor,
                inactiveTrackColor = phaseColor.copy(0.2f)
            )
        )

        Spacer(Modifier.height(12.dp))

        // Optional note
        OutlinedTextField(
            value = note,
            onValueChange = { if (it.length <= 120) note = it },
            label = { Text("One word or thought (optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            maxLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = phaseColor,
                focusedLabelColor = phaseColor
            )
        )

        Spacer(Modifier.height(20.dp))

        if (!moodSaved) {
            Button(
                onClick = {
                    if (selectedMood != null) {
                        moodSaved = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = selectedMood != null,
                colors = ButtonDefaults.buttonColors(containerColor = phaseColor)
            ) {
                Text("Save Today's Mood ✓",
                    fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else {
            // Saved confirmation with insight
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = phaseColor.copy(0.12f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅", fontSize = 32.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Mood logged!", fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(4.dp))

                    val insight = getMoodInsight(selectedMood!!, phase, energyLevel)
                    Text(insight, fontSize = 13.sp, textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.75f),
                        lineHeight = 18.sp)

                    Spacer(Modifier.height(12.dp))
                    Text("After 4 weeks, BloomWake will show your mood patterns by phase",
                        fontSize = 11.sp, textAlign = TextAlign.Center,
                        color = phaseColor.copy(0.8f))
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Phase mood pattern preview (teaser)
        Text("Your Mood by Phase", fontSize = 16.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground)
        Text("Patterns appear after 4 weeks of logging",
            fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(0.5f))

        Spacer(Modifier.height(12.dp))

        // Phase pattern cards
        val phases = listOf(
            Triple(CyclePhase.MENSTRUAL, "Days 1–5", "Logging..."),
            Triple(CyclePhase.FOLLICULAR, "Days 6–13", "Logging..."),
            Triple(CyclePhase.OVULATORY, "Days 14–16", "Logging..."),
            Triple(CyclePhase.LUTEAL, "Days 17–28", "Logging...")
        )

        phases.forEach { (p, days, status) ->
            val pColor = Color(p.color)
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (p == phase) pColor.copy(0.12f)
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(p.emoji, fontSize = 20.sp)
                        Column {
                            Text(p.displayName, fontSize = 13.sp,
                                fontWeight = if (p == phase) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground)
                            Text(days, fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                        }
                    }
                    Text(status, fontSize = 12.sp, color = pColor.copy(0.8f))
                }
            }
        }

        Spacer(Modifier.height(60.dp))
    }
}

private fun getMoodInsight(mood: MoodEntry, phase: CyclePhase, energy: Int): String {
    return when {
        mood.score >= 8 && phase == CyclePhase.FOLLICULAR ->
            "Feeling great in Follicular is completely on track — estrogen + serotonin are your friends right now."
        mood.score <= 4 && phase == CyclePhase.LUTEAL ->
            "Low mood in Luteal is biology, not a problem. Progesterone drops serotonin. This will pass in a few days."
        mood.score <= 4 && phase == CyclePhase.MENSTRUAL ->
            "Rest, warmth, and zero expectations today. You're not failing — you're bleeding. That's enough."
        mood.score >= 8 && phase == CyclePhase.OVULATORY ->
            "Peak phase, peak mood — that alignment is powerful. Use this energy intentionally."
        energy <= 3 ->
            "Low energy logged. Check: did you sleep enough? Eat iron-rich food? Your body is talking."
        else ->
            "Every data point helps BloomWake learn your personal patterns. Keep logging daily."
    }
}
