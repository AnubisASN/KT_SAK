package com.anubis.module_Talkback

import android.util.Log
import com.anubis.kt_extends.eLog
import com.gvs.vdp.talkback_os.TalkBackEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by CZC on 2017/11/27.
 */
class TalkBackPresenter {
    private val TAG = "TalkBackPresenter"
    private val destAddr = " "
    private var talkbackView: ITalkbackView?
    private var localTalkBackStat = TALKBACK_STA_IDEL
    private var cloudTalkBackStat = TALKBACK_STA_IDEL
    private var localTalkBackModule: String? = null
    private var cloudTalkBackModule: String? = null
    fun setTalkbackView(view: ITalkbackView?) {
        talkbackView = view
    }

    fun registerLocalTalkBackModule(moduleName: String?) {
        localTalkBackModule = moduleName
    }

    fun registerCloudTalkBackModule(moduleName: String?) {
        cloudTalkBackModule = moduleName
    }

    fun getTalkBackModuleStat(moduleTag: String): Int {
        return if (moduleTag == LOCAL_TALKBACK_TAG) {
            localTalkBackStat
        } else if (moduleTag == CLOUD_TALKBACK_TAG) {
            cloudTalkBackStat
        } else {
            -1
        }
    }

    private fun setTalkBackModuleStat(moduleTag: String, stat: Int) {
        when (moduleTag) {
            LOCAL_TALKBACK_TAG -> {
                localTalkBackStat = stat
                if (localTalkBackStat == TALKBACK_STA_IDEL) {
                    talkbackEnableModule(cloudTalkBackModule, true)
                } else {
                    talkbackEnableModule(cloudTalkBackModule, false) //锁定云对讲模块，不能被监视
                }
            }
            CLOUD_TALKBACK_TAG -> {
                cloudTalkBackStat = stat
                if (cloudTalkBackStat == TALKBACK_STA_IDEL) {
                    talkbackEnableModule(localTalkBackModule, true)
                } else {
                    talkbackEnableModule(localTalkBackModule, false) //锁定本地对讲模块，不能被监视
                }
            }
            else -> return
        }
    }

    fun callOut(dest_addr: String) {
        callOutModule(dest_addr, localTalkBackModule)
    }

