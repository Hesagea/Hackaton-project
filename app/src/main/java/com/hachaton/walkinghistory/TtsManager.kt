package com.hachaton.walkinghistory

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("pl", "PL"))
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isReady = true
            }
        }
    }

    fun opowiedzHistorie(name: String, description: String, narration: String, scene: String) {
        if (isReady) {
            // Czyścimy teksty z nawiasów, zamieniając je na przecinki dla lepszej pauzy
            val cleanName = name.replace("(", ", ").replace(")", ", ")
            val cleanScene = scene.replace("(", ", ").replace(")", ", ")
        }
    }

    fun speak(text: String) {
        if (isReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun stop() {
        tts?.stop()
    }
}