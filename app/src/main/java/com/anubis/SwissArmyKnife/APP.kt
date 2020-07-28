package com.anubis.SwissArmyKnife

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.anubis.kt_extends.eCrash
import com.anubis.module_ftp.eDataFTP
import com.tencent.bugly.crashreport.CrashReport
import java.io.File

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
    var mActivityList: ArrayList<Activity>? = null

    companion object {
        private var mInit: APP? = null
        val mAPP: APP get() = mInit!!
        val mActivityList: ArrayList<Activity> = ArrayList()
    }

    override fun onCreate() {
        super.onCreate()
        mInit = this
        CrashReport.initCrashReport(applicationContext, "47d98f44ec", false)
        ARouter.openLog()
        ARouter.openDebug()
        ARouter.init(mInit)
        eDataFTP.init(mInit!!, 3335, "anubis", "anubis")
//        createDir()
        eCrash()
    }


    //---------------------------------------分割线   FTP---------------------------------------------
//    /*创建文件夹*/
//    private fun createDir() {
//        //联胜文件夹
//        val fileLS = File(Environment.getExternalStorageDirectory().toString(), "联胜智能")
//        if (!fileLS.exists()) {
//            fileLS.mkdir()
//        }
//    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // 主要是添加下面这句代码
        MultiDex.install(this)
    }
}
