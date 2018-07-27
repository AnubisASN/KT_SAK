package com.anubis.SwissArmyKnife

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.speech.tts.Voice
import android.support.v4.app.ActivityCompat
import android.view.KeyEvent
import android.view.View

import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eSetKeyDownExit
import com.anubis.kt_extends.eSetOnRequestPermissionsResult
import com.anubis.kt_extends.eSetPermissions
import com.anubis.module_arcfaceft.eArcFaceFTActivity
import com.anubis.module_gorge.eGorgeMessage
import com.anubis.module_tts.eTTS
import com.alibaba.android.arouter.launcher.ARouter
import com.anubis.module_arcfaceft.face
import com.anubis.module_tts.Bean.*
import com.anubis.module_tts.eTTS.setParams
import com.tencent.bugly.crashreport.CrashReport


class MainActivity : Activity() {
    var TTS: eTTS? = null
    var APP:app?=null
    var mEGorge: eGorgeMessage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        eSetPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA))
        APP=app().get()
        app().get()?.getActivity()!!.add(this)
        TTS = app().get()!!.mTTS
        mEGorge = eGorgeMessage().getInit(this)
    }

    fun mainClick(v: View) {
        when (v.id) {
            R.id.button2 -> eTTS.initTTS(APP!!,APP!!.mHandler!!).setParams().speak("初始化调用")
            R.id.button3 -> eTTS.initTTS(APP!!, APP!!.mHandler!!, TTSMode.MIX, VoiceModel.EMOTIONAL_MALE).setParams( ParamMixMode.MIX_MODE_HIGH_SPEED_NETWORK).speak("发音人切换,网络优先调用")
            R.id.button4 -> {
                ARouter.getInstance().build("/app/Test1").navigation()
                // startActivity(Intent(this, Test1::class.java))
            }

            R.id.button5 -> reflection("com.anubis.SwissArmyKnife.Reflection.Reflection")
            R.id.button6 -> {
                eLog("点击了")
//                FaceFT.startDetector(this)
//                startFace("com.anubis.module_arcfaceft.eArcFaceFTActivity")
                ARouter.getInstance().build("/face/arcFace").navigation()
            }
            R.id.button7 -> {
//                FaceFT.startDetector(this)
//                startFace("com.anubis.module_arcfaceft.eArcFaceFTActivity")
                startActivity(Intent(this, face::class.java))
            }
            R.id.button8 -> {
//                FaceFT.startDetector(this)
                startFace("com.anubis.module_arcfaceft.face")
            }
        }
    }

    fun startFace(packName: String) {
        val cls = Class.forName(packName)
        startActivity(Intent(this, cls))
//        val clsInstance = cls.newInstance()
//        val method = cls.getDeclaredMethod("toastr", Activity::class.java, String::class.java)
//        eLog("获得所有方法${cls.declaredMethods}--获得方法传入类型：${method.parameterTypes}")
//        method.invoke(clsInstance, this, "00115492654+")
    }

    fun startDetector() {
        val it = Intent(this, eArcFaceFTActivity::class.java)
        ActivityCompat.startActivityForResult(this, it, 3, null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        eSetOnRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != 1) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        eLog("size" + app().get()?.getActivity()!!.size)
        return eSetKeyDownExit(keyCode, app().get()?.getActivity(), false, exitHint = "完成退出")
    }


}
