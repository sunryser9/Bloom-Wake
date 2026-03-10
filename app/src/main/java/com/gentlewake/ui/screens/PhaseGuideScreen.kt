package com.bloomwake.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.bloomwake.ui.viewmodel.BloomWakeViewModel
import com.bloomwake.utils.CycleCalculator
import com.bloomwake.utils.CyclePhase

@Composable
fun PhaseGuideScreen(
    viewModel: BloomWakeViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val cycleState = remember(profile) {
        profile?.lastPeriodDate?.let {
            CycleCalculator.getCurrentPhase(it, profile?.avgCycleLength ?: 28)
        }
    }
    val currentPhase = cycleState?.phase ?: CyclePhase.FOLLICULAR
    var selectedPhase by remember { mutableStateOf(currentPhase) }
    val phaseColor = Color(selectedPhase.color)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(phaseColor.copy(alpha = 0.35f), MaterialTheme.colorScheme.background)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = phaseColor)
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Cycle Guide", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground)
                            Text("Understand every phase of your cycle",
                                fontSize = 14.sp, color = phaseColor)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                // Phase selector tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CyclePhase.entries.forEach { phase ->
                        val isSelected = phase == selectedPhase
                        val isCurrent = phase == currentPhase
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color(phase.color)
                                    else Color(phase.color).copy(alpha = 0.15f)
                                )
                                .border(
                                    if (isCurrent) BorderStroke(2.dp, Color(phase.color)) else BorderStroke(0.dp, Color.Transparent),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedPhase = phase }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(phase.emoji, fontSize = 18.sp)
                                Text(
                                    phase.displayName.take(3),
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White else Color(phase.color),
                                    fontWeight = FontWeight.Bold
                                )
                                if (isCurrent) {
                                    Text("NOW", fontSize = 8.sp,
                                        color = if (isSelected) Color.White else Color(phase.color))
                                }
                            }
                        }
                    }
                }

                // Phase hero card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = phaseColor.copy(alpha = 0.12f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(selectedPhase.emoji, fontSize = 40.sp)
                            Column {
                                Text(selectedPhase.displayName, fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground)
                                Text(selectedPhase.tagline, fontSize = 14.sp, color = phaseColor)
                            }
                        }
                        Text(selectedPhase.description, fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            lineHeight = 22.sp)
                    }
                }

                // Energy scores
                Text("Phase Energy Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        Triple("⚡", "Energy", selectedPhase.energyScore),
                        Triple("😊", "Mood", selectedPhase.moodScore),
                        Triple("🎯", "Focus", selectedPhase.focusScore),
                        Triple("🤝", "Social", selectedPhase.socialScore)
                    ).forEach { (emoji, label, score) ->
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = phaseColor.copy(alpha = 0.08f))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(emoji, fontSize = 18.sp)
                                Text(label, fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                Text("$score/10", fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold, color = phaseColor)
                                // Mini bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(phaseColor.copy(alpha = 0.15f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(score / 10f)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(phaseColor)
                                    )
                                }
                            }
                        }
                    }
                }

                // Superpower & avoid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("✨ Superpower", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                            Text(selectedPhase.superpower, fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE57373).copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("⚠️ Avoid", fontSize = 12.sp, color = Color(0xFFE57373), fontWeight = FontWeight.Bold)
                            Text(selectedPhase.avoid, fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                // Best activities
                Text("Best activities this phase", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground)

                val activities = when (selectedPhase) {
                    CyclePhase.MENSTRUAL -> listOf(
                        "🧘" to "Yoga & stretching",
                        "📔" to "Journaling",
                        "🛁" to "Warm baths",
                        "📚" to "Reading & learning",
                        "🌙" to "Early sleep",
                        "🍵" to "Herbal tea rituals"
                    )
                    CyclePhase.FOLLICULAR -> listOf(
                        "🚀" to "Start new projects",
                        "🏋️" to "High intensity workouts",
                        "🤝" to "Networking",
                        "🎨" to "Creative work",
                        "📅" to "Plan your month",
                        "💡" to "Brainstorming"
                    )
                    CyclePhase.OVULATORY -> listOf(
                        "🎤" to "Public speaking",
                        "💼" to "Important meetings",
                        "💃" to "Social events",
                        "🔥" to "HIIT training",
                        "💬" to "Difficult conversations",
                        "📸" to "Content creation"
                    )
                    CyclePhase.LUTEAL -> listOf(
                        "🎯" to "Detail-oriented tasks",
                        "🧹" to "Organising & cleaning",
                        "🏃" to "Moderate exercise",
                        "🫂" to "Nurturing relationships",
                        "🌿" to "Meal prep",
                        "💜" to "Self-care rituals"
                    )
                }

                val rows = activities.chunked(2)
                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { (emoji, label) ->
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = phaseColor.copy(alpha = 0.08f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(emoji, fontSize = 20.sp)
                                    Text(label, fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onBackground)
                                }
                            }
                        }
                        if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
