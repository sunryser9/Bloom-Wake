package com.bloomwake.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloomwake.alarm.AlarmScheduler
import com.bloomwake.ui.viewmodel.BloomWakeViewModel
import com.bloomwake.utils.CycleCalculator
import com.bloomwake.utils.CyclePhase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSetScreen(
    viewModel: BloomWakeViewModel,
    onSaved: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    var hour by remember { mutableIntStateOf(profile?.alarmHour ?: 7) }
    var minute by remember { mutableIntStateOf(profile?.alarmMinute ?: 0) }
    var alarmEnabled by remember { mutableStateOf(profile?.alarmEnabled ?: false) }

    val cycleState = remember(profile) {
        profile?.lastPeriodDate?.let {
            CycleCalculator.getCurrentPhase(it, profile?.avgCycleLength ?: 28)
        }
    }

    // ── Use phase color for full consistency with rest of app ──────────────
    val phase = cycleState?.phase ?: CyclePhase.FOLLICULAR
    val phaseColor = Color(phase.color)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        phaseColor.copy(alpha = 0.08f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Set Your Alarm ⏰",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = phaseColor
            )

            Text(
                text = "Your ${phase.displayName} mission will be waiting for you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            // Time Picker Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = phaseColor.copy(alpha = 0.08f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TimePickerColumn(
                            value = hour,
                            onIncrease = { hour = (hour + 1) % 24 },
                            onDecrease = { hour = (hour - 1 + 24) % 24 },
                            label = "HH",
                            accentColor = phaseColor
                        )

                        Text(
                            ":",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = phaseColor
                        )

                        TimePickerColumn(
                            value = minute,
                            onIncrease = { minute = (minute + 5) % 60 },
                            onDecrease = { minute = (minute - 5 + 60) % 60 },
                            label = "MM",
                            accentColor = phaseColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (hour < 12) "AM" else "PM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = phaseColor
                    )
                }
            }

            // Enable toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = phaseColor.copy(alpha = 0.08f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Enable BloomWake alarm",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "Repeats daily with your current phase",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = alarmEnabled,
                        onCheckedChange = { alarmEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = phaseColor
                        )
                    )
                }
            }

            // Alarm reliability note
            if (alarmEnabled) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = phaseColor.copy(alpha = 0.05f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 18.sp)
                        Text(
                            "For reliable early morning alarms (e.g. 2–5 AM): " +
                            "go to Settings → Apps → BloomWake → Battery → " +
                            "set to 'No restrictions'. This prevents Xiaomi from sleeping the app.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.saveAlarm(hour, minute, alarmEnabled)
                    if (alarmEnabled) {
                        val phaseName = cycleState?.phase?.name ?: "FOLLICULAR"
                        AlarmScheduler.scheduleAlarm(context, hour, minute, phaseName)
                    } else {
                        AlarmScheduler.cancelAlarm(context)
                    }
                    onSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = phaseColor)
            ) {
                Text(
                    "Save Alarm",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun TimePickerColumn(
    value: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    label: String,
    accentColor: Color = Color(0xFF7BC8A4)
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onIncrease) {
            Icon(Icons.Default.Add, contentDescription = "Increase $label", tint = accentColor)
        }
        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        IconButton(onClick = onDecrease) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease $label", tint = accentColor)
        }
    }
}
