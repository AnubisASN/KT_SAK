package com.anubis.module_dialog

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.anubis.kt_extends.*
import com.anubis.module_dialog.eNotification.Companion.ACTION_NOTIFICATION
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
 *说明： 前台服务
 */


@RequiresApi(Build.VERSION_CODES.N)
open class eForegroundService : Service() {
     companion object {
        val ACTION_STOP = "STOP"
        val ACTION_CLEAN = "CLEAN"
        private var initClickBlock: ((String) -> Unit)? = null
        private var initMallIcon: Int? = null
        private var initRunJob: Job? = null
          var initNotiLayoutId:Int?=null
        private var initClazz: Class<*>? = null
        private var initIsForeground = true
        private var initTitle: String? = null
        private var initText: String? = null
        private var initSound: String? = null
         private var initIsClose: Boolean=false
         var mNotification: eNotification? = null
        private var customizeNotify: (() -> Unit)? = null

        /**
         *说明： 参数初始化，弹窗内容定义,可选调用，必须第一位
         * @调用方法：initParam()
         * @param： mallIcon: Int?，小图标
         * @param：title: String = "系统通知"， 通知标题
         * @param：text: String = "正在后台运行", 通知内容
         * @param: sound: String?="" null-无消息，""-默认 ”XX/X“-音频地址,
         * @param:customize:(()->Unit)?=null, 定制扩展
         */
        open fun initParam(mallIcon: Int, title: String, text: String, sound: String?, isClose:Boolean=false,customize: (() -> Unit)? = null) {
            initMallIcon = mallIcon
            initTitle = title
            initText = text
            initSound = sound
            initIsClose=isClose
            customizeNotify = customize
            customizeNotify = customizeNotify
                    ?: { mNotification?.eSendNotify(initMallIcon, initTitle!!, initText!!, initSound, initIsForeground) }
        }

        /**
         *说明： 开始服务
         * @调用方法：initStart()
         * @param： context: Context,上下文
         * @param：tIntent: Intent, 服务意图
         * @param：clazz: Class<*>，点击服务打开界面
         * @param: runJob: Job?=null,自定义前台服务运行Job
         * @param:isForeground: Boolean = initIsForeground, 是否是前台服务
         * @param：notiLayoutId:Int?=null，自定义布局 ，设定后 initParam（）将无效
         * @param： clickBlock: ((String) -> Unit)? = null;点击事件响应
         */
        open fun initStart(context: Context, tIntent: Intent, clazz: Class<*>, runJob: Job? = null, isForeground: Boolean = initIsForeground,notiLayoutId:Int?=null, clickBlock: ((String) -> Unit)? = null) {
            initClazz = clazz
            initRunJob = runJob
            initNotiLayoutId=notiLayoutId
            initIsForeground = isForeground
            initClickBlock=clickBlock
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(tIntent)
                return
            }
            context.startService(tIntent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        mNotification = eNotification(this, initClazz?:this::class.java)
        initNotiLayoutId?.let {  mNotification?.eInitRemoteViews(eForegroundService::class.java,it)
            /*设定点击事件*/
        { remoteViews: RemoteViews, intent: Intent ->
            if (initIsClose) {
                remoteViews?.setViewVisibility(R.id.notif_ivClose,View.VISIBLE)
                mNotification?.eSetClick(remoteViews, intent, R.id.notif_ivClose)
            }
            else
                remoteViews.setViewVisibility(R.id.notif_ivClose,View.GONE)
            initMallIcon?.let { remoteViews.setImageViewResource(R.id.notif_iv,it) }
            initTitle?.let { remoteViews.setTextViewText(R.id.notif_tvTitle,it) }
            initText?.let { remoteViews.setTextViewText(R.id.notif_tvText,it) }
        }
        }
        customizeNotify?.let { it() }
                ?: mNotification?.eSendNotify(initMallIcon, isForeground = initIsForeground)
        if (initIsForeground)
            ePlayVoice(this, R.raw.n, isLoop = true)
        initRunJob?.start()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return START_NOT_STICKY
        when (intent.action) {
            ACTION_CLEAN -> mNotification?.eCleanNotify()
            ACTION_STOP -> mNotification?.eStopNotify()
            ACTION_NOTIFICATION -> initClickBlock?.invoke(intent.getStringExtra(eNotification.ACTION_TYPE))
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