    private fun callOutModule(destAddr: String, moduleName: String?) {
        if (moduleName == null) {
            return
        }
        try {
            Log.d(TAG, "$moduleName call Out:$destAddr")
            val jsonObject = JSONObject()
            jsonObject.put(TalkBackEvent.EVENT_MODULE_NAME, moduleName)
            jsonObject.put(TalkBackEvent.EVENT_TYPE, TalkBackEvent.USER_CALLOUT_EVENT)
            jsonObject.put("DEST", destAddr)
            val event = TalkBackEvent(jsonObject)
            EventBus.getDefault().post(event)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun talkbackEnable(flag: Boolean) {
        talkbackEnableModule(localTalkBackModule, flag)
    }

    fun talkbackEnableModule(moduleName: String?, flag: Boolean) {
        if (moduleName == null) {
            return
        }
        try {
            Log.d(TAG, "$moduleName talkbackEnable:$flag")
            val jsonObject = JSONObject()
            jsonObject.put(TalkBackEvent.EVENT_MODULE_NAME, moduleName)
            jsonObject.put(TalkBackEvent.EVENT_TYPE, TalkBackEvent.USER_TALKBACK_ENABLE_EVENT)
            jsonObject.put("Enable", flag)
            val event = TalkBackEvent(jsonObject)
            EventBus.getDefault().post(event)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun handUp() {
        handUpModule(localTalkBackModule, false, TalkBackEvent.HANDUP_NORMAL)
    }

    fun handUp(flag: Boolean, type: String) {
        handUpModule(localTalkBackModule, flag, type)
    }

    private fun handUpModule(moduleName: String?, flag: Boolean, type: String) {
        if (moduleName == null) {
            return
        }
        Log.d(TAG, "$moduleName handup...")
        try {
            val jsonObject = JSONObject()
            jsonObject.put(TalkBackEvent.EVENT_MODULE_NAME, moduleName)
            jsonObject.put(TalkBackEvent.EVENT_TYPE, TalkBackEvent.USER_HANDUP_EVENT)
            jsonObject.put("DEST", destAddr)
            jsonObject.put("FLAG", flag)
            jsonObject.put("TYPE", type)
            val event = TalkBackEvent(jsonObject)
            EventBus.getDefault().post(event)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun unlock(destIp: String?, destAddr: ByteArray?) {
        try {
            val jsonObject = JSONObject()
            jsonObject.put(TalkBackEvent.EVENT_MODULE_NAME, localTalkBackModule)
            jsonObject.put(TalkBackEvent.EVENT_TYPE, TalkBackEvent.USER_UNLOCK_EVENT)
            jsonObject.put("DEST", destAddr)
            jsonObject.put("DEST_IP", destIp)
            val event = TalkBackEvent(jsonObject)
            EventBus.getDefault().post(event)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun pushToApp(dest_addr: String) {
        if ((getTalkBackModuleStat(LOCAL_TALKBACK_TAG) == TALKBACK_STA_RINGING ||
                        getTalkBackModuleStat(LOCAL_TALKBACK_TAG) == TALKBACK_STA_CALLOUT) &&
                getTalkBackModuleStat(CLOUD_TALKBACK_TAG) == TALKBACK_STA_IDEL) {
            callOutModule(dest_addr, cloudTalkBackModule)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMoonEvent(messageEvent: TalkBackEvent) {
        val msg = messageEvent.message
        try {
            val cmd = msg[TalkBackEvent.EVENT_TYPE].toString()
            val moduleName = msg[TalkBackEvent.EVENT_MODULE_NAME].toString()
            var moduleTag = ""
            moduleTag = if (moduleName == localTalkBackModule) {
                LOCAL_TALKBACK_TAG
            } else if (moduleName == cloudTalkBackModule) {
                CLOUD_TALKBACK_TAG
            } else {
                return
            }
            eLog("cmd:$cmd")
            when (cmd) {
                TalkBackEvent.MONITOR_EVENT -> {
                    if (moduleName == localTalkBackModule) {
                        setTalkBackModuleStat(LOCAL_TALKBACK_TAG, TALKBACK_STA_MONITORING)
                        if (getTalkBackModuleStat(CLOUD_TALKBACK_TAG) == TALKBACK_STA_MONITORING) {
                            handUpModule(localTalkBackModule, true, TalkBackEvent.HANDUP_INTERRUPT)
                        }
                    } else {
                        setTalkBackModuleStat(CLOUD_TALKBACK_TAG, TALKBACK_STA_MONITORING)
                        if (getTalkBackModuleStat(LOCAL_TALKBACK_TAG) == TALKBACK_STA_MONITORING) {
                            handUpModule(localTalkBackModule, true, TalkBackEvent.HANDUP_INTERRUPT)
                        }
                    }
                    if (talkbackView != null) {
                        talkbackView!!.showMonitorPage(moduleTag, msg.getString("DEST"))
                    }
                }
                TalkBackEvent.CALL_TRANSFER_EVENT -> if (talkbackView != null) {
                    talkbackView!!.showCallTransferPage(moduleTag, msg.getString("TRANSFER"))
                }
                TalkBackEvent.HAND_UP_EVENT -> {

                    //本地挂机
                    if (moduleName == localTalkBackModule) {
                        setTalkBackModuleStat(LOCAL_TALKBACK_TAG, TALKBACK_STA_IDEL)
                        //如果不是因为云对讲摘机导致的挂机,同步挂断云对讲模块
                        if (getTalkBackModuleStat(CLOUD_TALKBACK_TAG) != TALKBACK_STA_TALKING) {
                            handUpModule(cloudTalkBackModule, true, TalkBackEvent.HANDUP_INTERRUPT) //挂断云对讲
                        }
                    } else {
                        //云对讲挂机
                        setTalkBackModuleStat(CLOUD_TALKBACK_TAG, TALKBACK_STA_IDEL)
                    }
                    if (talkbackView != null) {
                        val type = msg.getString("TYPE")
                        talkbackView!!.showHandupPage(moduleTag, type)
                    } else {
                        Log.e(TAG, "talkbackView is null")
                    }
                }
                TalkBackEvent.RING_COUNT_EVENT -> if (talkbackView != null) {
                    val ringCount = msg.getInt("COUNT")
                    talkbackView!!.showCountDown(moduleTag, ringCount)
                }
                TalkBackEvent.TALK_COUNT_EVENT -> if (talkbackView != null) {
                    val talkCount = msg.getInt("COUNT")
                    talkbackView!!.showCountDown(moduleTag, talkCount)
                }
                TalkBackEvent.MONITOR_COUNT_EVENT -> if (talkbackView != null) {
                    val monitorCount = msg.getInt("COUNT")
                    talkbackView!!.showCountDown(moduleTag, monitorCount)
                }
                TalkBackEvent.SUSPEND_COUNT_EVENT -> if (talkbackView != null) {
                    val suspendCount = msg.getInt("COUNT")
                    talkbackView!!.showCountDown(moduleTag, suspendCount)
                }
                TalkBackEvent.UNLOCK_EVENT ->
                    if (talkbackView != null) {
                        talkbackView!!.showUnlockPage(moduleTag, msg.getString("DEST"))


                }
                TalkBackEvent.CALL_OUT_EVENT -> {
                    if (moduleName == localTalkBackModule) {
                        setTalkBackModuleStat(LOCAL_TALKBACK_TAG, TALKBACK_STA_CALLOUT)
                    } else {
                        setTalkBackModuleStat(CLOUD_TALKBACK_TAG, TALKBACK_STA_CALLOUT)
                    }
                    if (talkbackView != null) {
                        val addr = msg.getString("DEST")
                        talkbackView!!.showCallOutPage(moduleTag, addr)
                    }
                }
                TalkBackEvent.TALKING_EVENT -> {
                    if (moduleName == localTalkBackModule) {
                        setTalkBackModuleStat(LOCAL_TALKBACK_TAG, TALKBACK_STA_TALKING)
                        handUpModule(cloudTalkBackModule, false, TalkBackEvent.HANDUP_NORMAL)
                    } else {
                        setTalkBackModuleStat(CLOUD_TALKBACK_TAG, TALKBACK_STA_TALKING)
                        handUpModule(localTalkBackModule, false, TalkBackEvent.HANDUP_NORMAL)
                    }
                    if (talkbackView != null) {
                        talkbackView!!.showTalkPage(moduleTag, msg.getString("DEST"))
                    }
                }
                TalkBackEvent.RING_EVENT -> {
                    if (moduleName == localTalkBackModule) {
                        setTalkBackModuleStat(LOCAL_TALKBACK_TAG, TALKBACK_STA_RINGING)
                    } else {
                        setTalkBackModuleStat(CLOUD_TALKBACK_TAG, TALKBACK_STA_RINGING)
                    }
                    if (talkbackView != null) {
                        val destAddr = msg.getString("DEST")
                        talkbackView!!.showRingPage(moduleTag, destAddr)
                    }
                }
                TalkBackEvent.SUSPEND_EVENT -> if (talkbackView != null) {
                    val destAddr1 = msg.getString("DEST")
                    talkbackView!!.showSuspendPage(moduleTag, destAddr1)
                }
                TalkBackEvent.CALL_TURN_EVENT -> if (talkbackView != null) {
                    val destAddr2 = msg.getString("DEST")
                    talkbackView!!.showCallTurnPage(moduleTag, destAddr2)
                }
                TalkBackEvent.CALL_PUSH_APP -> if (moduleName == localTalkBackModule) {
                    val destAddr3 = msg.getString("DEST")
                    pushToApp(destAddr3)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    companion object {
        private var talkBackPresenter: TalkBackPresenter? = null

        // stat define
        const val TALKBACK_STA_IDEL = 0 //空闲状态
        const val TALKBACK_STA_CALLOUT = 1 //呼叫中
        const val TALKBACK_STA_MONITORING = 2 //监视中
        const val TALKBACK_STA_RINGING = 3 //振铃中
        const val TALKBACK_STA_TALKING = 4 //通话中
        const val LOCAL_TALKBACK_TAG = "Local_TalkBack" //本地对讲模块tag
        const val CLOUD_TALKBACK_TAG = "Cloud_TalkBack" //云对讲模块tag

        //单例
        val instance: TalkBackPresenter?
            get() {
                synchronized(TalkBackPresenter::class.java) {
                    if (talkBackPresenter == null) {
                        talkBackPresenter = TalkBackPresenter()
                    }
                }
                return talkBackPresenter
            }
    }

    init {
        EventBus.getDefault().register(this)
        talkbackView = null
    }
}
