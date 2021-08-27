package com.anubis.app_coroutine

import androidx.appcompat.app.AppCompatActivity
import android.app.Application
import com.anubis.kt_extends.eLog
import com.anubis.module_eventbus.eEventBus
import com.anubis.module_eventbus.observe.eObserveEvent

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

class APP : Application() {
    var mActivityList: ArrayList<AppCompatActivity>? = null

    companion object {
        private var mInit: APP? = null
        val mAPP: APP get() = mInit!!
    }

    override fun onCreate() {
        eLog("APP启动")
        super.onCreate()
        mInit = this
        eEventBus.eInit(this)
    }
}
