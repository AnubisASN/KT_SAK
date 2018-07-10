package com.anubis.kt_extend

import android.os.*
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.anubis.module_tts.TTS
import  kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val MSG_TYPE_WUR = 11
    private val MSG_TYPE_ASR = 22
    private val MSG_TYPE_TTS = 33
    private val MSG_STATE_TTS_SPEAK_OVER = 0
    private val MSG_STATE_TTS_SPEAK_START = 1

    private var TTS: TTS? = null
    private var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                handleMSG(msg!!)
            }
        }
        TTS = TTS(this, mHandler as Handler)

    }

    fun TestClick(v: View) {
        when (v.id) {
            btTTS.id -> TTS?.speak("句斤斤计较斤斤计较")
        }
    }

    private fun handleMSG(msg: Message) {
        when (msg.arg1) {
            MSG_TYPE_TTS -> {
                if (msg.obj != null) {
                    when (msg.what) {
                        MSG_STATE_TTS_SPEAK_START -> {
//                        播放开始
                        }
                        MSG_STATE_TTS_SPEAK_OVER -> {
//                        播放结束
                        }

                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        TTS?.ttsDestroy()
    }

}
