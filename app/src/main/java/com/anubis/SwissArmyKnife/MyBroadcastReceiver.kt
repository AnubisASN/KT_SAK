package com.anubis.SwissArmyKnife

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.anubis.kt_extends.*

/**
 * Author  ： AnubisASN   on 2018-07-23 9:12.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * Q Q： 773506352
 * 命名规则定义：
 * Module :  module_'ModuleName'
 * Library :  lib_'LibraryName'
 * Package :  'PackageName'_'Module'
 * Class :  'Mark'_'Function'_'Tier'
 * Layout :  'Module'_'Function'
 * Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 * Router :  /'Module'/'Function'
 * 说明：
 */
class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        eLog("接收到广播")
      //  eBReceiver.eInit.eSetPowerBoot(context,intent,MainActivity::class.java)
       // eBReceiver.eInit.eSetAPPUpdateBoot(context,intent,MainActivity::class.java)
    }
}
