package com.anubis.module_webRTC.demo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.TypedArray
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.database.CoreDB
import com.anubis.module_webRTC.database.HistoryBean
import com.anubis.module_webRTC.database.MessageBean

import org.json.JSONException
import org.json.JSONObject
import java.util.Timer
import java.util.TimerTask

@SuppressLint("StaticFieldLeak")
/**
 * Created by zhangjt on 2017/8/17.
 */

object MLOC {
    lateinit var appContext: Context
    var userId: String? = ""

    var SERVER_HOST = "demo.starrtc.com"
    var VOIP_SERVER_URL: String? = "$SERVER_HOST:10086"
    var IM_SERVER_URL: String? = "$SERVER_HOST:19903"
    var CHATROOM_SERVER_URL: String? = "$SERVER_HOST:19906"
    var LIVE_VDN_SERVER_URL: String? = "$SERVER_HOST:19928"
    var LIVE_SRC_SERVER_URL: String? = "$SERVER_HOST:19931"
    var LIVE_PROXY_SERVER_URL: String? = "$SERVER_HOST:19932"

    var AEventCenterEnable: Boolean? = false

    var IM_GROUP_LIST_URL: String? = "http://www.starrtc.com/aec/group/list.php"
    var IM_GROUP_INFO_URL: String? = "http://www.starrtc.com/aec/group/members.php"
    var LIST_SAVE_URL: String? = "http://www.starrtc.com/aec/list/save.php"
    var LIST_DELETE_URL: String? = "http://www.starrtc.com/aec/list/del.php"
    var LIST_QUERY_URL: String? = "http://www.starrtc.com/aec/list/query.php"

    val LIST_TYPE_CHATROOM = 0             //IM 聊天室
    val LIST_TYPE_LIVE = 1                 //直播
    val LIST_TYPE_LIVE_PUSH = 2            //直播转推第三方流
    val LIST_TYPE_MEETING = 3              //会议
    val LIST_TYPE_MEETING_PUSH = 4         //会议转推第三方流
    val LIST_TYPE_CLASS = 5                //小班课
    val LIST_TYPE_CLASS_PUSH = 6           //小班课转推第三方流
    val LIST_TYPE_AUDIO_LIVE = 7           //音频直播
    val LIST_TYPE_AUDIO_LIVE_PUSH = 8      //音频直播转推第三方流
    val LIST_TYPE_SUPER_ROOM = 9           //超级对讲
    val LIST_TYPE_SUPER_ROOM_PUSH = 10     //超级对讲转推第三方流

    val LIST_TYPE_LIVE_ALL = LIST_TYPE_LIVE.toString() + "," + LIST_TYPE_LIVE_PUSH
    val LIST_TYPE_MEETING_ALL = LIST_TYPE_MEETING.toString() + "," + LIST_TYPE_MEETING_PUSH
    val LIST_TYPE_CLASS_ALL = LIST_TYPE_CLASS.toString() + "," + LIST_TYPE_CLASS_PUSH
    val LIST_TYPE_AUDIO_LIVE_ALL = LIST_TYPE_AUDIO_LIVE.toString() + "," + LIST_TYPE_AUDIO_LIVE_PUSH
    val LIST_TYPE_SUPER_ROOM_ALL = LIST_TYPE_SUPER_ROOM.toString() + "," + LIST_TYPE_SUPER_ROOM_PUSH
    val LIST_TYPE_PUSH_ALL = (LIST_TYPE_LIVE_PUSH.toString()
            + "," + LIST_TYPE_MEETING_PUSH
            + "," + LIST_TYPE_CLASS_PUSH
            + "," + LIST_TYPE_AUDIO_LIVE_PUSH
            + "," + LIST_TYPE_SUPER_ROOM_PUSH)
    var hasLogout: Boolean = false

    var hasNewC2CMsg = false
    var hasNewGroupMsg = false
    var hasNewVoipMsg = false
    var canPickupVoip = true

    var deleteGroup = false

    private var coreDB: CoreDB? = null

    private var debug: Boolean? = true

    private var mToast: Toast? = null

