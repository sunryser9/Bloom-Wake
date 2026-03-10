package com.bloomwake.ui.screens

// ═══════════════════════════════════════════════════════════════════════════
//  CYCLE CALENDAR — Visual 28-day phase calendar
//  Shows past + future phases at a glance. Plan your MONTH around your cycle.
//  Flo has this but makes it pay-to-see. BloomWake: 100% free forever.
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
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CycleCalendarScreen(viewModel: BloomWakeViewModel, onBack: () -> Unit) {
    val profile by viewModel.userProfile.collectAsState()
    val cycleLength = profile?.avgCycleLength ?: 28
    val lastPeriod = profile?.lastPeriodDate ?: LocalDate.now().minusDays(7)

    val cycleState = remember(profile) {
        profile?.lastPeriodDate?.let {
            CycleCalculator.getCurrentPhase(it, cycleLength)
        }
    }
    val currentPhase = cycleState?.phase ?: CyclePhase.FOLLICULAR
    val todayDay = cycleState?.dayOfCycle ?: 1

    var selectedDay by remember { mutableStateOf(todayDay) }

    // Build 2 full cycles (56 days) of phase data
    val calendarDays = remember(lastPeriod, cycleLength) {
        (0 until 56).map { offset ->
            val date = lastPeriod.plusDays(offset.toLong())
            val dayOfCycle = ((offset) % cycleLength) + 1
            val phase = CycleCalculator.phaseForDay(dayOfCycle)
            Triple(date, dayOfCycle, phase)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Your Cycle Calendar", fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground)
        Text("Tap any day to see what to expect",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(0.55f))

        Spacer(Modifier.height(16.dp))

        // Phase legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CyclePhase.values().forEach { p ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(p.color))
                    )
                    Text(p.emoji + " " + p.displayName.take(3),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Day of week headers
        val dayNames = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
        Row(modifier = Modifier.fillMaxWidth()) {
            dayNames.forEach { d ->
                Text(d, modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.4f),
                    fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Calendar grid - current cycle
        Text("This cycle", fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(0.6f))
        Spacer(Modifier.height(6.dp))

        val startDayOfWeek = lastPeriod.dayOfWeek.value % 7 // 0=Sun

        // First cycle grid
        CalendarGrid(
            days = calendarDays.take(cycleLength),
            startOffset = startDayOfWeek,
            todayDay = todayDay,
            selectedDay = selectedDay,
            onDaySelected = { selectedDay = it }
        )

        Spacer(Modifier.height(20.dp))

        // Next cycle preview
        Text("Next cycle (predicted)", fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(0.6f))
        Spacer(Modifier.height(6.dp))

        CalendarGrid(
            days = calendarDays.drop(cycleLength).take(cycleLength),
            startOffset = (startDayOfWeek + cycleLength) % 7,
            todayDay = -1, // no today in future cycle
            selectedDay = -1,
            onDaySelected = {}
        )

        Spacer(Modifier.height(20.dp))

        // Selected day detail
        val selected = calendarDays.firstOrNull { it.second == selectedDay }
        if (selected != null) {
            val (date, day, phase) = selected
            val pColor = Color(phase.color)
            val isToday = day == todayDay && date <= LocalDate.now()

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = pColor.copy(0.12f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(phase.emoji, fontSize = 28.sp)
                            Column {
                                Text(phase.displayName, fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground)
                                Text(
                                    "Day $day" + if (isToday) " · TODAY" else " · ${date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${date.dayOfMonth}",
                                    fontSize = 12.sp, color = pColor,
                                    fontWeight = FontWeight.SemiBold)
                            }
                        }
                        if (isToday) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(pColor)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("NOW", fontSize = 10.sp,
                                    color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(phase.description, fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.75f),
                        lineHeight = 18.sp)

                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PhaseStatChip("⚡ ${phase.energyScore}/10", pColor)
                        PhaseStatChip("😊 ${phase.moodScore}/10", pColor)
                        PhaseStatChip("🎯 ${phase.focusScore}/10", pColor)
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(pColor.copy(0.15f))
                                .padding(8.dp)
                        ) {
                            Text("✨ ${phase.superpower}", fontSize = 11.sp,
                                color = pColor, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(60.dp))
    }
}

@Composable
private fun CalendarGrid(
    days: List<Triple<LocalDate, Int, CyclePhase>>,
    startOffset: Int,
    todayDay: Int,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    val cells = mutableListOf<Triple<LocalDate, Int, CyclePhase>?>()
    repeat(startOffset) { cells.add(null) }
    cells.addAll(days)
    while (cells.size % 7 != 0) cells.add(null)

    cells.chunked(7).forEach { week ->
        Row(modifier = Modifier.fillMaxWidth()) {
            week.forEach { cell ->
                if (cell == null) {
                    Box(modifier = Modifier.weight(1f).padding(2.dp))
                } else {
                    val (_, day, phase) = cell
                    val pColor = Color(phase.color)
                    val isToday = day == todayDay
                    val isSelected = day == selectedDay

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isToday -> pColor
                                    isSelected -> pColor.copy(0.3f)
                                    else -> pColor.copy(0.15f)
                                }
                            )
                            .clickable { onDaySelected(day) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$day",
                            fontSize = 11.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday) Color.White
                            else MaterialTheme.colorScheme.onBackground.copy(0.8f)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(3.dp))
    }
}

@Composable
private fun PhaseStatChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
    }
}
