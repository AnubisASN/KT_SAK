package com.anubis.kt_extend.TTS

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View

import com.anubis.kt_extend.R
import com.anubis.kt_extend.R.id.*
import com.anubis.kt_extend.Reflection.Reflection
import com.anubis.kt_extend.app
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eSetKeyDownExit
import com.anubis.module_gorge.eGorgeMessage
import com.anubis.module_tts.Bean.voiceModel
import com.anubis.module_tts.eTTS
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.System.exit
import kotlin.math.log
import android.widget.Toast
import com.anubis.kt_extend.TTS.Test1
import com.anubis.kt_extends.eShowTip


class MainActivity : Activity() {
    var TTS: eTTS? = null
    var mEGorge: eGorgeMessage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TTS = app().get()!!.mTTS
        mEGorge = eGorgeMessage().getInit(this)
    }

    fun mainClick(v: View) {
        when (v.id) {
            button2.id -> TTS!!.speak("初始化调用")
            button3.id -> TTS!!.setParams(voiceModel.CHILDREN).speak("发音人切换调用")
            button4.id -> startActivity(Intent(this, Test1::class.java))
            button5.id -> reflection("com.anubis.kt_extend.Reflection.Reflection")
        }
    }

    fun reflection(packName: String) {
        val cls = Class.forName(packName)
        val clsInstance = cls.newInstance()
        val method = cls.getDeclaredMethod("toastr", Activity::class.java, String::class.java)
        eLog("获得所有方法${cls.declaredMethods}--获得方法传入类型：${method.parameterTypes}")
        method.invoke(clsInstance, this, "00115492654+")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        eLog(keyCode)
        return  eSetKeyDownExit(keyCode)
    }


}
