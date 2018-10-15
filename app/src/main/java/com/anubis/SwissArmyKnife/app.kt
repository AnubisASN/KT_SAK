package com.anubis.SwissArmyKnife

import android.app.Activity
import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.tencent.bugly.crashreport.CrashReport

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
    var mActivityList: ArrayList<Activity>?=null
    companion object {
        var init: app? = null

    }
    override fun onCreate() {
        super.onCreate()
        mActivityList=ArrayList()
        CrashReport.initCrashReport(applicationContext, "47d98f44ec", false)
        CrashReport.initCrashReport(applicationContext)
        ARouter.openLog()
        ARouter.openDebug()
        init = this
        ARouter.init(init)
        }


    fun get() = init
}
