package com.anubis.kt_extend.TTS

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View

import com.anubis.kt_extend.R
import com.anubis.kt_extend.app
import com.anubis.module_gorge.gorgeMessage
import com.anubis.module_tts.Bean.voiceModel
import com.anubis.module_tts.TTS
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    var TTS: TTS? = null
    var gorge: gorgeMessage?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TTS = app().get()!!.mTTS
         gorge = gorgeMessage().getInit(this)
    }

    fun mainClick(v: View) {
        when (v.id) {
            button2.id -> TTS!!.speak("初始化调用")
            button3.id ->   TTS!!.setParams(voiceModel.CHILDREN).speak("发音人切换调用")
            button4.id -> startActivity(Intent(this, Test1::class.java))
        }
    }
}
