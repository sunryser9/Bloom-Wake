package com.bloomwake.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.bloomwake.utils.CyclePhase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import java.util.UUID

class BloomVoice(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    var onDone: (() -> Unit)? = null

    init {
        try {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.US)
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.setLanguage(Locale.getDefault())
                    }
                    tts?.setSpeechRate(0.88f)
                    tts?.setPitch(1.05f)
                    isReady = true
                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(id: String?) { _isSpeaking.value = true }
                        override fun onDone(id: String?) {
                            _isSpeaking.value = false
                            onDone?.invoke()
                        }
                        override fun onError(id: String?) { _isSpeaking.value = false }
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun speak(text: String, onFinish: (() -> Unit)? = null) {
        if (!isReady || tts == null) {
            onFinish?.invoke()
            return
        }
        try {
            onDone = onFinish
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
        } catch (e: Exception) {
            e.printStackTrace()
            _isSpeaking.value = false
            onFinish?.invoke()
        }
    }

    fun stop() {
        try { tts?.stop() } catch (e: Exception) { e.printStackTrace() }
        _isSpeaking.value = false
    }

    fun destroy() {
        try { tts?.stop(); tts?.shutdown() } catch (e: Exception) { e.printStackTrace() }
        tts = null
    }

    fun wakeUpMessage(phase: CyclePhase, name: String, dayOfCycle: Int): String {
        val greeting = if (name.isNotBlank()) "Good morning, $name!" else "Good morning!"
        return when (phase) {
            CyclePhase.MENSTRUAL ->
                "$greeting Today is day $dayOfCycle of your cycle. " +
                "Your body is doing sacred work right now. You don't need to perform today. " +
                "Rest is productive. Drink something warm, move gently, and honour what your body needs. " +
                "You are enough exactly as you are."
            CyclePhase.FOLLICULAR ->
                "$greeting You're in your follicular phase, day $dayOfCycle. " +
                "Your estrogen is rising and your brain is building new connections faster than any other time. " +
                "This is your window. Start the thing. Say yes to the opportunity. " +
                "Your energy will only build from here. Go be brilliant."
            CyclePhase.OVULATORY ->
                "$greeting It's your ovulatory phase, day $dayOfCycle. " +
                "This is your peak. Your voice carries further today. People are drawn to your energy. " +
                "Have the important conversation. Walk into the room like you own it. " +
                "You are magnetic right now. Use it."
            CyclePhase.LUTEAL ->
                "$greeting You're in your luteal phase, day $dayOfCycle. " +
                "Your body is asking for gentleness today. That's not weakness — that's wisdom. " +
                "Prioritise what truly matters, let the rest wait. " +
                "Eat well, sleep early, move softly. You are allowed to slow down."
        }
    }

    fun eveningMessage(phase: CyclePhase, missionsCompleted: Int): String = when (phase) {
        CyclePhase.MENSTRUAL ->
            "Good evening. You showed up today, and that's everything. " +
            "Rest deeply tonight. Your body regenerates while you sleep."
        CyclePhase.FOLLICULAR ->
            "You had a strong day. Your follicular energy is building. " +
            "Sleep well tonight — tomorrow you'll wake up even sharper."
        CyclePhase.OVULATORY ->
            "What a powerful day. Your ovulatory energy is magnetic. " +
            "Rest now so you can show up fully again tomorrow."
        CyclePhase.LUTEAL ->
            "You did well today. Be gentle with yourself tonight. " +
            "A warm bath, magnesium, early sleep — your body will thank you."
    }

    fun supplementReminder(phase: CyclePhase): String = when (phase) {
        CyclePhase.MENSTRUAL ->
            "Reminder: Iron-rich foods and vitamin C help replenish what your body is releasing. " +
            "Think spinach, lentils, or a supplement today."
        CyclePhase.FOLLICULAR ->
            "Reminder: B vitamins and zinc support your rising estrogen. " +
            "Eggs, nuts, and seeds are your friends this week."
        CyclePhase.OVULATORY ->
            "Reminder: Antioxidants protect your peak energy. " +
            "Berries, leafy greens, and plenty of water today."
        CyclePhase.LUTEAL ->
            "Reminder: Magnesium reduces PMS symptoms significantly. " +
            "Dark chocolate, pumpkin seeds, or a magnesium supplement tonight."
    }

    fun phaseTransitionAlert(nextPhase: CyclePhase): String = when (nextPhase) {
        CyclePhase.FOLLICULAR ->
            "Heads up — your follicular phase begins tomorrow. Energy is about to rise. Plan something exciting."
        CyclePhase.OVULATORY ->
            "Tomorrow is your ovulatory peak. Schedule your most important conversations and meetings now."
        CyclePhase.LUTEAL ->
            "Luteal phase begins tomorrow. Add some gentle movement and nourishing food to your schedule."
        CyclePhase.MENSTRUAL ->
            "Your period may begin soon. Prepare your comfort rituals. You've got this."
    }
}
