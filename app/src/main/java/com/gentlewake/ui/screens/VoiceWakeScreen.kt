package com.bloomwake.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.bloomwake.ui.viewmodel.BloomWakeViewModel
import com.bloomwake.utils.CycleCalculator
import com.bloomwake.utils.CyclePhase
import com.bloomwake.voice.BloomVoice
import kotlinx.coroutines.delay

@Composable
fun VoiceWakeScreen(
    viewModel: BloomWakeViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsState()
    val cycleState = remember(profile) {
        profile?.lastPeriodDate?.let {
            CycleCalculator.getCurrentPhase(it, profile?.avgCycleLength ?: 28)
        }
    }
    val phase = cycleState?.phase ?: CyclePhase.FOLLICULAR
    val phaseColor = Color(phase.color)
    val voice = remember { BloomVoice(context) }
    val isSpeaking by voice.isSpeaking.collectAsState()

    var hasPlayed by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf(0) } // 0=WakeUp, 1=Evening, 2=Supplement

    // Pre-load message preview when mode changes
    val name = profile?.userName ?: ""
    val day = cycleState?.dayOfCycle ?: 1
    val missions = profile?.totalMissionsCompleted ?: 0

    LaunchedEffect(selectedMode, phase) {
        messageText = when (selectedMode) {
            0 -> voice.wakeUpMessage(phase, name, day)
            1 -> voice.eveningMessage(phase, missions)
            else -> voice.supplementReminder(phase)
        }
    }

    val modes = listOf(
        Triple("🌅", "Wake Up", "Morning motivation"),
        Triple("🌙", "Evening", "Wind-down message"),
        Triple("💊", "Supplements", "What to take today")
    )

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glow"
    )

    DisposableEffect(Unit) { onDispose { voice.destroy() } }

    fun playMessage() {
        hasPlayed = true
        voice.speak(messageText)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        phaseColor.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { voice.stop(); onBack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = phaseColor)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎙️ Voice Assistant", fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground)
                        Text("${phase.emoji} ${phase.displayName} Phase",
                            fontSize = 13.sp, color = phaseColor)
                    }
                    Spacer(Modifier.width(48.dp))
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Mode selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modes.forEachIndexed { index, (emoji, title, _) ->
                        val isSelected = selectedMode == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) phaseColor else phaseColor.copy(alpha = 0.1f))
                                .clickable { selectedMode = index; hasPlayed = false; messageText = "" }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(emoji, fontSize = 20.sp)
                                Text(title, fontSize = 11.sp,
                                    color = if (isSelected) Color.White else phaseColor,
                                    fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Big play button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(180.dp)
                ) {
                    // Outer glow ring
                    if (isSpeaking) {
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .scale(glowScale)
                                .clip(CircleShape)
                                .background(phaseColor.copy(alpha = 0.2f))
                        )
                    }
                    // Inner ring
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(phaseColor.copy(alpha = 0.15f))
                            .border(2.dp, phaseColor.copy(alpha = 0.4f), CircleShape)
                    )
                    // Main button
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(phaseColor)
                            .clickable { if (isSpeaking) voice.stop() else playMessage() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                null, tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                if (isSpeaking) "Stop" else "Play",
                                fontSize = 13.sp, color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Status text
                Text(
                    when {
                        isSpeaking -> "🔊 Speaking..."
                        hasPlayed -> "✅ Done — tap to replay"
                        else -> "Tap to hear your ${modes[selectedMode].second.lowercase()} message"
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                // Message card
                if (messageText.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = phaseColor.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(phase.emoji, fontSize = 20.sp)
                                Text("Your message", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = phaseColor)
                            }
                            Text(
                                messageText,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                // Phase transition alert
                if (cycleState?.tomorrowIsTransition == true) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(cycleState.tomorrowPhase.color).copy(alpha = 0.12f)
                        ),
                        border = BorderStroke(1.dp, Color(cycleState.tomorrowPhase.color).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(cycleState.tomorrowPhase.emoji, fontSize = 28.sp)
                            Column {
                                Text("Phase Transition Tomorrow",
                                    fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                    color = Color(cycleState.tomorrowPhase.color))
                                Text(
                                    CycleCalculator.getTomorrowTeaser(cycleState.tomorrowPhase, true),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                                Spacer(Modifier.height(4.dp))
                                TextButton(
                                    onClick = {
                                        val alert = voice.phaseTransitionAlert(cycleState.tomorrowPhase)
                                        messageText = alert
                                        voice.speak(alert)
                                    },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("🔊 Hear transition alert", fontSize = 12.sp,
                                        color = Color(cycleState.tomorrowPhase.color))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
