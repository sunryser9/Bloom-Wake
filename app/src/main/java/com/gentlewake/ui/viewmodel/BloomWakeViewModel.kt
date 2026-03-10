package com.bloomwake.ui.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import com.bloomwake.data.UserPreferencesRepository
import com.bloomwake.data.UserProfile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class BloomWakeViewModel(private val repository: UserPreferencesRepository) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveOnboarding(lastPeriodDate: LocalDate, avgCycleLength: Int,
                       goalLessStress: Boolean, goalMoreEnergy: Boolean, userName: String = "") {
        viewModelScope.launch {
            repository.saveOnboarding(lastPeriodDate, avgCycleLength, goalLessStress, goalMoreEnergy, userName)
        }
    }

    fun saveAlarm(hour: Int, minute: Int, enabled: Boolean) {
        viewModelScope.launch { repository.saveAlarm(hour, minute, enabled) }
    }

    fun recordCompletion() {
        viewModelScope.launch { repository.recordMissionCompletion() }
    }

    fun checkStreak() {
        viewModelScope.launch { repository.checkAndResetStreakIfMissed() }
    }

    fun markDemoSeen() {
        viewModelScope.launch { repository.markDemoSeen() }
    }
}

class BloomWakeViewModelFactory(private val repository: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BloomWakeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BloomWakeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
