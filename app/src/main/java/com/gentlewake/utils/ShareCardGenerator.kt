package com.bloomwake.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import kotlin.math.*

/**
 * Generates a beautiful 1080x1920 shareable phase card image — the viral engine.
 * Pure Android Canvas API, zero dependencies.
 */
object ShareCardGenerator {

    fun generateAndShare(
        context: Context,
        phase: CyclePhase,
        dayOfCycle: Int,
        streak: Int,
        insight: String
    ) {
        val bitmap = generateCard(phase, dayOfCycle, streak, insight)
        val uri = saveBitmapToCache(context, bitmap)
        shareImage(context, uri, CycleCalculator.getShareCaption(phase, streak, dayOfCycle))
    }

    fun generateCard(
        phase: CyclePhase,
        dayOfCycle: Int,
        streak: Int,
        insight: String
    ): Bitmap {
        val W = 1080
        val H = 1920
        val bitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // ── Background gradient ──────────────────────────────────────────────
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val (top, bottom) = phaseGradientColors(phase)
        bgPaint.shader = LinearGradient(0f, 0f, 0f, H.toFloat(), top, bottom, Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, W.toFloat(), H.toFloat(), bgPaint)

        // ── Decorative circles (depth) ───────────────────────────────────────
        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 18
        }
        canvas.drawCircle(W * 0.85f, H * 0.12f, 320f, circlePaint)
        canvas.drawCircle(W * 0.1f, H * 0.88f, 250f, circlePaint)
        circlePaint.alpha = 10
        canvas.drawCircle(W * 0.5f, H * 0.5f, 480f, circlePaint)

        // ── App name top ─────────────────────────────────────────────────────
        val appNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 180
            textSize = 48f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            letterSpacing = 0.15f
        }
        canvas.drawText("🌸  BLOOMWAKE", W / 2f, 120f, appNamePaint)

        // ── Giant emoji ──────────────────────────────────────────────────────
        val emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 280f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(phase.emoji, W / 2f, 560f, emojiPaint)

        // ── Phase name ───────────────────────────────────────────────────────
        val phasePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 120f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)
            setShadowLayer(12f, 0f, 4f, Color.argb(80, 0, 0, 0))
        }
        canvas.drawText(phase.displayName, W / 2f, 720f, phasePaint)

        // ── Tagline pill ─────────────────────────────────────────────────────
        val pillRect = RectF(240f, 760f, 840f, 840f)
        val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 45
        }
        canvas.drawRoundRect(pillRect, 40f, 40f, pillPaint)
        val taglinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 52f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            letterSpacing = 0.08f
        }
        canvas.drawText(phase.tagline.uppercase(), W / 2f, 818f, taglinePaint)

        // ── Divider ──────────────────────────────────────────────────────────
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; alpha = 60 }
        canvas.drawLine(120f, 880f, 960f, 880f, dividerPaint)

        // ── Day of cycle ─────────────────────────────────────────────────────
        val dayLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; alpha = 160; textSize = 44f; textAlign = Paint.Align.CENTER
        }
        canvas.drawText("CYCLE DAY", W / 2f, 950f, dayLabelPaint)
        val dayNumPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; textSize = 180f; textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("$dayOfCycle", W / 2f, 1100f, dayNumPaint)

        // ── Insight quote ────────────────────────────────────────────────────
        val quotePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; alpha = 220; textSize = 52f; textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        }
        drawWrappedText(canvas, "\"$insight\"", W / 2f, 1240f, W - 160f, quotePaint, 72f)

        // ── Superpower badge ─────────────────────────────────────────────────
        val badgeRect = RectF(80f, 1540f, W - 80f, 1640f)
        val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; alpha = 25 }
        canvas.drawRoundRect(badgeRect, 30f, 30f, badgePaint)
        val badgeLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; alpha = 140; textSize = 36f; textAlign = Paint.Align.CENTER
        }
        canvas.drawText("SUPERPOWER", W / 2f, 1580f, badgeLabelPaint)
        val badgeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; textSize = 52f; textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(phase.superpower, W / 2f, 1625f, badgeTextPaint)

        // ── Streak counter ───────────────────────────────────────────────────
        val streakPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; textSize = 56f; textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("🔥 $streak Day Streak", W / 2f, 1730f, streakPaint)

        // ── Bottom CTA ───────────────────────────────────────────────────────
        val ctaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; alpha = 130; textSize = 40f; textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Download BloomWake • Cycle-Smart Mornings", W / 2f, 1840f, ctaPaint)

        return bitmap
    }

    private fun phaseGradientColors(phase: CyclePhase): Pair<Int, Int> = when (phase) {
        CyclePhase.MENSTRUAL -> Pair(Color.parseColor("#8B1A1A"), Color.parseColor("#C0414A"))
        CyclePhase.FOLLICULAR -> Pair(Color.parseColor("#1B5E20"), Color.parseColor("#388E3C"))
        CyclePhase.OVULATORY -> Pair(Color.parseColor("#E65100"), Color.parseColor("#F9A825"))
        CyclePhase.LUTEAL -> Pair(Color.parseColor("#4A148C"), Color.parseColor("#7B1FA2"))
    }

    private fun drawWrappedText(
        canvas: Canvas, text: String, x: Float, y: Float,
        maxWidth: Float, paint: Paint, lineHeight: Float
    ) {
        val words = text.split(" ")
        var line = ""
        var currentY = y
        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(testLine) > maxWidth) {
                canvas.drawText(line, x, currentY, paint)
                line = word
                currentY += lineHeight
            } else {
                line = testLine
            }
        }
        if (line.isNotEmpty()) canvas.drawText(line, x, currentY, paint)
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "bloomwake_share_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 95, it) }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun shareImage(context: Context, imageUri: Uri, caption: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, caption)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share your ${caption.take(20)}…"))
    }
}
