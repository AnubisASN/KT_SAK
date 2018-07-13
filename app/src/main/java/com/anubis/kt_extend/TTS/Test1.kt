package com.anubis.kt_extend.TTS

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.anubis.kt_extend.R
import com.anubis.kt_extend.R.id.*
import com.anubis.kt_extend.app
import com.anubis.module_tts.Bean.voiceModel
import com.anubis.module_tts.TTS
import com.anubis.module_tts.TTS.setParams
import kotlinx.android.synthetic.main.activity_main.*

class Test1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test1)
    }
    fun testClick(v: View) {
        when (v.id) {
            R.id.button22-> app().get()!!.mTTS!!.speak("初始化调用")
            R.id.button33 -> app().get()!!.mTTS!!.setParams(voiceModel.EMOTIONAL_MALE).speak("切换发音人调用")
            R.id.button44 -> startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
