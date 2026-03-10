package com.bloomwake.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bloomwake.data.UserPreferencesRepository
import com.bloomwake.ui.screens.*
import com.bloomwake.ui.viewmodel.BloomWakeViewModel
import com.bloomwake.ui.viewmodel.BloomWakeViewModelFactory

sealed class Screen(val route: String) {
    object InstantDemo   : Screen("instant_demo")
    object Welcome       : Screen("welcome")
    object CycleSetup    : Screen("cycle_setup")
    object Goals         : Screen("goals")
    object Home          : Screen("home")
    object AlarmSet      : Screen("alarm_set")
    object SymptomLog    : Screen("symptom_log")
    object Supplements   : Screen("supplements")
    object PhaseGuide    : Screen("phase_guide")
    object VoiceWake     : Screen("voice_wake")
    object Today         : Screen("today")           // 🆕 Unicorn feature
    object MoodTracker   : Screen("mood_tracker")    // 🆕 Mood × Phase correlation
    object CycleCalendar : Screen("cycle_calendar")  // 🆕 Visual phase planning
    object AlarmRinging  : Screen("alarm_ringing/{phase}") {
        fun createRoute(phase: String) = "alarm_ringing/$phase"
    }
}

@Composable
fun BloomWakeNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val repository = UserPreferencesRepository(context)
    val viewModel: BloomWakeViewModel = viewModel(factory = BloomWakeViewModelFactory(repository))
    val profile by viewModel.userProfile.collectAsState()

    val startDestination = when {
        profile == null -> Screen.InstantDemo.route
        profile?.onboardingComplete == true -> Screen.Home.route
        profile?.hasSeenInstantDemo == false -> Screen.InstantDemo.route
        else -> Screen.Welcome.route
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.InstantDemo.route) {
            InstantDemoScreen(onDone = {
                viewModel.markDemoSeen()
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(Screen.InstantDemo.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(onGetStarted = { navController.navigate(Screen.CycleSetup.route) })
        }

        composable(Screen.CycleSetup.route) {
            CycleSetupScreen(viewModel = viewModel, onNext = { navController.navigate(Screen.Goals.route) })
        }

        composable(Screen.Goals.route) {
            GoalsScreen(viewModel = viewModel, onFinish = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onSetAlarm    = { navController.navigate(Screen.AlarmSet.route) },
                onAlarmRing   = { phase -> navController.navigate(Screen.AlarmRinging.createRoute(phase)) },
                onSymptomLog  = { navController.navigate(Screen.SymptomLog.route) },
                onSupplements = { navController.navigate(Screen.Supplements.route) },
                onPhaseGuide  = { navController.navigate(Screen.PhaseGuide.route) },
                onVoiceWake   = { navController.navigate(Screen.VoiceWake.route) },
                onToday       = { navController.navigate(Screen.Today.route) },
                onMoodTracker = { navController.navigate(Screen.MoodTracker.route) },
                onCalendar    = { navController.navigate(Screen.CycleCalendar.route) }
            )
        }

        composable(Screen.AlarmSet.route) {
            AlarmSetScreen(viewModel = viewModel, onSaved = { navController.popBackStack() })
        }

        composable(Screen.SymptomLog.route) {
            SymptomLogScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable(Screen.Supplements.route) {
            SupplementScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable(Screen.PhaseGuide.route) {
            PhaseGuideScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable(Screen.VoiceWake.route) {
            VoiceWakeScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable(Screen.Today.route) {
            TodayScreen(viewModel = viewModel)
        }

        composable(Screen.MoodTracker.route) {
            MoodTrackerScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable(Screen.CycleCalendar.route) {
            CycleCalendarScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable(Screen.AlarmRinging.route) { backStack ->
            val phase = backStack.arguments?.getString("phase") ?: "FOLLICULAR"
            AlarmRingingScreen(
                phase = phase, viewModel = viewModel,
                onDone = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
