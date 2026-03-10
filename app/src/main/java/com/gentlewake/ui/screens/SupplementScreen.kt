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
import androidx.compose.ui.unit.*
import com.bloomwake.ui.viewmodel.BloomWakeViewModel
import com.bloomwake.utils.CycleCalculator
import com.bloomwake.utils.CyclePhase

data class Supplement(
    val emoji: String,
    val name: String,
    val reason: String,
    val timing: String,
    val foods: List<String>
)

fun supplementsForPhase(phase: CyclePhase): List<Supplement> = when (phase) {
    CyclePhase.MENSTRUAL -> listOf(
        Supplement("🩸", "Iron", "Replenishes what your period releases", "Morning with Vitamin C", listOf("Spinach", "Lentils", "Red meat", "Pumpkin seeds")),
        Supplement("💊", "Vitamin C", "Boosts iron absorption significantly", "With meals", listOf("Oranges", "Bell peppers", "Strawberries", "Kiwi")),
        Supplement("🌡️", "Omega-3", "Reduces menstrual cramping", "With food", listOf("Salmon", "Walnuts", "Flaxseed", "Chia seeds")),
        Supplement("😴", "Magnesium", "Reduces cramps and improves sleep", "Before bed", listOf("Dark chocolate", "Avocado", "Almonds", "Leafy greens"))
    )
    CyclePhase.FOLLICULAR -> listOf(
        Supplement("🧠", "B Vitamins", "Supports rising estrogen & brain function", "Morning", listOf("Eggs", "Chicken", "Nutritional yeast", "Sunflower seeds")),
        Supplement("⚡", "Zinc", "Boosts energy and immune function", "With meals", listOf("Pumpkin seeds", "Cashews", "Chickpeas", "Oysters")),
        Supplement("🌿", "Probiotics", "Gut health supports hormonal balance", "Morning fasting", listOf("Yogurt", "Kefir", "Kimchi", "Kombucha")),
        Supplement("✨", "Vitamin D", "Lifts mood and supports hormones", "Morning with fat", listOf("Salmon", "Egg yolks", "Fortified milk", "Sunshine"))
    )
    CyclePhase.OVULATORY -> listOf(
        Supplement("🔥", "Antioxidants", "Protect peak energy from oxidative stress", "With meals", listOf("Blueberries", "Pomegranate", "Green tea", "Dark chocolate")),
        Supplement("💪", "Vitamin E", "Supports ovulation and hormonal peak", "With food", listOf("Almonds", "Sunflower seeds", "Avocado", "Olive oil")),
        Supplement("🫀", "CoQ10", "Cellular energy for your peak phase", "Morning", listOf("Beef", "Sardines", "Organ meats", "Broccoli")),
        Supplement("💧", "Electrolytes", "Peak output needs peak hydration", "Throughout day", listOf("Coconut water", "Bananas", "Celery", "Watermelon"))
    )
    CyclePhase.LUTEAL -> listOf(
        Supplement("✨", "Magnesium", "Reduces PMS, bloating & mood swings", "Before bed", listOf("Dark chocolate 70%+", "Pumpkin seeds", "Black beans", "Spinach")),
        Supplement("😌", "Vitamin B6", "Reduces PMS symptoms by up to 50%", "With meals", listOf("Banana", "Chickpeas", "Tuna", "Potato")),
        Supplement("🌙", "Chasteberry", "Balances progesterone naturally", "Morning", listOf("Supplement only", "—", "—", "—")),
        Supplement("☀️", "Vitamin D3", "Reduces mood dips in luteal phase", "Morning with fat", listOf("Salmon", "Egg yolks", "Mushrooms", "Sunshine"))
    )
}

@Composable
fun SupplementScreen(
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
    val supplements = supplementsForPhase(phase)
    var checkedItems by remember { mutableStateOf(setOf<Int>()) }

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
                            Text("Supplements", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground)
                            Text("${phase.emoji} Optimised for your ${phase.displayName} phase",
                                fontSize = 14.sp, color = phaseColor)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = phaseColor.copy(alpha = 0.15f))
                    ) {
                        Text(
                            phase.superpower,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                Text("${supplements.size} supplements for this phase",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))

                supplements.forEachIndexed { index, supp ->
                    val isChecked = index in checkedItems
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                checkedItems = if (isChecked)
                                    checkedItems - index else checkedItems + index
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChecked) phaseColor.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, if (isChecked) phaseColor else phaseColor.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(supp.emoji, fontSize = 28.sp)
                                    Column {
                                        Text(supp.name, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground)
                                        Text(supp.timing, fontSize = 12.sp, color = phaseColor)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isChecked) phaseColor else phaseColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isChecked)
                                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(supp.reason, fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                            // Food sources
                            Text("Food sources:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                                color = phaseColor)
                            // Wrap chips across multiple rows to prevent word-splitting
                            val rowFoods = supp.foods.take(4).filter { it != "—" }
                            val firstRow = rowFoods.take(2)
                            val secondRow = rowFoods.drop(2)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                firstRow.forEach { food ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(phaseColor.copy(alpha = 0.1f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(food, fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            maxLines = 1)
                                    }
                                }
                            }
                            if (secondRow.isNotEmpty()) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    secondRow.forEach { food ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(phaseColor.copy(alpha = 0.1f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(food, fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Progress
                if (checkedItems.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = phaseColor.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("✅", fontSize = 24.sp)
                            Text("${checkedItems.size}/${supplements.size} taken today — your body thanks you! 🌸",
                                fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