    internal var dialogs = arrayOfNulls<Dialog>(1)
    internal var dialogTimer: Timer? = null
    internal var timerTask: TimerTask? = null

    private var mHeadIconIds: IntArray? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        if (coreDB == null) {
            coreDB = CoreDB()
        }
        userId = loadSharedData(context, "userId", userId)

        VOIP_SERVER_URL = loadSharedData(context, "VOIP_SERVER_URL", VOIP_SERVER_URL)
        IM_SERVER_URL = loadSharedData(context, "IM_SERVER_URL", IM_SERVER_URL)
        LIVE_SRC_SERVER_URL = loadSharedData(context, "LIVE_SRC_SERVER_URL", LIVE_SRC_SERVER_URL)
        LIVE_PROXY_SERVER_URL = loadSharedData(context, "LIVE_PROXY_SERVER_URL", LIVE_PROXY_SERVER_URL)
        LIVE_VDN_SERVER_URL = loadSharedData(context, "LIVE_VDN_SERVER_URL", LIVE_VDN_SERVER_URL)
        CHATROOM_SERVER_URL = loadSharedData(context, "CHATROOM_SERVER_URL", CHATROOM_SERVER_URL)


        if (loadSharedData(context, "AEC_ENABLE", "0") == "0") {
            AEventCenterEnable = false
        } else {
            AEventCenterEnable = true
        }

