package com.anubis.SwissArmyKnife

import android.app.Application
import android.os.Handler
import android.os.Message
import com.anubis.module_tts.eTTS

/**
 * Author  ： AnubisASN   on 2018-07-21 17:03.
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

class app : Application() {
    var mTTS: eTTS? = null
    var mHandler: Handler? = null
    companion object {
        var init: app? = null
    }
    override fun onCreate() {
        super.onCreate()
        init = this
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                handleMSG(msg!!)
            }
        }
        mTTS = eTTS.initTTS(this, mHandler as Handler)
    }
    fun get() = init
    fun handleMSG(msg: Message) {

    }
}
