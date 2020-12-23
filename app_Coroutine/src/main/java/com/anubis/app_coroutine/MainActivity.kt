package com.anubis.app_coroutine

import android.annotation.TargetApi
import android.graphics.ColorSpace
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeechService
import com.anubis.kt_extends.eLog
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.onClick
import java.util.*

class MainActivity : AppCompatActivity() {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            it.eLog("TextToSpeech Listener")
        },"com.iflytek.speechcloud")
        tts.language = Locale.CHINESE

        button.onClick {
            tts.speak("123456哈哈哈", TextToSpeech.QUEUE_FLUSH, null)
            try {
                tts.voices?.forEach {
                    it.name.eLog("voice Name")
                }
            } catch (e: Exception) {
            }
            tts.engines?.forEach {
                it.name.eLog("engine Name")
                it.label.eLog("engine label")
            }
        }
    }
}
