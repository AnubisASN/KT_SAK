package com.anubis.module_Talkback

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eShowTip
import com.gvs.eventbus.MyEventBusIndex
import com.gvs.eventbus.TalkBackEventBusIndex
import com.gvs.vdp.talkback_os.GvsSdk_OS
import com.gvs.vdp.talkback_os.ITalkBackControl
import com.gvs.vdp.talkback_os.TalkBackBusiness
import org.greenrobot.eventbus.EventBus

/**
 * Author  ： AnubisASN   on 21-6-28 下午3:13.
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
open class eTalkBack() {

    companion object {
var targetActivity:Activity?=null
        private lateinit var LocalAddr: String
        private var ICallTalkBack: ITalkBackControl? = null
        private lateinit var mApp: Application
        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            eTalkBack()
        }

        /** 设置本机编号
         * @param application: Application； app上下文
         * @param localAddr: String;设备本机编号 XX:X-X-X
         * @param ITalkBack: ITalkBackControl? = null;视频对讲连接监听
         * */
        fun eInit(application: Application, localAddr: String, ITalkBack: ITalkBackControl? = null): eTalkBack {
            mApp = application
            LocalAddr = localAddr
            ICallTalkBack = ITalkBack
            return eInit
        }

    }

    init {
        EventBus.builder().addIndex(MyEventBusIndex())
                .addIndex(TalkBackEventBusIndex())
                .installDefaultEventBus()
        GvsSdk_OS.getInstance().init(mApp, LocalAddr)
        TalkBackBusiness.getInstance().setITalkBackControl(ICallTalkBack)
        TalkBackPresenter.instance?.registerLocalTalkBackModule(TalkBackBusiness::class.java.simpleName)
    }


    /** 设置本机编号
     * @param addrStr:String； 本机规则编号 XX:X-X-X
     * @param succBlock:((String)->Unit)?=null;成功回调
     * */
    fun eSetAddress(addrStr: String= LocalAddr,succBlock:((String)->Unit)?=null ):String {
        val ip = GvsSdk_OS.getInstance().changeIdToIp(addrStr)
        eLog("eSetAddress-IP:$ip")
        GvsSdk_OS.getInstance().setLocalAddr(addrStr)
        succBlock?.invoke(ip)
        return  ip
    }

    /** 设置监听
     * @param ITalkBack: ITalkbackView?=null；音视频状态监听
     * */
    fun eSetTalkbackView(ITalkBack: ITalkbackView? = null) {
        TalkBackPresenter.instance?.setTalkbackView(ITalkBack)
    }

    /** 设置监听
     * @param context: Context；上下文
     * @param clazz:Class<*>；通话意图
     * @param textView: TextView?=null；状态显示
     * */
    fun eGetDefaultITalkbackView(context: Context, clazz:Class<*>,textView: TextView?=null) = object : ITalkbackView {
        override fun showMonitorPage(moduleTag: String?, info: String?) {}
        override fun showCountDown(moduleTag: String?, count: Int) {
        }

        override fun showCallOutPage(moduleTag: String?, info: String?) {
            val intent = Intent(context, clazz)
            intent.putExtra("INFO", info)
            intent.putExtra("TYPE", "CallOut")
            context.startActivity(intent)
        }

        override fun showRingPage(moduleTag: String?, info: String) {
            textView?.let { it.text="正在呼叫$info" }
            eLog("showRingPage:正在呼叫$info")
        }

        override fun showTalkPage(moduleTag: String?, info: String) {
            textView?.let { it.text="正在与$info 通话..." }
            eLog("showTalkPage:正在与$info 通话...")
        }

        override fun showHandupPage(moduleTag: String?, info: String) {
            textView?.let { it.text="正在挂机:$info" }
            eLog("showHandupPage:正在挂机:$info")
            targetActivity?.finish()
        }

        override fun showUnlockPage(moduleTag: String?, result: String) {
            textView?.let { it.text="正在开锁:$result" }
            mApp.eShowTip("正在开锁:$result")
            GvsSdk_OS.getInstance().openDoor(0)
        }

        override fun showSuspendPage(moduleTag: String?, info: String?) {}
        override fun showCallTransferPage(moduleTag: String?, info: String?) {}
        override fun showCallTurnPage(moduleTag: String?, info: String?) {}

    }


    /** 呼叫
     * @param number:String； 目标规则编号
     * @param type:String? =null；  目标规则类型
     * */
    fun eCall(number: String, type: String? =null) {
        val str= type?.let { "$number:$type" }?:number
        eLog("call:$str")
        TalkBackPresenter.instance?.callOut(str)
    }

    /** 挂断
     * */
    fun eHandUp() {
        TalkBackPresenter.instance?.handUp()
    }

}
