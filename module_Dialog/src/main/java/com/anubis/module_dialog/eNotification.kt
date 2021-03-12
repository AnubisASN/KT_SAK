package com.anubis.module_dialog

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.anubis.kt_extends.eLogE

/**
 *类说明：Notification 消息发送
 * 支持普通弹窗消息与前台服务
 */

/**
 * Author  ： AnubisASN   on 20-7-16 上午9:32.
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
 *说明： 通知
 */
@RequiresApi(Build.VERSION_CODES.N)
open class eNotification(val mContext: Context, val mClazz: Class<*>) {
    private var mNotification: Notification? = null
    private var mManager: NotificationManager? = null
   companion object{
       private var mNotifyId: Int = 1
   }

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
    open fun eSendNotify(smallIcon: Int?, title: String = "系统通知", text: String = "正在后台运行", sound: String? = "", isForeground: Boolean = false, notifyId:Int = mNotifyId++, builderBlock: ((Notification.Builder) -> Unit)? = null, notifyBlock: ((Notification) -> Unit)? = null): Int {
        mManager = mManager
                ?: (mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        //进行8.0的判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(notifyId.toString(),
                    title, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            mManager?.createNotificationChannel(notificationChannel)
        }
        val mBuild = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(mContext.applicationContext, notifyId.toString()).setChannelId(notifyId.toString())
        } else {
            Notification.Builder(mContext.applicationContext)
        } //获取一个Notification构造器
        val nfIntent = Intent(mContext, mClazz)
        mBuild.setContentIntent(PendingIntent.getActivity(mContext, notifyId, nfIntent, 0)) // FLAG_ONE_SHOT 第一次有效
                .setContentTitle(title) // 设置下拉列表里的标题
                .setContentText(text) // 设置上下文内容
                .setAutoCancel(true)
        smallIcon?.let { mBuild.setSmallIcon(it) }  // 设置状态栏内的小图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            mBuild.setChannelId(notifyId.toString())
        builderBlock?.let { it(mBuild) }
        mNotification = mBuild.build()
        with(mNotification!!) {
            sound?.let { if (it.isBlank()) defaults = Notification.DEFAULT_SOUND else this.sound = Uri.parse("file://$it") }
            notifyBlock?.let { it(this) }
            if (isForeground) {
                return if (mContext is Service) {
                    mContext.startForeground(notifyId, this)
                    notifyId
                } else {
                    -1
                }
            }
            mManager!!.notify(notifyId, this)
        }
        return notifyId
    }

    /**
     *说明： 前台服务停止
     * @调用方法：eStopNotify()
     * @return: Boolean 返回状态
     */
    open fun eStopNotify(): Boolean {
        try {
            with((mContext as? Service) ?: return false) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    stopForeground(Service.STOP_FOREGROUND_REMOVE)
                } else {
                    stopSelf()
                }
            }
            return true
        } catch (e: Exception) {
            e.eLogE("eStopNotify")
            return false
        }

    }

    /**
     *说明： 通知清理
     * @调用方法：eCleanNotify()
     * @param notifyId: Int? = null；通知ID  null-全清
     * @return: Boolean 返回状态
     */
    open fun eCleanNotify(notifyId: Int? = null): Boolean {
        try {
            notifyId?.let { mManager?.cancel(it) } ?: mManager?.cancelAll()
            return true
        } catch (e: Exception) {
            e.eLogE("eCleanNotify")
            return false
        }
    }
}
