package com.anubis.kt_extend.TTS

import android.content.Intent
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View

import com.anubis.kt_extend.R
import com.anubis.kt_extend.app
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eSetKeyDownExit
import com.anubis.module_gorge.eGorgeMessage
import java.io.OutputStream

class Test1 : AppCompatActivity() {
    private var PATH2: String? = null //串口名称         RS485开门方式
    private var BAUDRATE: Int? = null            //波特率
    private var outputStream: OutputStream? = null     //发送串口的输出流
    private var mp: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test1)
    }
    fun testClick(v: View) {
        when (v.id) {
            R.id.button22-> app().get()!!.mTTS!!.speak("初始化调用")
            R.id.button33 -> eGorgeMessage().getInit(this).MSG()
            R.id.button44 -> startActivity(Intent(this, MainActivity::class.java))
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        eLog(keyCode)
        return  eSetKeyDownExit(keyCode)
    }
}
