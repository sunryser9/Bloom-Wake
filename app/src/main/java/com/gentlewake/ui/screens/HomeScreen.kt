package com.bloomwake.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.bloomwake.R
import com.bloomwake.ui.viewmodel.BloomWakeViewModel
import com.bloomwake.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    viewModel: BloomWakeViewModel,
    onSetAlarm: () -> Unit,
    onAlarmRing: (String) -> Unit,
    onSymptomLog: () -> Unit = {},
    onSupplements: () -> Unit = {},
    onPhaseGuide: () -> Unit = {},
    onVoiceWake: () -> Unit = {},
    onToday: () -> Unit = {},
    onMoodTracker: () -> Unit = {},
    onCalendar: () -> Unit = {}
) {
    val profile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.checkStreak() }

    val cycleState = remember(profile) {
        profile?.lastPeriodDate?.let {
            CycleCalculator.getCurrentPhase(it, profile?.avgCycleLength ?: 28)
        }
    }

    val phase = cycleState?.phase ?: CyclePhase.FOLLICULAR
    val insight = remember(phase, cycleState?.dayOfCycle) {
        CycleCalculator.getDailyInsight(phase, cycleState?.dayOfCycle ?: 1)
    }

    // Animated background pulse
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val bgAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f, targetValue = 0.22f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "bg_alpha"
    )

    val phaseColor = Color(phase.color)
    val phaseColorMuted = phaseColor.copy(alpha = bgAlpha)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to phaseColor.copy(alpha = bgAlpha),
                        0.4f to MaterialTheme.colorScheme.background,
                        1.0f to MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── TOP BAR ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "BloomWake 🌸",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    val greeting = when (java.time.LocalTime.now().hour) {
                        in 4..11 -> "Good morning"
                        in 12..16 -> "Good afternoon"
                        else -> "Good evening"
                    }
                    Text(
                        greeting,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
                // Streak flame badge
                StreakBadge(streak = profile?.currentStreak ?: 0)
            }

            // ── HERO PHASE CARD ───────────────────────────────────────────────
            PhaseHeroCard(
                phase = phase,
                cycleState = cycleState,
                insight = insight,
                phaseColor = phaseColor
            )

            // ── TOMORROW PREVIEW ─────────────────────────────────────────────
            cycleState?.let { state ->
                TomorrowCard(
                    tomorrowPhase = state.tomorrowPhase,
                    isTransition = state.tomorrowIsTransition,
                    phaseColor = phaseColor
                )
            }

            // ── STATS ROW ─────────────────────────────────────────────────────
            StatsRow(
                streak = profile?.currentStreak ?: 0,
                longestStreak = profile?.longestStreak ?: 0,
                totalMissions = profile?.totalMissionsCompleted ?: 0,
                phaseColor = phaseColor
            )

            // ── ALARM CARD ────────────────────────────────────────────────────
            AlarmCard(
                hour = profile?.alarmHour ?: 7,
                minute = profile?.alarmMinute ?: 0,
                enabled = profile?.alarmEnabled ?: false,
                phase = phase,
                onSetAlarm = onSetAlarm,
                onTestMission = { onAlarmRing(phase.name) }
            )

            // ── DAILY INSIGHT CARD ────────────────────────────────────────────
            DailyInsightCard(insight = insight, phaseColor = phaseColor)

            // ── FREEMIUM CARD ─────────────────────────────────────────────────
            FreemiumCard(weeklyCount = profile?.weeklyMissionCount ?: 0)

            // ── VIRAL SHARE CARD ──────────────────────────────────────────────
            ViralShareCard(
                phase = phase,
                dayOfCycle = cycleState?.dayOfCycle ?: 1,
                streak = profile?.currentStreak ?: 0,
                insight = insight.headline,
                onShare = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            ShareCardGenerator.generateAndShare(
                                context, phase,
                                cycleState?.dayOfCycle ?: 1,
                                profile?.currentStreak ?: 0,
                                insight.headline
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── TODAY'S PRIORITY TEASER ───────────────────────────────────
            val todayAction = remember(cycleState) {
                cycleState?.let {
                    com.bloomwake.utils.PhaseActionEngine.getTodayAction(it.phase, it.dayOfCycle)
                }
            }
            if (todayAction != null) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                        .background(phaseColor.copy(alpha = 0.1f))
                        .clickable { onToday() }
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🎯", fontSize = 16.sp)
                                Text("TODAY'S PRIORITY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = phaseColor,
                                    letterSpacing = 1.sp)
                            }
                            Text("See full plan →",
                                fontSize = 11.sp,
                                color = phaseColor.copy(0.8f),
                                fontWeight = FontWeight.Medium)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            todayAction.priority,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── FEATURE GRID ──────────────────────────────────────────────
            Text(
                "Your Toolkit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            val featureItems = listOf(
                Triple("🎯", "Today's Plan", onToday),         // UNICORN
                Triple("📅", "Cycle Calendar", onCalendar),    // UNICORN
                Triple("😊", "Mood Tracker", onMoodTracker),   // UNICORN
                Triple("🎙️", "Voice Morning", onVoiceWake),
                Triple("📋", "Daily Check-in", onSymptomLog),
                Triple("💊", "Supplements", onSupplements),
                Triple("📖", "Phase Guide", onPhaseGuide)
            )

            val rows = featureItems.chunked(2)
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { (emoji, label, action) ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { action() },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = phaseColor.copy(alpha = 0.1f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, phaseColor.copy(alpha = 0.25f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(emoji, fontSize = 24.sp)
                                Column {
                                    Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onBackground)
                                    Text("Tap →", fontSize = 11.sp, color = phaseColor)
                                }
                            }
                        }
                    }
                }
            }

            // ── IN-APP REVIEW NUDGE (shows after 7+ streak) ─────────────
            val streak = profile?.currentStreak ?: 0
            if (streak >= 7) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = phaseColor.copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("⭐", fontSize = 28.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "$streak days strong! Loving BloomWake?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "A review helps other women find this app 🌸",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // ── MEDICAL DISCLAIMER (tappable → opens privacy policy) ────
            Text(
                "⚠️ BloomWake is a wellness app, not medical advice. " +
                "Do not use for contraception or medical decisions. " +
                "Tap for full privacy policy.",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                lineHeight = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://sites.google.com/view/bloomwake-privacy"))
                        context.startActivity(intent)
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── COMPONENTS ────────────────────────────────────────────────────────────────

@Composable
fun StreakBadge(streak: Int) {
    val scale by animateFloatAsState(
        targetValue = if (streak > 0) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "streak_scale"
    )
    Surface(
        shape = RoundedCornerShape(50),
        color = if (streak >= 7) Color(0xFFFF6B35) else MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier.scale(scale)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("🔥", fontSize = 20.sp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "$streak",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (streak >= 7) Color.White else MaterialTheme.colorScheme.onTertiaryContainer,
                    fontSize = 20.sp
                )
                Text(
                    "days",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (streak >= 7) Color.White.copy(0.8f) else MaterialTheme.colorScheme.onTertiaryContainer.copy(0.7f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun PhaseHeroCard(
    phase: CyclePhase,
    cycleState: CycleState?,
    insight: PhaseInsight,
    phaseColor: Color
) {
    val emojiScale by rememberInfiniteTransition(label = "emoji").animateFloat(
        initialValue = 1f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "emoji_scale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            phaseColor.copy(alpha = 0.28f),
                            phaseColor.copy(alpha = 0.10f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(phaseColor.copy(0.6f), phaseColor.copy(0.1f))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val womanRes = when (phase) {
                    CyclePhase.FOLLICULAR -> R.drawable.woman_follicular
                    CyclePhase.OVULATORY -> R.drawable.woman_ovulatory
                    CyclePhase.LUTEAL -> R.drawable.woman_luteal
                    CyclePhase.MENSTRUAL -> R.drawable.woman_menstrual
                }
                Image(
                    painter = painterResource(womanRes),
                    contentDescription = "${phase.displayName} phase illustration",
                    modifier = Modifier
                        .size(140.dp)
                        .scale(emojiScale)
                )
                Text(
                    phase.emoji,
                    fontSize = 28.sp
                )
                Text(
                    "${phase.displayName} Phase",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = phaseColor.copy(alpha = 0.25f)
                ) {
                    Text(
                        phase.tagline.uppercase(),
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = phaseColor,
                        letterSpacing = 1.5.sp
                    )
                }
                cycleState?.let { state ->
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        PhaseStatChip("Day ${state.dayOfCycle}", "of cycle", phaseColor)
                        PhaseStatChip("${state.daysUntilNextPhase}", "days left", phaseColor)
                    }
                    // Cycle progress bar
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LinearProgressIndicator(
                            progress = { state.cycleProgressPercent },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = phaseColor,
                            trackColor = phaseColor.copy(alpha = 0.2f)
                        )
                        Text(
                            "Cycle progress",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.4f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                // Scores row
                EnergyScoreRow(phase = phase, phaseColor = phaseColor)
            }
        }
    }
}

@Composable
fun PhaseStatChip(value: String, label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        }
    }
}

@Composable
fun EnergyScoreRow(phase: CyclePhase, phaseColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ScoreBar("Energy", phase.energyScore, phaseColor)
        ScoreBar("Mood", phase.moodScore, phaseColor)
        ScoreBar("Focus", phase.focusScore, phaseColor)
        ScoreBar("Social", phase.socialScore, phaseColor)
    }
}

@Composable
fun ScoreBar(label: String, score: Int, color: Color) {
    val animatedScore by animateFloatAsState(
        targetValue = score / 10f,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "score_$label"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(60.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(animatedScore)
                    .background(color.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(animatedScore)
                    .background(
                        Brush.verticalGradient(listOf(color, color.copy(0.4f))),
                        RoundedCornerShape(4.dp)
                    )
            )
        }
        Text(
            "$score",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
            fontSize = 9.sp
        )
    }
}

@Composable
fun TomorrowCard(tomorrowPhase: CyclePhase, isTransition: Boolean, phaseColor: Color) {
    val tomorrowColor = Color(tomorrowPhase.color)
    val teaser = CycleCalculator.getTomorrowTeaser(tomorrowPhase, isTransition)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isTransition) tomorrowColor.copy(0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(0.6f),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isTransition) Modifier.border(1.dp, tomorrowColor.copy(0.4f), RoundedCornerShape(18.dp)) else Modifier)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                if (isTransition) tomorrowPhase.emoji else "🌿",
                fontSize = 28.sp
            )
            Column {
                Text(
                    if (isTransition) "⚡ Phase transition tomorrow" else "Tomorrow",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isTransition) tomorrowColor else MaterialTheme.colorScheme.onSurface.copy(0.5f)
                )
                Text(
                    teaser,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                )
            }
        }
    }
}

