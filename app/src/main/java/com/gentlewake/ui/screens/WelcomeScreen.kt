package com.bloomwake.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        1f, 1.08f,
        infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse),
        "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(0.4f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp).verticalScroll(rememberScrollState())
        ) {
            Text("🌸", fontSize = 80.sp, modifier = Modifier.scale(scale))

            Text(
                "BloomWake",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Cycle-Smart Mornings",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Wake up with rituals designed for exactly\nwhere you are in your cycle — every day.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(0.75f),
                lineHeight = 24.sp
            )

            // Feature list
            listOf(
                Triple("🌙", "Phase-aware alarms", "Right ritual, right day"),
                Triple("🔬", "Science-backed missions", "Real hormonal science"),
                Triple("📸", "Viral phase cards", "Share your cycle era"),
                Triple("🔒", "100% on-device", "Your data, always private")
            ).forEach { (emoji, title, sub) ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(emoji, fontSize = 24.sp)
                        Column {
                            Text(title, fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium)
                            Text(sub, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.55f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onGetStarted,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Begin My Journey →", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
            }

            Text(
                "🔒 All your data stays on your device. Always.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(0.4f)
            )

            Text(
                "⚠️ For wellness only. Not medical advice.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(0.3f)
            )
        }
    }
}
