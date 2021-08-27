package com.anubis.app_webrtc

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDex
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eRegex
import com.anubis.kt_extends.eRegex.Companion.eIRegex
import com.anubis.kt_extends.eShowTip
import com.anubis.module_eventbus.eEventBus
import com.anubis.module_eventbus.observe.eObserveEvent
import com.anubis.module_eventbus.post.ePostEvent
import com.anubis.module_voip.PhoneActivity
import com.anubis.module_voip.testAPP
import java.io.File
import java.util.logging.Handler
import kotlin.system.exitProcess


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

class APP : testAPP() {
    var mActivityList: ArrayList<AppCompatActivity>? = null

    companion object {
        private var mInit: APP? = null
        val mAPP: APP get() = mInit!!
        var onlyVoipActivity: RunActivity?=null
    }

    override fun onCreate() {
        super.onCreate()
        mInit = this
        eEventBus.eInit(this)
       eObserveEvent(this,"", IntentFilter("CALL")){ it, intent->
           if (eIRegex.eGetNumber(it).toString().length==it.length){
               ePostEvent(it,timeMillis = 2000)
               with(Intent(this, PhoneActivity::class.java)){
                   flags = Intent.FLAG_ACTIVITY_NEW_TASK
                   try {
                       putExtra("TOKEN",File("/sdcard/img/info/token").readText())
                   }catch (e:Exception){e.eLogE("readTextToken")}
                  android.os.Handler().postDelayed({ startActivity(this)},1000)
               }
           }
           else
               eShowTip( "非手机号")
       }
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
