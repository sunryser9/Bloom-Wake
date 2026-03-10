package com.bloomwake.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.bloomwake.utils.CyclePhase
import com.bloomwake.utils.MissionGenerator
import com.bloomwake.utils.MissionType
import kotlinx.coroutines.delay

/**
 * Instant "aha moment" screen shown before onboarding.
 * Users feel the value within 60 seconds — before committing to setup.
 */
@Composable
fun InstantDemoScreen(onDone: () -> Unit) {
    // Use Follicular as demo — highest energy, most aspirational
    val phase = CyclePhase.FOLLICULAR
    val mission = MissionGenerator.getMissionsForPhase(phase)[0] // 5 deep breaths
    val phaseColor = Color(phase.color)

    var step by remember { mutableIntStateOf(0) } // 0=intro, 1=mission, 2=insight
    var breathCount by remember { mutableIntStateOf(0) }
    var timerLeft by remember { mutableIntStateOf(mission.durationSeconds) }
    var timerRunning by remember { mutableStateOf(false) }
    var breathLabel by remember { mutableStateOf("Breathe In 🌬️") }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (timerLeft > 0) {
                delay(1000L)
                timerLeft--
                val elapsed = mission.durationSeconds - timerLeft
                breathLabel = when {
                    elapsed % 15 < 5 -> "Breathe In 🌬️"
                    elapsed % 15 < 9 -> "Hold 🤫"
                    else -> "Breathe Out 💨"
                }
            }
            timerRunning = false
            step = 2
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "demo")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(2500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "breath"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(phaseColor.copy(0.35f), Color(0xFF0A1628))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        when (step) {
            // ── STEP 0: INTRO ─────────────────────────────────────────────────
            0 -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Text("🌸", fontSize = 80.sp, modifier = Modifier.scale(breathScale))
                Text(
                    "Feel the difference\nin 60 seconds",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    "No sign-up needed.\nJust try one mission — right now.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(0.7f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { step = 1; timerRunning = true },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = phaseColor)
                ) {
                    Text("Try My Free Mission ✨", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                TextButton(onClick = onDone) {
                    Text("Skip to setup →", color = Color.White.copy(0.5f))
                }
            }

            // ── STEP 1: LIVE MISSION ──────────────────────────────────────────
            1 -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Text("${phase.emoji} ${phase.displayName} Demo", color = Color.White.copy(0.6f),
                    style = MaterialTheme.typography.labelLarge)
                Text(mission.title, style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                Text(mission.instruction, style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(0.85f), textAlign = TextAlign.Center, lineHeight = 26.sp)

                // Breathing orb
                Box(modifier = Modifier.size(180.dp), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(breathScale)
                            .background(phaseColor.copy(0.3f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(breathScale * 0.9f)
                            .background(phaseColor.copy(0.5f), CircleShape)
                    )
                    Text(breathLabel, color = Color.White, fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center, fontSize = 14.sp)
                }

                // Timer
                Surface(shape = RoundedCornerShape(50), color = Color.White.copy(0.15f)) {
                    Text(
                        "${timerLeft}s remaining",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        color = Color.White, fontWeight = FontWeight.SemiBold
                    )
                }

                LinearProgressIndicator(
                    progress = { 1f - timerLeft.toFloat() / mission.durationSeconds },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = phaseColor,
                    trackColor = Color.White.copy(0.2f)
                )

                OutlinedButton(
                    onClick = { step = 2 },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(0.3f))
                ) {
                    Text("Done Early ✓", color = Color.White)
                }
            }

            // ── STEP 2: INSIGHT + CONVERSION ─────────────────────────────────
            2 -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Text("🎉", fontSize = 64.sp)
                Text("That's the BloomWake difference.", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)

                Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(0.12f),
                    modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("💡 Science says:", color = phaseColor, fontWeight = FontWeight.Bold)
                        Text(mission.scienceFact, color = Color.White.copy(0.85f),
                            style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
                        Text(mission.insight, color = Color.White.copy(0.7f),
                            style = MaterialTheme.typography.bodySmall, lineHeight = 20.sp)
                    }
                }

                Text(
                    "Every morning is different based on your cycle phase.\nWe give you the right ritual every day.",
                    color = Color.White.copy(0.65f), textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = phaseColor)
                ) {
                    Text("Set Up My Cycle →", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
