package com.bloomwake.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bloomwake_prefs")

data class UserProfile(
    val onboardingComplete: Boolean = false,
    val lastPeriodDate: LocalDate? = null,
    val avgCycleLength: Int = 28,
    val goalLessStress: Boolean = false,
    val goalMoreEnergy: Boolean = false,
    val userName: String = "",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalMissionsCompleted: Int = 0,
    val lastCompletionDate: LocalDate? = null,
    val weeklyMissionCount: Int = 0,
    val weeklyMissionResetDate: LocalDate? = null,
    val alarmHour: Int = 7,
    val alarmMinute: Int = 0,
    val alarmEnabled: Boolean = false,
    val hasSeenInstantDemo: Boolean = false,
    val lastNotifDate: LocalDate? = null
)

class UserPreferencesRepository(private val context: Context) {
    companion object {
        private val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        private val LAST_PERIOD_DATE = stringPreferencesKey("last_period_date")
        private val AVG_CYCLE_LENGTH = intPreferencesKey("avg_cycle_length")
        private val GOAL_LESS_STRESS = booleanPreferencesKey("goal_less_stress")
        private val GOAL_MORE_ENERGY = booleanPreferencesKey("goal_more_energy")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val CURRENT_STREAK = intPreferencesKey("current_streak")
        private val LONGEST_STREAK = intPreferencesKey("longest_streak")
        private val TOTAL_MISSIONS = intPreferencesKey("total_missions")
        private val LAST_COMPLETION_DATE = stringPreferencesKey("last_completion_date")
        private val WEEKLY_MISSION_COUNT = intPreferencesKey("weekly_mission_count")
        private val WEEKLY_MISSION_RESET_DATE = stringPreferencesKey("weekly_mission_reset_date")
        private val ALARM_HOUR = intPreferencesKey("alarm_hour")
        private val ALARM_MINUTE = intPreferencesKey("alarm_minute")
        private val ALARM_ENABLED = booleanPreferencesKey("alarm_enabled")
        private val HAS_SEEN_DEMO = booleanPreferencesKey("has_seen_demo")
        private val LAST_NOTIF_DATE = stringPreferencesKey("last_notif_date")
    }

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            onboardingComplete = prefs[ONBOARDING_COMPLETE] ?: false,
            lastPeriodDate = prefs[LAST_PERIOD_DATE]?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            avgCycleLength = prefs[AVG_CYCLE_LENGTH] ?: 28,
            goalLessStress = prefs[GOAL_LESS_STRESS] ?: false,
            goalMoreEnergy = prefs[GOAL_MORE_ENERGY] ?: false,
            userName = prefs[USER_NAME] ?: "",
            currentStreak = prefs[CURRENT_STREAK] ?: 0,
            longestStreak = prefs[LONGEST_STREAK] ?: 0,
            totalMissionsCompleted = prefs[TOTAL_MISSIONS] ?: 0,
            lastCompletionDate = prefs[LAST_COMPLETION_DATE]?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            weeklyMissionCount = prefs[WEEKLY_MISSION_COUNT] ?: 0,
            weeklyMissionResetDate = prefs[WEEKLY_MISSION_RESET_DATE]?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            alarmHour = prefs[ALARM_HOUR] ?: 7,
            alarmMinute = prefs[ALARM_MINUTE] ?: 0,
            alarmEnabled = prefs[ALARM_ENABLED] ?: false,
            hasSeenInstantDemo = prefs[HAS_SEEN_DEMO] ?: false,
            lastNotifDate = prefs[LAST_NOTIF_DATE]?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        )
    }

    suspend fun saveOnboarding(
        lastPeriodDate: LocalDate, avgCycleLength: Int,
        goalLessStress: Boolean, goalMoreEnergy: Boolean, userName: String = ""
    ) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETE] = true
            prefs[LAST_PERIOD_DATE] = lastPeriodDate.toString()
            prefs[AVG_CYCLE_LENGTH] = avgCycleLength
            prefs[GOAL_LESS_STRESS] = goalLessStress
            prefs[GOAL_MORE_ENERGY] = goalMoreEnergy
            if (userName.isNotBlank()) prefs[USER_NAME] = userName
        }
    }

    suspend fun saveAlarm(hour: Int, minute: Int, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ALARM_HOUR] = hour
            prefs[ALARM_MINUTE] = minute
            prefs[ALARM_ENABLED] = enabled
        }
    }

    suspend fun markDemoSeen() {
        context.dataStore.edit { it[HAS_SEEN_DEMO] = true }
    }

    suspend fun recordMissionCompletion() {
        context.dataStore.edit { prefs ->
            val today = LocalDate.now()
            val lastCompletion = prefs[LAST_COMPLETION_DATE]?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            val newStreak = when {
                lastCompletion == null -> 1
                lastCompletion == today.minusDays(1) -> (prefs[CURRENT_STREAK] ?: 0) + 1
                lastCompletion == today -> prefs[CURRENT_STREAK] ?: 1
                else -> 1
            }
            prefs[CURRENT_STREAK] = newStreak
            val longest = prefs[LONGEST_STREAK] ?: 0
            if (newStreak > longest) prefs[LONGEST_STREAK] = newStreak
            prefs[LAST_COMPLETION_DATE] = today.toString()
            prefs[TOTAL_MISSIONS] = (prefs[TOTAL_MISSIONS] ?: 0) + 1

            val resetDate = prefs[WEEKLY_MISSION_RESET_DATE]?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            val isNewWeek = resetDate == null || today.isAfter(resetDate.plusDays(6))
            if (isNewWeek) {
                prefs[WEEKLY_MISSION_COUNT] = 1
                prefs[WEEKLY_MISSION_RESET_DATE] = today.toString()
            } else {
                prefs[WEEKLY_MISSION_COUNT] = (prefs[WEEKLY_MISSION_COUNT] ?: 0) + 1
            }
        }
    }

    suspend fun checkAndResetStreakIfMissed() {
        context.dataStore.edit { prefs ->
            val lastCompletion = prefs[LAST_COMPLETION_DATE]?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            if (lastCompletion != null && lastCompletion.isBefore(LocalDate.now().minusDays(1))) {
                prefs[CURRENT_STREAK] = 0
            }
        }
    }
}