        IM_GROUP_LIST_URL = loadSharedData(context, "IM_GROUP_LIST_URL", IM_GROUP_LIST_URL)
        IM_GROUP_INFO_URL = loadSharedData(context, "IM_GROUP_INFO_URL", IM_GROUP_INFO_URL)
        LIST_SAVE_URL = loadSharedData(context, "LIST_SAVE_URL", LIST_SAVE_URL)
        LIST_DELETE_URL = loadSharedData(context, "LIST_DELETE_URL", LIST_DELETE_URL)
        LIST_QUERY_URL = loadSharedData(context, "LIST_QUERY_URL", LIST_QUERY_URL)
    }

    fun setDebug(b: Boolean?) {
        debug = b
    }

    fun d(tag: String, msg: String) {
        if (debug!!) {
            Log.d("starSDK_demo_$tag", msg)
        }
    }

    fun e(tag: String, msg: String) {
        Log.e("starSDK_demo_$tag", msg)
    }

    fun showMsg(str: String) {
        try {
            if (mToast != null) {
                mToast!!.setText(str)
                mToast!!.duration = Toast.LENGTH_SHORT
            } else {
                mToast = Toast.makeText(appContext.applicationContext, str, Toast.LENGTH_SHORT)
            }
            mToast!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun showMsg(context: Context, str: String) {
        try {
            if (mToast != null) {
                mToast!!.setText(str)
                mToast!!.duration = Toast.LENGTH_SHORT
            } else {
                mToast = Toast.makeText(context.applicationContext, str, Toast.LENGTH_SHORT)
            }
            mToast!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getHistoryList(type: String): List<HistoryBean>? {
        return if (coreDB != null) {
            coreDB!!.getHistory(type)
        } else {
            null
        }
    }

    fun addHistory(history: HistoryBean, hasRead: Boolean?) {
        if (coreDB != null) {
            coreDB!!.addHistory(history, hasRead)
        }
    }

    fun updateHistory(history: HistoryBean) {
        if (coreDB != null) {
            coreDB!!.updateHistory(history)
        }
    }

    fun removeHistory(history: HistoryBean) {
        if (coreDB != null) {
            coreDB!!.removeHistory(history)
        }
    }

    fun getMessageList(conversationId: String): List<MessageBean>? {
        return if (coreDB != null) {
            coreDB!!.getMessageList(conversationId)
        } else {
            null
        }
    }

    fun saveMessage(messageBean: MessageBean) {
        if (coreDB != null) {
            coreDB!!.setMessage(messageBean)
        }
    }

    fun saveSharedData(context: Context, key: String, value: String?) {
        val sp = context.applicationContext.getSharedPreferences("stardemo", Activity.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun loadSharedData(context: Context, key: String): String {
        val sp = context.applicationContext.getSharedPreferences("stardemo", Activity.MODE_PRIVATE)
        return sp.getString(key, "")
    }

    fun loadSharedData(context: Context, key: String, defValue: String?): String? {
        val sp = context.applicationContext.getSharedPreferences("stardemo", Activity.MODE_PRIVATE)
        return sp.getString(key, defValue)
    }

    fun saveUserId(id: String) {
        MLOC.userId = id
        MLOC.saveSharedData(appContext, "userId", MLOC.userId)
    }

    fun saveVoipServerUrl(voipServerUrl: String) {
        MLOC.VOIP_SERVER_URL = voipServerUrl
        saveSharedData(appContext, "VOIP_SERVER_URL", VOIP_SERVER_URL)
    }

    fun saveSrcServerUrl(srcServerUrl: String) {
        MLOC.LIVE_SRC_SERVER_URL = srcServerUrl
        saveSharedData(appContext, "LIVE_SRC_SERVER_URL", LIVE_SRC_SERVER_URL)
    }

    fun saveVdnServerUrl(vdnServerUrl: String) {
        MLOC.LIVE_VDN_SERVER_URL = vdnServerUrl
        saveSharedData(appContext, "LIVE_VDN_SERVER_URL", LIVE_VDN_SERVER_URL)
    }

    fun saveProxyServerUrl(proxyServerUrl: String) {
        MLOC.LIVE_PROXY_SERVER_URL = proxyServerUrl
        saveSharedData(appContext, "LIVE_PROXY_SERVER_URL", LIVE_PROXY_SERVER_URL)
    }

    fun saveChatroomServerUrl(chatroomServerUrl: String) {
        MLOC.CHATROOM_SERVER_URL = chatroomServerUrl
        saveSharedData(appContext, "CHATROOM_SERVER_URL", CHATROOM_SERVER_URL)
    }

    fun saveImServerUrl(imServerUrl: String) {
        MLOC.IM_SERVER_URL = imServerUrl
        saveSharedData(appContext, "IM_SERVER_URL", IM_SERVER_URL)
    }

    fun saveC2CUserId(context: Context, uid: String) {
        val history = MLOC.loadSharedData(context.applicationContext, "c2cHistory")
        if (history.length > 0) {
            val arr = history.split(",,".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var newHistory = ""
            for (i in arr.indices) {
                if (i == 0) {
                    if (arr[i] == uid) return
                    newHistory += arr[i]
                } else {
                    if (arr[i] == uid) continue
                    newHistory += ",," + arr[i]
                }
            }
            if (newHistory.length == 0) {
                newHistory = uid
            } else {
                newHistory = "$uid,,$newHistory"
            }
            MLOC.saveSharedData(context.applicationContext, "c2cHistory", newHistory)
        } else {
            MLOC.saveSharedData(context.applicationContext, "c2cHistory", uid)
        }
    }

    fun cleanC2CUserId(context: Context) {
        MLOC.saveSharedData(context.applicationContext, "c2cHistory", "")
    }

    fun saveVoipUserId(context: Context, uid: String) {
        val history = MLOC.loadSharedData(context.applicationContext, "voipHistory")
        if (history.length > 0) {
            val arr = history.split(",,".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var newHistory = ""
            for (i in arr.indices) {
                if (i == 0) {
                    if (arr[i] == uid) return
                    newHistory += arr[i]
                } else {
                    if (arr[i] == uid) continue
                    newHistory += ",," + arr[i]
                }
            }
            if (newHistory.length == 0) {
                newHistory = uid
            } else {
                newHistory = "$uid,,$newHistory"
            }
            MLOC.saveSharedData(context.applicationContext, "voipHistory", newHistory)
        } else {
            MLOC.saveSharedData(context.applicationContext, "voipHistory", uid)
        }
    }

    fun cleanVoipUserId(context: Context) {
        MLOC.saveSharedData(context.applicationContext, "voipHistory", "")
    }

    fun showDialog(context: Context, data: JSONObject) {
        try {
            val type = data.getInt("listType")// 0:c2c,1:group,2:voip
            val farId = data.getString("farId")// 对方ID
            val msg = data.getString("msg")// 提示消息

            if (dialogs[0] == null || dialogs[0]!!.isShowing() == false) {
                dialogs[0] = Dialog(context, com.anubis.module_webRTC.R.style.dialog_notify)
                dialogs[0]!!.setContentView(com.anubis.module_webRTC.R.layout.dialog_new_msg)
                val win = dialogs[0]!!.getWindow()
                win!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                win.setWindowAnimations(com.anubis.module_webRTC.R.style.dialog_notify_animation)
                win.setGravity(Gravity.TOP)
                dialogs[0]!!.setCanceledOnTouchOutside(true)
            }
            (dialogs[0]!!.findViewById<View>(com.anubis.module_webRTC.R.id.msg_info) as TextView).text = msg
            dialogs[0]!!.findViewById<View>(com.anubis.module_webRTC.R.id.yes_btn).setOnClickListener {
                if (dialogTimer != null) {
                    dialogTimer!!.cancel()
                    timerTask!!.cancel()
                    dialogTimer = null
                    timerTask = null
                }
                dialogs[0]!!.dismiss()
                dialogs[0] = null
                //                    if(listType==0){
                //                        //C2C
                //                        Intent intent = new Intent(context,C2CListActivity.class);
                //                        context.startActivity(intent);
                //                    }else if(listType==1){
                //                        //Group
                //                        Intent intent = new Intent(context, MessageGroupListActivity.class);
                //                        context.startActivity(intent);
                //                    }else if(listType==2){
                //                        //VOIP
                //                        Intent intent = new Intent(context, VoipListActivity.class);
                //                        context.startActivity(intent);
                //                    }
            }
            dialogs[0]!!.show()

            if (dialogTimer != null) {
                dialogTimer!!.cancel()
                timerTask!!.cancel()
                dialogTimer = null
                timerTask = null
            }
            dialogTimer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    if (dialogs[0]!= null && dialogs[0]!!.isShowing()) {
                        dialogs[0]!!.dismiss()
                        dialogs[0] = null
                    }
                }
            }
            dialogTimer!!.schedule(timerTask, 5000)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun getHeadImage(context: Context, userID: String): Int {
        if (mHeadIconIds == null) {
            val ar = context.applicationContext.resources.obtainTypedArray(com.anubis.module_webRTC.R.array.head_images)
            val len = ar.length()
            mHeadIconIds = IntArray(len)
            for (i in 0 until len) {
                mHeadIconIds!![i]= ar.getResourceId(i, 0)
            }
            ar.recycle()
        }

        if (userID.isEmpty()) {
            return mHeadIconIds!![70]
        } else {
            var intId = 0
            val chars = userID.toCharArray()
            for (i in chars.indices) {
                intId += chars[i].toInt()
            }
            return mHeadIconIds!![intId % 70]
        }
    }

    fun saveImGroupListUrl(imGroupListUrl: String) {
        MLOC.IM_GROUP_LIST_URL = imGroupListUrl
        saveSharedData(appContext, "IM_GROUP_LIST_URL", IM_GROUP_LIST_URL)
    }

    fun saveImGroupInfoUrl(imGroupInfoUrl: String) {
        MLOC.IM_GROUP_INFO_URL = imGroupInfoUrl
        saveSharedData(appContext, "IM_GROUP_INFO_URL", IM_GROUP_INFO_URL)
    }

    fun saveListSaveUrl(listSaveUrl: String) {
        MLOC.LIST_SAVE_URL = listSaveUrl
        saveSharedData(appContext, "LIST_SAVE_URL", LIST_SAVE_URL)
    }

    fun saveListDeleteUrl(listDeleteUrl: String) {
        MLOC.LIST_DELETE_URL = listDeleteUrl
        saveSharedData(appContext, "LIST_DELETE_URL", LIST_DELETE_URL)
    }

    fun saveListQueryUrl(listQueryUrl: String) {
        MLOC.LIST_QUERY_URL = listQueryUrl
        saveSharedData(appContext, "LIST_QUERY_URL", LIST_QUERY_URL)
    }
}
