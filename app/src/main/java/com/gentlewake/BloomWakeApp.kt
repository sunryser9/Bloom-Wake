package com.bloomwake

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class BloomWakeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val alarmChannel = NotificationChannel(
            CHANNEL_ALARM,
            "BloomWake Alarm",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Your cycle-aware gentle morning alarm"
            setShowBadge(true)
        }

        val reminderChannel = NotificationChannel(
            CHANNEL_REMINDER,
            "Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily wellness reminders"
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(alarmChannel)
        manager.createNotificationChannel(reminderChannel)
    }

    companion object {
        const val CHANNEL_ALARM = "bloomwake_alarm"
        const val CHANNEL_REMINDER = "bloomwake_reminder"
    }
}