@Composable
fun StatsRow(streak: Int, longestStreak: Int, totalMissions: Int, phaseColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard("🔥", "$streak", "Current\nStreak", phaseColor, Modifier.weight(1f))
        StatCard("🏆", "$longestStreak", "Best\nStreak", phaseColor, Modifier.weight(1f))
        StatCard("✨", "$totalMissions", "Missions\nDone", phaseColor, Modifier.weight(1f))
    }
}

@Composable
fun StatCard(emoji: String, value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 22.sp)
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(0.5f), fontSize = 10.sp)
        }
    }
}

@Composable
fun AlarmCard(
    hour: Int, minute: Int, enabled: Boolean,
    phase: CyclePhase, onSetAlarm: () -> Unit, onTestMission: () -> Unit
) {
    val phaseColor = Color(phase.color)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Morning Alarm", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                Text(
                    String.format("%02d:%02d", hour, minute),
                    fontSize = 44.sp, fontWeight = FontWeight.ExtraBold,
                    color = if (enabled) phaseColor else MaterialTheme.colorScheme.onSurface.copy(0.4f)
                )
                Text(
                    if (enabled) "✅ Active — ${phase.emoji} ${phase.displayName}" else "⏸ Alarm off",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) phaseColor.copy(0.8f) else MaterialTheme.colorScheme.onSurface.copy(0.4f)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.End) {
                Button(
                    onClick = onSetAlarm, shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = phaseColor)
                ) {
                    Icon(Icons.Default.Alarm, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Set Alarm")
                }
                OutlinedButton(
                    onClick = onTestMission, shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, phaseColor.copy(0.4f))
                ) {
                    Text("Try Mission Now ✨", style = MaterialTheme.typography.labelSmall, color = phaseColor)
                }
            }
        }
    }
}

