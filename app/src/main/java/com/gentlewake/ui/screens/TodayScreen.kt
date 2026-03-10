package com.bloomwake.ui.screens

// ═══════════════════════════════════════════════════════════════════════════
//  TODAY SCREEN — BloomWake's unicorn feature
//  "What should I do RIGHT NOW?" — nobody else answers this question.
//  Flo: shows data. Clue: shows data. BloomWake: tells you what to DO.
// ═══════════════════════════════════════════════════════════════════════════

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloomwake.ui.viewmodel.BloomWakeViewModel
import com.bloomwake.utils.*

@Composable
fun TodayScreen(viewModel: BloomWakeViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    val cycleState = remember(profile) {
        profile?.lastPeriodDate?.let {
            CycleCalculator.getCurrentPhase(it, profile?.avgCycleLength ?: 28)
        }
    }

    val phase = cycleState?.phase ?: CyclePhase.FOLLICULAR
    val day = cycleState?.dayOfCycle ?: 8
    val phaseColor = Color(phase.color)
    val action = remember(phase, day) { PhaseActionEngine.getTodayAction(phase, day) }
    val mantra = remember(phase, day) { PhaseActionEngine.getDailyMantra(phase, day) }
    val partnerTip = remember(phase) { PhaseActionEngine.getPartnerTip(phase) }

    var expandedSection by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── HERO — Today's Priority ──────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(phaseColor.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()) {

                Text(
                    "${phase.emoji} Today · Day $day · ${phase.displayName}",
                    fontSize = 13.sp,
                    color = phaseColor,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )

                Spacer(Modifier.height(12.dp))

                // Daily mantra
                Text(
                    "\"$mantra\"",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Spacer(Modifier.height(16.dp))

                // Hero priority card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = phaseColor.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🎯", fontSize = 20.sp)
                            Text("TODAY'S PRIORITY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = phaseColor,
                                letterSpacing = 1.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            action.priority,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 24.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            action.why,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // ── ACTION CARDS GRID ────────────────────────────────────────────
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Meal + Workout row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // MEAL
                ActionExpandCard(
                    modifier = Modifier.weight(1f),
                    emoji = action.meal.emoji,
                    title = "Eat",
                    subtitle = action.meal.name,
                    phaseColor = phaseColor,
                    expanded = expandedSection == "meal",
                    onToggle = { expandedSection = if (expandedSection == "meal") null else "meal" }
                ) {
                    Text(action.meal.why,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                        lineHeight = 17.sp)
                    Spacer(Modifier.height(8.dp))
                    action.meal.ingredients.forEach { ing ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text("•", color = phaseColor, fontWeight = FontWeight.Bold)
                            Text(ing, fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.8f))
                        }
                    }
                }

                // WORKOUT
                ActionExpandCard(
                    modifier = Modifier.weight(1f),
                    emoji = action.workout.emoji,
                    title = "Move",
                    subtitle = action.workout.type,
                    phaseColor = phaseColor,
                    expanded = expandedSection == "workout",
                    onToggle = { expandedSection = if (expandedSection == "workout") null else "workout" }
                ) {
                    Text("${action.workout.duration} · ${action.workout.why}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                        lineHeight = 17.sp)
                    Spacer(Modifier.height(8.dp))
                    action.workout.examples.forEach { ex ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text("•", color = phaseColor, fontWeight = FontWeight.Bold)
                            Text(ex, fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.8f))
                        }
                    }
                }
            }

            // Tip cards row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TipCard(Modifier.weight(1f), "💼", "Work", action.workTip, phaseColor)
                TipCard(Modifier.weight(1f), "💤", "Sleep", action.sleepTip, phaseColor)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TipCard(Modifier.weight(1f), "💧", "Hydrate", action.hydration, phaseColor)
                TipCard(Modifier.weight(1f), "✨", "Skin", action.skinTip, phaseColor)
            }

            // Mindset card - full width
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = phaseColor.copy(0.08f))
            ) {
                Row(modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top) {
                    Text("🧠", fontSize = 22.sp)
                    Column {
                        Text("MINDSET", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            color = phaseColor, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(action.mindset, fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground, lineHeight = 20.sp)
                    }
                }
            }

            // Avoid card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.3f))
            ) {
                Row(modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top) {
                    Text("⚠️", fontSize = 22.sp)
                    Column {
                        Text("AVOID TODAY", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(action.avoid, fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground, lineHeight = 20.sp)
                    }
                }
            }

            // PARTNER TIP — viral & shareable
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val shareText = "💡 BloomWake tip for partners during ${phase.displayName} phase:\n\n" +
                            "\"$partnerTip\"\n\nApp: BloomWake – Cycle Sync Alarm"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share partner tip"))
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.3f))
            ) {
                Row(modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top) {
                    Text("💌", fontSize = 22.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()) {
                            Text("PARTNER TIP", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary, letterSpacing = 1.sp)
                            Text("tap to share →", fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.tertiary.copy(0.7f))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(partnerTip, fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                            lineHeight = 19.sp)
                    }
                }
            }

            // Social tip
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top) {
                    Text("👥", fontSize = 22.sp)
                    Column {
                        Text("SOCIAL", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            color = phaseColor, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(action.socialTip, fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                            lineHeight = 19.sp)
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun TipCard(
    modifier: Modifier,
    emoji: String,
    title: String,
    content: String,
    phaseColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 16.sp)
                Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    color = phaseColor, letterSpacing = 0.8.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text(content, fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(0.75f),
                lineHeight = 15.sp)
        }
    }
}

@Composable
private fun ActionExpandCard(
    modifier: Modifier,
    emoji: String,
    title: String,
    subtitle: String,
    phaseColor: Color,
    expanded: Boolean,
    onToggle: () -> Unit,
    expandedContent: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.clickable { onToggle() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (expanded) phaseColor.copy(0.12f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                color = phaseColor, letterSpacing = 0.8.sp)
            Text(subtitle, fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground)
            Text("tap for details", fontSize = 10.sp,
                color = phaseColor.copy(0.7f))

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    expandedContent()
                }
            }
        }
    }
}
