package com.anubis.SwissArmyKnife.TTS

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import com.anubis.SwissArmyKnife.R
import com.anubis.SwissArmyKnife.app
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eSetKeyDownExit
import com.anubis.module_gorge.eGorgeMessage
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


class Test1 : AppCompatActivity() {
    private var PATH2: String? = null //串口名称         RS485开门方式
    private var BAUDRATE: Int? = null            //波特率
    private var outputStream: OutputStream? = null     //发送串口的输出流
    private var mp: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test1)
        app().get()?.getActivity()!!.add(this)
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
        return  eSetKeyDownExit(keyCode,app().get()!!.getActivity(),false,exitHint = "完成退出")
    }

}