@Composable
fun DailyInsightCard(insight: PhaseInsight, phaseColor: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = phaseColor.copy(alpha = 0.08f),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, phaseColor.copy(0.2f), RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Today's Insight",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = phaseColor,
                letterSpacing = 1.sp
            )
            Text(
                insight.headline,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                insight.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                lineHeight = 22.sp
            )
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = phaseColor.copy(0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 16.sp)
                    Text(
                        insight.action,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.85f)
                    )
                }
            }
        }
    }
}

@Composable
fun FreemiumCard(weeklyCount: Int) {
    val left = (3 - weeklyCount).coerceAtLeast(0)
    val isExhausted = left == 0
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isExhausted) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.secondaryContainer.copy(0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(if (isExhausted) "🔒" else "⭐", fontSize = 24.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (isExhausted) "Upgrade for unlimited missions" else "$left free missions left this week",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    if (isExhausted) "BloomWake Premium — all phases, all missions" else "Free plan · 3 per week",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                )
            }
            if (isExhausted) {
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFF6B35)) {
                    Text(
                        "Upgrade",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ViralShareCard(
    phase: CyclePhase,
    dayOfCycle: Int,
    streak: Int,
    insight: String,
    onShare: () -> Unit
) {
    val phaseColor = Color(phase.color)
    var sharing by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(phaseColor.copy(0.3f), phaseColor.copy(0.15f))
                    ),
                    RoundedCornerShape(20.dp)
                )
                .border(1.dp, phaseColor.copy(0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📸", fontSize = 22.sp)
                    Text(
                        "Share Your Phase Card",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    "Generate a beautiful ${phase.displayName} card • Day $dayOfCycle • $streak day streak",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.65f)
                )
                // Mini card preview
                ShareCardPreview(phase = phase, phaseColor = phaseColor, dayOfCycle = dayOfCycle, streak = streak)

                Button(
                    onClick = {
                        sharing = true
                        onShare()
                        sharing = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = phaseColor)
                ) {
                    if (sharing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Share ${phase.emoji} ${phase.displayName} Card",
                        fontWeight = FontWeight.Bold
                    )
                }

                // Text-only share
                val context = LocalContext.current
                TextButton(
                    onClick = {
                        val caption = CycleCalculator.getShareCaption(phase, streak, dayOfCycle)
                        context.startActivity(Intent.createChooser(
                            Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, caption) },
                            "Share"
                        ))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Share as text instead",
                        style = MaterialTheme.typography.labelMedium,
                        color = phaseColor
                    )
                }
            }
        }
    }
}

@Composable
fun ShareCardPreview(phase: CyclePhase, phaseColor: Color, dayOfCycle: Int, streak: Int) {
    // Visual mini-preview of what the share card looks like
    Surface(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(phaseColor, phaseColor.copy(0.6f)))
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("🌸 BLOOMWAKE", fontSize = 9.sp, color = Color.White.copy(0.7f), letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(phase.emoji, fontSize = 32.sp)
                    Text(phase.displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text(phase.tagline, fontSize = 10.sp, color = Color.White.copy(0.8f))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("DAY", fontSize = 9.sp, color = Color.White.copy(0.7f))
                    Text("$dayOfCycle", fontWeight = FontWeight.ExtraBold, fontSize = 36.sp, color = Color.White)
                    Text("🔥 $streak days", fontSize = 11.sp, color = Color.White.copy(0.9f))
                }
            }
        }
    }
}
