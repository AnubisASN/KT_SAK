package com.anubis.SwissArmyKnife.TTS

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.anubis.SwissArmyKnife.MainActivity
import com.anubis.SwissArmyKnife.R
import com.anubis.SwissArmyKnife.app
import com.anubis.kt_extends.eGetShowActivity
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eSetKeyDownExit
import com.anubis.module_gorge.eGorgeMessage
import com.anubis.module_office.dataTest
import com.anubis.module_office.eExportExcel
import com.anubis.module_tts.Bean.ParamMixMode
import com.anubis.module_tts.Bean.TTSMode
import com.anubis.module_tts.Bean.VoiceModel
import com.anubis.module_tts.eTTS
import java.io.OutputStream

/**
 * Author  ： AnubisASN   on 2018-07-21 17:04.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 *Router :  /'Module'/'Function'
 *说明：
 */

@Route(path = "/app/Test1")
class Test1 : AppCompatActivity() {
    private var PATH2: String? = null //串口名称         RS485开门方式
    private var BAUDRATE: Int? = null            //波特率
    private var outputStream: OutputStream? = null     //发送串口的输出流
    private var mp: MediaPlayer? = null
    private var TTS: eTTS? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test1)
        TTS = eTTS.initTTS(app().get()!!, app().get()!!.mHandler!!, TTSMode.MIX, VoiceModel.MALE, ParamMixMode.MIX_MODE_HIGH_SPEED_NETWORK)
        app().get()?.getActivity()!!.add(this)
        eLog(eGetShowActivity())
    }

    fun testClick(v: View) {
        when (v.id) {
            R.id.button22 -> TTS!!.setParams(VoiceModel.EMOTIONAL_MALE).speak("发音人切换,网络优先调用")
            R.id.button33 -> {
                val state = eGorgeMessage().getInit(this).MSG()
                eLog("串口状态："+state)
            }
            R.id.button44 -> startActivity(Intent(this, MainActivity::class.java))
            R.id.button55 -> ARouter.getInstance().build("/face/arcFace").navigation()
            R.id.button66 ->{
                eExportExcel(this, arrayOf("1","2"), mutableListOf(dataTest("s","ss"),dataTest("s1","ss2"),dataTest("s11","ss22")))
//                eExportExcel(this, arrayOf("1","2"), mutableListOf(dataTest("s","ss")))
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        eLog(keyCode)
        return eSetKeyDownExit(keyCode, app().get()!!.getActivity(), false, exitHint = "完成退出")
    }

}
