package com.anubis.app_nertc

import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDex
import com.anubis.kt_extends.eLog
import java.lang.String

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

//        createDir()
    }



    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // 主要是添加下面这句代码
        MultiDex.install(this)
    }
}
