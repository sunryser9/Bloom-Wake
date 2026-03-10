package com.bloomwake.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloomwake.data.UserProfile
import com.bloomwake.ui.viewmodel.BloomWakeViewModel

@Composable
fun GoalsScreen(
    viewModel: BloomWakeViewModel,
    onFinish: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()

    var lessStress by remember { mutableStateOf(false) }
    var moreEnergy by remember { mutableStateOf(false) }
    var betterSleep by remember { mutableStateOf(false) }
    var hormoneBalance by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your Goals 🌟",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "What matters most to you right now? Select all that apply.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            GoalCheckCard(
                emoji = "🧘",
                title = "Less stress",
                subtitle = "Calm morning rituals to ease into the day",
                checked = lessStress,
                onToggle = { lessStress = !lessStress }
            )

            GoalCheckCard(
                emoji = "⚡",
                title = "More energy",
                subtitle = "Cycle-aligned wake-up missions to boost vitality",
                checked = moreEnergy,
                onToggle = { moreEnergy = !moreEnergy }
            )

            GoalCheckCard(
                emoji = "🌙",
                title = "Better sleep",
                subtitle = "Evening wind-down suggestions tailored to your phase",
                checked = betterSleep,
                onToggle = { betterSleep = !betterSleep }
            )

            GoalCheckCard(
                emoji = "🔮",
                title = "Hormone balance",
                subtitle = "Lifestyle nudges that support your natural rhythm",
                checked = hormoneBalance,
                onToggle = { hormoneBalance = !hormoneBalance }
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── PHASE EDUCATION ──────────────────────────────────────────
            Text(
                "Your cycle has 4 phases — each changes everything:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            val phases = listOf(
                Triple("🌙", "Menstrual", "Rest & restore. Low energy is normal and healthy."),
                Triple("🌱", "Follicular", "Energy rises. Best time to start new things & be creative."),
                Triple("✨", "Ovulatory", "Peak power. Magnetic, social, confident — use it."),
                Triple("🍂", "Luteal", "Slow down. Finish tasks, nourish yourself, prep to rest.")
            )

            phases.forEach { (emoji, name, desc) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(emoji, style = MaterialTheme.typography.titleMedium)
                    Column {
                        Text(
                            name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val currentProfile = profile
                    if (currentProfile != null) {
                        viewModel.saveOnboarding(
                            lastPeriodDate = currentProfile.lastPeriodDate ?: java.time.LocalDate.now().minusDays(7),
                            avgCycleLength = currentProfile.avgCycleLength,
                            goalLessStress = lessStress,
                            goalMoreEnergy = moreEnergy
                        )
                    }
                    onFinish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Start My Journey ✨",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun GoalCheckCard(
    emoji: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    val borderColor = if (checked) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    val bgColor = if (checked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    else MaterialTheme.colorScheme.surface

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .border(
                width = if (checked) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = emoji, style = MaterialTheme.typography.headlineSmall)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            if (checked) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(28.dp)
                ) {}
            }
        }
    }
}
