package com.anubis.module_dialog

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.anubis.kt_extends.*
import kotlinx.coroutines.*

/**
 * Author  ： AnubisASN   on 21-2-21 下午5:24.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 *Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 *Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 *Router :  /'Module'/'Function'
 *说明：
 */

/**
 *说明： 消息发送
 * @调用方法：eSendNotify()
 * @param： smallIcon: Int?，小图标
 * @param：title: String = "系统通知"， 通知标题
 * @param：text: String = "正在后台运行", 通知内容
 * @param: sound: String?="" null-无消息，""-默认 ”XX/X“-音频地址,
 * @param:isForeground:Boolean=false, 是否启是前景通知
 * @param：notifyId: Int = mNotifyId++，通知ID
 * @param：builderBlock: ((Notification.Builder) -> Unit)? = null, Notify 构造器扩展
 * @param：notifyBlock: ((Notification) -> Unit)? = null， Notify 扩展
 * @return: Int， 返回 notifyId  -1-失败
 */
@RequiresApi(Build.VERSION_CODES.N)
open class eForegroundService : Service() {
    companion object {
        val ACTION_STOP = "STOP"
        val ACTION_CLEAN = "CLEAN"

       private var initMallIcon: Int? = null
        var initRunJob: Job? = null
        var initIsVocieWork = true
        private  var initClazz: Class<*>? = null
        private var initIsForeground = true
        private var  initTitle:String?=null
        private var  initText:String?=null
        private var  initSound:String?=null
         var mNotification: eNotification? = null
        private  var customizeNotify:(()->Unit)?=null
        fun initParam(mallIcon:Int,title:String,text:String,sound:String?,customize:(()->Unit)?=null) {
            initMallIcon=mallIcon
            initTitle=title
            initText=text
            initSound=sound
            customizeNotify=customize
            customizeNotify=  customizeNotify?:{ mNotification?.eSendNotify(initMallIcon, initTitle!!, initText!!, initSound,initIsForeground)}
        }
        fun initStart(context: Context, tIntent: Intent, clazz: Class<*>, runJob:Job?,isForeground: Boolean = initIsForeground) {
            initClazz= clazz
            initRunJob=runJob
            initIsForeground = isForeground
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(tIntent)
                return
            }
            context.startService(tIntent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        eLog("onCreate")
        mNotification = eNotification(this, initClazz!!)
        customizeNotify?.let { it() }?:mNotification?.eSendNotify(initMallIcon, isForeground = initIsForeground)
        if (initIsVocieWork)
            ePlayVoice(this, R.raw.n, isLoop = true)
        initRunJob?.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        eLog("onStartCommand")
        intent ?: return START_NOT_STICKY
        when (intent.action) {
            ACTION_CLEAN -> mNotification?.eCleanNotify()
            ACTION_STOP -> mNotification?.eStopNotify()
        }
        return START_NOT_STICKY        //保活的一种
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        eLog("onDestroy")
        initRunJob?.cancel()
        eMediaPlayer?.eClean()
        mNotification?.eStopNotify()
        super.onDestroy()

    }

}
