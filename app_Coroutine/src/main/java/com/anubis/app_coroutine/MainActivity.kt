package com.anubis.app_coroutine

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.anubis.kt_extends.eLog
import com.anubis.module_eventbus.eEventBus
import com.anubis.module_eventbus.observe.eObserveEvent
import com.anubis.module_eventbus.post.ePostEvent
import com.anubis.module_eventbus.post.ePostSpan
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.anko.onClick
import java.util.*

class MainActivity : AppCompatActivity() {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        eEventBus.eInit(application)
        startService(Intent(this@MainActivity, MyService::class.java))
        val tts = TextToSpeech(this, {
            it.eLog("TextToSpeech Listener")
        }, "com.iflytek.speechcloud")
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
        button2.onClick {
            ePostEvent("service")
        }
        button3.onClick {
ePostSpan("123456654")
        }
        button4.onClick {
            ePostEvent("跳转成功")
            startActivity(Intent(this,MainActivity1::class.java))
        }
        EvenTest()
    }

    fun EvenTest() {
        eObserveEvent<String> {
            it.eLog("observeEvent1")
        }
        eObserveEvent<String>(CoroutineScope(Dispatchers.Main)) {
            it.eLog("observeEvent2")
        }
        eObserveEvent<String>(CoroutineScope(Dispatchers.IO)) {
            it.eLog("observeEvent3")
        }
    }

}
