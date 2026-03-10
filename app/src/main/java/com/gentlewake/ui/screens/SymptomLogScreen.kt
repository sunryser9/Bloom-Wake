package com.bloomwake.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.bloomwake.ui.viewmodel.BloomWakeViewModel
import com.bloomwake.utils.CycleCalculator
import com.bloomwake.utils.CyclePhase

data class SymptomEntry(
    val energy: Int = 5,
    val mood: Int = 5,
    val cramps: Int = 0,
    val bloating: Int = 0,
    val headache: Int = 0,
    val sleepQuality: Int = 5,
    val notes: String = ""
)

@Composable
fun SymptomLogScreen(
    viewModel: BloomWakeViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val cycleState = remember(profile) {
        profile?.lastPeriodDate?.let {
            CycleCalculator.getCurrentPhase(it, profile?.avgCycleLength ?: 28)
        }
    }
    val phase = cycleState?.phase ?: CyclePhase.FOLLICULAR
    val phaseColor = Color(phase.color)

    var energy by remember { mutableIntStateOf(5) }
    var mood by remember { mutableIntStateOf(5) }
    var cramps by remember { mutableIntStateOf(0) }
    var bloating by remember { mutableIntStateOf(0) }
    var headache by remember { mutableIntStateOf(0) }
    var sleepQuality by remember { mutableIntStateOf(5) }
    var notes by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

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
                            listOf(phaseColor.copy(alpha = 0.3f), MaterialTheme.colorScheme.background)
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
                            Text("Daily Check-in", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground)
                            Text("${phase.emoji} ${phase.displayName} · Day ${cycleState?.dayOfCycle ?: 1}",
                                fontSize = 14.sp, color = phaseColor)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                // Energy
                SliderCard(
                    title = "⚡ Energy",
                    value = energy,
                    max = 10,
                    color = Color(0xFF4CAF50),
                    labels = listOf("Drained", "Low", "Okay", "Good", "Energised"),
                    onValueChange = { energy = it }
                )

                // Mood
                SliderCard(
                    title = "💜 Mood",
                    value = mood,
                    max = 10,
                    color = phaseColor,
                    labels = listOf("Low", "Meh", "Neutral", "Good", "Amazing"),
                    onValueChange = { mood = it }
                )

                // Sleep
                SliderCard(
                    title = "😴 Sleep Quality",
                    value = sleepQuality,
                    max = 10,
                    color = Color(0xFF5C6BC0),
                    labels = listOf("Terrible", "Poor", "Okay", "Good", "Amazing"),
                    onValueChange = { sleepQuality = it }
                )

                // Symptoms section
                Text("Symptoms today", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SymptomChip("🩸 Cramps", cramps, Color(0xFFE57373), Modifier.weight(1f)) { cramps = it }
                    SymptomChip("🫧 Bloating", bloating, Color(0xFFFF8A65), Modifier.weight(1f)) { bloating = it }
                    SymptomChip("🤕 Headache", headache, Color(0xFFBA68C8), Modifier.weight(1f)) { headache = it }
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("How are you really feeling?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = phaseColor,
                        unfocusedBorderColor = phaseColor.copy(alpha = 0.3f)
                    )
                )

                // Phase tip
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = phaseColor.copy(alpha = 0.1f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Text(phase.emoji, fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Phase Insight", fontSize = 13.sp, color = phaseColor, fontWeight = FontWeight.Bold)
                            Text(phase.description, fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                        }
                    }
                }

                // Save button
                Button(
                    onClick = { saved = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = phaseColor)
                ) {
                    if (saved) {
                        Icon(Icons.Default.Check, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Saved! ✨", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Save Check-in", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SliderCard(
    title: String,
    value: Int,
    max: Int,
    color: Color,
    labels: List<String>,
    onValueChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(color)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("$value/$max", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = 0f..max.toFloat(),
                steps = max - 1,
                colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
            )
            val labelIndex = ((value.toFloat() / max) * (labels.size - 1)).toInt().coerceIn(0, labels.size - 1)
            Text(labels[labelIndex], fontSize = 12.sp, color = color,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun SymptomChip(label: String, value: Int, color: Color, modifier: Modifier = Modifier, onChange: (Int) -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (value > 0) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (value > 0) color else color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp).clickable { onChange(if (value < 3) value + 1 else 0) },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, fontSize = 12.sp, color = if (value > 0) color else
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center)
            Row {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (i < value) color else color.copy(alpha = 0.2f))
                    )
                    if (i < 2) Spacer(Modifier.width(3.dp))
                }
            }
        }
    }
}
