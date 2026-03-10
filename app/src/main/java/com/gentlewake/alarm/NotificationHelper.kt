package com.bloomwake.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.bloomwake.BloomWakeApp
import com.bloomwake.MainActivity
import com.bloomwake.R
import com.bloomwake.utils.CycleCalculator
import com.bloomwake.utils.CyclePhase

/**
 * Sends re-engagement notifications:
 * 1. Phase transition alerts ("You're entering Ovulatory tomorrow ✨")
 * 2. Mid-day nudges with phase-specific micro-wisdom
 * 3. Streak protection ("Don't break your 7-day streak!")
 */
object NotificationHelper {

    fun sendPhaseTransitionNotification(context: Context, nextPhase: CyclePhase) {
        val (title, body) = when (nextPhase) {
            CyclePhase.FOLLICULAR -> Pair(
                "🌱 Your energy is rising",
                "Follicular phase begins tomorrow. Perfect time to plan something bold."
            )
            CyclePhase.OVULATORY -> Pair(
                "✨ Peak season incoming",
                "Ovulatory phase starts tomorrow. You'll feel magnetic — plan your most important moments."
            )
            CyclePhase.LUTEAL -> Pair(
                "🍂 Time to slow down soon",
                "Luteal phase begins tomorrow. Schedule gentleness. Cancel what you can."
            )
            CyclePhase.MENSTRUAL -> Pair(
                "🌙 Rest season approaching",
                "Your bleed may begin soon. Prepare your warmth rituals and be kind to yourself."
            )
        }
        sendNotification(context, title, body, NOTIF_PHASE_TRANSITION)
    }

    fun sendMidDayNudge(context: Context, phase: CyclePhase, dayOfCycle: Int) {
        val insight = CycleCalculator.getDailyInsight(phase, dayOfCycle)
        sendNotification(
            context,
            "${phase.emoji} ${insight.headline}",
            insight.action,
            NOTIF_MIDDAY
        )
    }

    fun sendStreakProtection(context: Context, streak: Int) {
        if (streak < 3) return
        sendNotification(
            context,
            "🔥 Don't break your $streak-day streak!",
            "Complete today's morning mission to keep your momentum.",
            NOTIF_STREAK
        )
    }

    private fun sendNotification(context: Context, title: String, body: String, id: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, BloomWakeApp.CHANNEL_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id, notification)
    }

    const val NOTIF_PHASE_TRANSITION = 101
    const val NOTIF_MIDDAY = 102
    const val NOTIF_STREAK = 103
}
