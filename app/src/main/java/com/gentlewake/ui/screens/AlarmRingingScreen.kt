package com.bloomwake.ui.screens

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.bloomwake.ui.viewmodel.BloomWakeViewModel
import com.bloomwake.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AlarmRingingScreen(
    phase: String,
    viewModel: BloomWakeViewModel,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profile by viewModel.userProfile.collectAsState()
    val cyclePhase = CycleCalculator.phaseFromName(phase)
    val mission = remember(phase) { MissionGenerator.getRandomMission(cyclePhase) }
    val phaseColor = Color(cyclePhase.color)

    var showInsight by remember { mutableStateOf(false) }
    var journalText by remember { mutableStateOf("") }
    var timerSecondsLeft by remember { mutableIntStateOf(mission.durationSeconds) }
    var timerRunning by remember { mutableStateOf(mission.type == MissionType.BREATHING || mission.type == MissionType.MOVEMENT) }
    var breathLabel by remember { mutableStateOf("Breathe In 🌬️") }

    LaunchedEffect(timerRunning) {
        if (timerRunning && timerSecondsLeft > 0) {
            while (timerSecondsLeft > 0 && timerRunning) {
                delay(1000L)
                timerSecondsLeft--
                if (mission.type == MissionType.BREATHING) {
                    val elapsed = mission.durationSeconds - timerSecondsLeft
                    breathLabel = when {
                        elapsed % 15 < 5 -> "Breathe In 🌬️"
                        elapsed % 15 < 9 -> "Hold 🤫"
                        else -> "Breathe Out 💨"
                    }
                }
            }
            if (timerSecondsLeft == 0) timerRunning = false
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "alarm")
    val breathScale by infiniteTransition.animateFloat(
        1f, 1.12f,
        infiniteRepeatable(tween(2500, easing = EaseInOutSine), RepeatMode.Reverse),
        "breathe"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(phaseColor.copy(0.35f), Color(0xFF0A1628), Color(0xFF050D1A))
                )
            )
    ) {
        if (showInsight) {
            AlarmInsightScreen(
                phase = cyclePhase, mission = mission, profile = profile,
                onShare = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            ShareCardGenerator.generateAndShare(
                                context, cyclePhase,
                                profile?.let {
                                    it.lastPeriodDate?.let { d ->
                                        CycleCalculator.getCurrentPhase(d, it.avgCycleLength).dayOfCycle
                                    }
                                } ?: 1,
                                (profile?.currentStreak ?: 0) + 1,
                                mission.insight
                            )
                        }
                    }
                },
                onFinish = {
                    viewModel.recordCompletion()
                    onDone()
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(24.dp))

                // Phase badge
                Surface(shape = RoundedCornerShape(50), color = phaseColor.copy(0.25f)) {
                    Text(
                        "${cyclePhase.emoji} ${cyclePhase.displayName} Phase",
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp),
                        color = Color.White, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Text(
                    "Good Morning 🌸",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                // Mission card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White.copy(0.1f)
                ) {
                    Column(
                        modifier = Modifier
                            .border(1.dp, phaseColor.copy(0.4f), RoundedCornerShape(28.dp))
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(8.dp), color = phaseColor.copy(0.2f)) {
                            Text(
                                mission.type.name,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = phaseColor,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(mission.title, style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text(mission.instruction, style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(0.85f), lineHeight = 26.sp)

                        // Breathing orb
                        if (mission.type == MissionType.BREATHING) {
                            Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                                Box(Modifier.size(140.dp).scale(breathScale).background(phaseColor.copy(0.25f), CircleShape))
                                Box(Modifier.size(100.dp).scale(breathScale * 0.88f).background(phaseColor.copy(0.45f), CircleShape))
                                Text(breathLabel, color = Color.White, fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center, fontSize = 13.sp)
                            }
                        }

                        // Timer
                        if (mission.durationSeconds > 0) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Progress", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.5f))
                                    Text("${timerSecondsLeft}s", style = MaterialTheme.typography.labelSmall,
                                        color = phaseColor, fontWeight = FontWeight.Bold)
                                }
                                LinearProgressIndicator(
                                    progress = { 1f - timerSecondsLeft.toFloat() / mission.durationSeconds },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = phaseColor, trackColor = Color.White.copy(0.15f)
                                )
                            }
                        }

                        // Journal input
                        if (mission.type == MissionType.JOURNAL) {
                            OutlinedTextField(
                                value = journalText,
                                onValueChange = { journalText = it },
                                placeholder = { Text(mission.promptText, color = Color.White.copy(0.4f)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                minLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = phaseColor,
                                    unfocusedBorderColor = Color.White.copy(0.3f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White.copy(0.8f)
                                )
                            )
                        }

                        // Science fact
                        Surface(shape = RoundedCornerShape(10.dp), color = phaseColor.copy(0.15f)) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🔬", fontSize = 14.sp)
                                Text(mission.scienceFact, style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(0.7f), lineHeight = 18.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = { showInsight = true },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = phaseColor)
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(Modifier.width(8.dp))
                    Text("I Did It! ✨", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AlarmInsightScreen(
    phase: CyclePhase,
    mission: Mission,
    profile: com.bloomwake.data.UserProfile?,
    onShare: () -> Unit,
    onFinish: () -> Unit
) {
    val phaseColor = Color(phase.color)
    val newStreak = (profile?.currentStreak ?: 0) + 1
    val isStreakMilestone = newStreak % 7 == 0 || newStreak == 3 || newStreak == 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(24.dp))

        Text(if (isStreakMilestone) "🎉" else "✨", fontSize = 72.sp)

        if (isStreakMilestone) {
            Text(
                when (newStreak) {
                    1 -> "Day 1 complete!\nThe journey begins 🌱"
                    3 -> "3-day streak!\nYou're building a habit 🔥"
                    7 -> "One week! 🏆\nYou're cycle-synced!"
                    else -> "$newStreak days!\nAbsolutely unstoppable. 🔥"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        } else {
            Text("Beautiful morning!", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold, color = Color.White)
        }

        // Insight card
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White.copy(0.1f),
            modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .border(1.dp, phaseColor.copy(0.4f), RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("💡", fontSize = 18.sp)
                    Text("Today's Insight", fontWeight = FontWeight.Bold, color = phaseColor)
                }
                Text(mission.insight, style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(0.9f), lineHeight = 24.sp)
                HorizontalDivider(color = Color.White.copy(0.1f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🔬", fontSize = 16.sp)
                    Text(mission.scienceFact, style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.65f), lineHeight = 18.sp)
                }
            }
        }

        // Phase superpower
        Surface(shape = RoundedCornerShape(16.dp), color = phaseColor.copy(0.2f),
            modifier = Modifier.fillMaxWidth()
                .border(1.dp, phaseColor.copy(0.35f), RoundedCornerShape(16.dp))) {
            Row(modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(phase.emoji, fontSize = 28.sp)
                Column {
                    Text("Your ${phase.displayName} Superpower", style = MaterialTheme.typography.labelSmall,
                        color = phaseColor.copy(0.8f), fontWeight = FontWeight.Bold)
                    Text(phase.superpower, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }

        // Streak updated
        Surface(shape = RoundedCornerShape(50), color = Color(0xFFFF6B35).copy(0.9f)) {
            Text(
                "🔥 Streak: $newStreak days",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                fontWeight = FontWeight.ExtraBold,
                color = Color.White, fontSize = 16.sp
            )
        }

        // Share button
        Button(
            onClick = onShare,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = phaseColor)
        ) {
            Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Share My ${phase.displayName} Card 📸", fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.15f))
        ) {
            Text("Done ✓", fontWeight = FontWeight.SemiBold, color = Color.White)
        }
        Spacer(Modifier.height(16.dp))
    }
}
