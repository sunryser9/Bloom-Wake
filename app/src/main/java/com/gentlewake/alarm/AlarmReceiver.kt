package com.bloomwake.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AlarmScheduler.ACTION_ALARM -> {
                val phase = intent.getStringExtra(AlarmScheduler.EXTRA_PHASE) ?: "FOLLICULAR"
                val serviceIntent = Intent(context, AlarmService::class.java).apply {
                    putExtra(AlarmScheduler.EXTRA_PHASE, phase)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // Re-schedule alarm after reboot if needed
                // This would read from DataStore and re-schedule — simplified here
            }
        }
    }
}
