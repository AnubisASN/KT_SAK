package com.anubis.SwissArmyKnife.TTS

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View

import com.anubis.SwissArmyKnife.R
import com.anubis.SwissArmyKnife.R.id.*
import com.anubis.SwissArmyKnife.app
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eSetKeyDownExit
import com.anubis.module_gorge.R.id.button3
import com.anubis.module_gorge.eGorgeMessage
import com.anubis.module_tts.Bean.voiceModel
import com.anubis.module_tts.eTTS


class MainActivity : Activity() {
    var TTS: eTTS? = null
    var mEGorge: eGorgeMessage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        app().get()?.mActivityList?.add(this)
        TTS = app().get()!!.mTTS
        mEGorge = eGorgeMessage().getInit(this)
    }

    fun mainClick(v: View) {
        when (v.id) {
            R.id.button2  -> TTS!!.speak("初始化调用")
          R.id.button3-> TTS!!.setParams(voiceModel.CHILDREN).speak("发音人切换调用")
            R.id.button4 -> startActivity(Intent(this, Test1::class.java))
            R.id.button5 -> reflection("com.anubis.SwissArmyKnife.Reflection.Reflection")
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
        return  eSetKeyDownExit(keyCode)
    }


}
