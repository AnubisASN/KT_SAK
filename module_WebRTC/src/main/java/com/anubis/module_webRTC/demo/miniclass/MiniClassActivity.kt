package demo.miniclass

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView

import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.demo.BaseActivity
import com.anubis.module_webRTC.demo.MLOC
import com.anubis.module_webRTC.demo.videomeeting.ViewPosition
import com.anubis.module_webRTC.listener.XHLiveManagerListener
import com.anubis.module_webRTC.serverAPI.InterfaceUrls
import com.anubis.module_webRTC.utils.AEvent
import com.starrtc.starrtcsdk.api.XHClient
import com.starrtc.starrtcsdk.api.XHConstants
import com.starrtc.starrtcsdk.api.XHLiveItem
import com.starrtc.starrtcsdk.api.XHLiveManager
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage
import com.starrtc.starrtcsdk.core.player.StarPlayer
import com.starrtc.starrtcsdk.core.player.StarWhitePanel2
import com.starrtc.starrtcsdk.core.pusher.XHCameraRecorder

import org.json.JSONException
import org.json.JSONObject

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.ArrayList

class MiniClassActivity : BaseActivity() {

    private var classId: String? = null
    private var className: String? = null
    private var creatorId: String? = null

    private var vMeetingName: TextView? = null

    private var mDatas: MutableList<XHIMMessage>? = null
    private var mAdapter: MyChatroomListAdapter? = null
    private var vMsgList: ListView? = null
    private var vPlayerView: RelativeLayout? = null
    private var vLinkBtn: TextView? = null
    private var vCameraBtn: ImageView? = null
    private var vMicBtn: ImageView? = null
    private var vCleanBtn: ImageView? = null
    private var vRevokeBtn: ImageView? = null
    private var vLaserPenBtn: ImageView? = null
    private var vSelectColorBtn: ImageView? = null
    private var vSelectColorView: View? = null

    private var mPlayerList: ArrayList<ViewPosition>? = null
    private var borderW = 0
    private var borderH = 0

    private var classManager: XHLiveManager? = null
    private var vPaintPlayer: StarWhitePanel2? = null

    private var isPortrait: Boolean? = true
    private var isUploader: Boolean? = false

    private var starRTCAudioManager: StarRTCAudioManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        starRTCAudioManager = StarRTCAudioManager.create(this)
        starRTCAudioManager!!.start { selectedAudioDevice, availableAudioDevices -> }

        classManager = XHClient.getInstance().getLiveManager(this)
        classManager!!.setRtcMediaType(XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_VIDEO_AND_AUDIO)
        classManager!!.setRecorder(XHCameraRecorder())
        classManager!!.addListener(XHLiveManagerListener())

        val dm = resources.displayMetrics
        if (dm.heightPixels > dm.widthPixels) {
            isPortrait = true
            setContentView(R.layout.activity_mini_class)
        } else {
            isPortrait = false
            setContentView(R.layout.activity_mini_class_landscape)
        }
        vPlayerView = findViewById<View>(R.id.view1) as RelativeLayout
        mPlayerList = ArrayList()
        if (isPortrait!!) {
            val lp = LinearLayout.LayoutParams(dm.widthPixels, dm.widthPixels / 4 * 3)
            vPlayerView!!.layoutParams = lp
            borderW = dm.widthPixels
            borderH = dm.widthPixels / 4 * 3
        } else {
            val lp = LinearLayout.LayoutParams(dm.heightPixels / 4 / 9 * 16, dm.heightPixels)
            vPlayerView!!.layoutParams = lp
            borderW = dm.heightPixels / 4 / 9 * 16
            borderH = dm.heightPixels
        }

        addListener()
        classId = intent.getStringExtra(CLASS_ID)
        className = intent.getStringExtra(CLASS_NAME)
        creatorId = intent.getStringExtra(CLASS_CREATOR)
        vMeetingName = findViewById<View>(R.id.live_id_text) as TextView
        vMeetingName!!.text = "ID：" + className!!
        vPaintPlayer = findViewById<View>(R.id.painter_view) as StarWhitePanel2
        vPaintPlayer!!.setImageHost("api.starrtc.com")

        findViewById<View>(R.id.chat_btn).setOnClickListener {
            findViewById<View>( R.id.white_panel_view).visibility = View.INVISIBLE
            findViewById<View>( R.id.chat_message_view).visibility = View.VISIBLE
        }
        findViewById<View>(R.id.panel_btn).setOnClickListener {
            findViewById<View>(R.id.white_panel_view).visibility = View.VISIBLE
            findViewById<View>(R.id.chat_message_view).visibility = View.INVISIBLE
        }
        findViewById<View>(R.id.send_btn).setOnClickListener {
            val txt = (findViewById<View>(R.id.id_input) as EditText).text.toString()
            if (!TextUtils.isEmpty(txt)) {
                sendChatMsg(txt)
                (findViewById<View>(R.id.id_input) as EditText).setText("")
            }
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow((findViewById<View>(R.id.id_input) as EditText).windowToken, 0)
        }

        vCameraBtn = findViewById<View>(R.id.camera_btn) as ImageView
        vMicBtn = findViewById<View>(R.id.mic_btn) as ImageView
        vLinkBtn = findViewById<View>(R.id.link_btn) as TextView
        vCleanBtn = findViewById<View>(R.id.clean_btn) as ImageView
        vCleanBtn!!.setOnClickListener { vPaintPlayer!!.clean() }
        vRevokeBtn = findViewById<View>(R.id.revoke_btn) as ImageView
        vRevokeBtn!!.setOnClickListener { vPaintPlayer!!.revoke() }
        vLaserPenBtn = findViewById<View>(R.id.laser_btn) as ImageView
        vLaserPenBtn!!.setOnClickListener {
            if (vLaserPenBtn!!.isSelected) {
                vLaserPenBtn!!.isSelected = false
                vPaintPlayer!!.laserPenOff()
            } else {
                vLaserPenBtn!!.isSelected = true
                vPaintPlayer!!.laserPenOn()
            }
        }
        vSelectColorView = findViewById(R.id.select_color_view)
        vSelectColorBtn = findViewById<View>(R.id.select_color_btn) as ImageView
        vSelectColorBtn!!.setOnClickListener {
            if (vSelectColorView!!.visibility == View.VISIBLE) {
                vSelectColorView!!.visibility = View.INVISIBLE
            } else {
                vSelectColorView!!.visibility = View.VISIBLE
            }
        }
        val colorClickListener = View.OnClickListener { v ->
            when (v.id) {
                R.id.select_color_black -> {
                    vPaintPlayer!!.setSelectColor(0x000000)
                    vSelectColorBtn!!.setBackgroundResource(R.drawable.pen_color_bg_black)
                }
                R.id.select_color_red -> {
                    vPaintPlayer!!.setSelectColor(0xcf0206)
                    vSelectColorBtn!!.setBackgroundResource(R.drawable.pen_color_bg_red)
                }
                R.id.select_color_yellow -> {
                    vPaintPlayer!!.setSelectColor(0xf59b00)
                    vSelectColorBtn!!.setBackgroundResource(R.drawable.pen_color_bg_yellow)
                }
                R.id.select_color_green -> {
                    vPaintPlayer!!.setSelectColor(0x3dc25a)
                    vSelectColorBtn!!.setBackgroundResource(R.drawable.pen_color_bg_green)
                }
                R.id.select_color_blue -> {
                    vPaintPlayer!!.setSelectColor(0x0029f7)
                    vSelectColorBtn!!.setBackgroundResource(R.drawable.pen_color_bg_blue)
                }
                R.id.select_color_purple -> {
                    vPaintPlayer!!.setSelectColor(0x8600a7)
                    vSelectColorBtn!!.setBackgroundResource(R.drawable.pen_color_bg_purple)
                }
            }
            vSelectColorView!!.visibility = View.INVISIBLE
        }
        findViewById<View>(R.id.select_color_black).setOnClickListener(colorClickListener)
        findViewById<View>(R.id.select_color_red).setOnClickListener(colorClickListener)
        findViewById<View>(R.id.select_color_yellow).setOnClickListener(colorClickListener)
        findViewById<View>(R.id.select_color_green).setOnClickListener(colorClickListener)
        findViewById<View>(R.id.select_color_blue).setOnClickListener(colorClickListener)
        findViewById<View>(R.id.select_color_purple).setOnClickListener(colorClickListener)
        vCameraBtn!!.isSelected = true
        vMicBtn!!.isSelected = true
        vCameraBtn!!.setOnClickListener {
            if (vCameraBtn!!.isSelected) {
                vCameraBtn!!.isSelected = false
                classManager!!.setVideoEnable(false)
                sendChatMsg("关闭摄像头")
                for (i in mPlayerList!!.indices) {
                    if (mPlayerList!![i].userId == MLOC.userId) {
                        mPlayerList!![i].videoPlayer.visibility = View.INVISIBLE
                    }
                }
            } else {
                vCameraBtn!!.isSelected = true
                classManager!!.setVideoEnable(true)
                sendChatMsg("打开摄像头")
                for (i in mPlayerList!!.indices) {
                    if (mPlayerList!![i].userId == MLOC.userId) {
                        mPlayerList!![i].videoPlayer.visibility = View.VISIBLE
                    }
                }
            }
        }
        vMicBtn!!.setOnClickListener {
            if (vMicBtn!!.isSelected) {
                vMicBtn!!.isSelected = false
                classManager!!.setAudioEnable(false)
                sendChatMsg("关闭麦克风")
            } else {
                vMicBtn!!.isSelected = true
                classManager!!.setAudioEnable(true)
                sendChatMsg("打开麦克风")
            }
        }

        vLinkBtn!!.setOnClickListener {
            if (isUploader!!) {
                AlertDialog.Builder(this@MiniClassActivity).setCancelable(true)
                        .setTitle("是否结束互动?")
                        .setNegativeButton("取消") { arg0, arg1 -> }.setPositiveButton("确定"
                        ) { arg0, arg1 ->
                            isUploader = false
                            classManager!!.changeToAudience(object : IXHResultCallback {
                                override fun success(data: Any) {

                                }

                                override fun failed(errMsg: String) {

                                }
                            })
                            vLinkBtn!!.text = "互动"
                            vPaintPlayer!!.pause()
                            vCameraBtn!!.visibility = View.GONE
                            vMicBtn!!.visibility = View.GONE
                            vCleanBtn!!.visibility = View.GONE
                            vRevokeBtn!!.visibility = View.GONE
                        }.show()
            } else {
                AlertDialog.Builder(this@MiniClassActivity).setCancelable(true)
                        .setTitle("是否申请互动?")
                        .setNegativeButton("取消") { arg0, arg1 -> }.setPositiveButton("确定"
                        ) { arg0, arg1 -> classManager!!.applyToBroadcaster(creatorId) }.show()
            }
        }

        mDatas = ArrayList()
        vMsgList = findViewById<View>(R.id.msg_list) as ListView
        vMsgList!!.transcriptMode = ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL
        mAdapter = MyChatroomListAdapter()
        vMsgList!!.adapter = mAdapter

        findViewById<View>(R.id.back_btn).setOnClickListener { onBackPressed() }
        init()
    }

    private fun init() {
        if (creatorId == MLOC.userId) {
            vLinkBtn!!.visibility = View.GONE
            vCameraBtn!!.visibility = View.VISIBLE
            vMicBtn!!.visibility = View.VISIBLE
            vCleanBtn!!.visibility = View.VISIBLE
            vRevokeBtn!!.visibility = View.VISIBLE
            vSelectColorBtn!!.visibility = View.VISIBLE
            vLaserPenBtn!!.visibility = View.VISIBLE
            if (classId == null) {
                createNewClass()
            } else {
                startClass()
            }
        } else {
            vLinkBtn!!.visibility = View.VISIBLE
            vCameraBtn!!.visibility = View.GONE
            vMicBtn!!.visibility = View.GONE
            vCleanBtn!!.visibility = View.GONE
            vRevokeBtn!!.visibility = View.GONE
            vSelectColorBtn!!.visibility = View.GONE
            vLaserPenBtn!!.visibility = View.GONE
            if (classId == null) {
                MLOC.showMsg(this@MiniClassActivity, "课堂ID为空")
            } else {
                joinClass()
            }
        }
    }

    private fun createNewClass() {
        isUploader = true
        //创建新直播
        val classItem = XHLiveItem()
        classItem.liveName = className
        classItem.liveType = intent.getSerializableExtra(CLASS_TYPE) as XHConstants.XHLiveType
        classManager!!.createLive(classItem, object : IXHResultCallback {
            override fun success(data: Any) {
                classId = data as String
                try {
                    val info = JSONObject()
                    info.put("id", classId)
                    info.put("creator", MLOC.userId)
                    info.put("name", className)
                    var infostr = info.toString()
                    infostr = URLEncoder.encode(infostr, "utf-8")
                    if (MLOC.AEventCenterEnable!!) {
                        InterfaceUrls.demoSaveToList(MLOC.userId, MLOC.LIST_TYPE_CLASS, classId, infostr)
                    } else {
                        classManager!!.saveToList(MLOC.userId, MLOC.LIST_TYPE_CLASS, classId!!, infostr, null)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                startClass()

            }

            override fun failed(errMsg: String) {
                MLOC.showMsg(this@MiniClassActivity, errMsg)
                stopAndFinish()
            }
        })
    }

    private fun stop() {
        classManager!!.leaveLive(object : IXHResultCallback {
            override fun success(data: Any) {
                stopAndFinish()
            }

            override fun failed(errMsg: String) {
                MLOC.showMsg(this@MiniClassActivity, errMsg)
                stopAndFinish()
            }
        })
    }

    private fun startClass() {
        isUploader = true
        //开始直播
        vPaintPlayer!!.publish(classManager, MLOC.userId)
        classManager!!.startLive(classId, object : IXHResultCallback {
            override fun success(data: Any) {
                MLOC.d("XHLiveManager", "startLive success $data")
            }

            override fun failed(errMsg: String) {
                MLOC.d("XHLiveManager", "startLive failed $errMsg")
                MLOC.showMsg(this@MiniClassActivity, errMsg)
                stopAndFinish()
            }
        })
    }

    private fun joinClass() {
        isUploader = false
        //观看直播
        classManager!!.watchLive(classId!!, object : IXHResultCallback {
            override fun success(data: Any) {
                MLOC.d("XHLiveManager", "watchLive success $data")
            }

            override fun failed(errMsg: String) {
                MLOC.d("XHLiveManager", "watchLive failed $errMsg")
                MLOC.showMsg(this@MiniClassActivity, errMsg)
                stopAndFinish()
            }
        })
    }

    fun addListener() {
        AEvent.addListener(AEvent.AEVENT_LIVE_ADD_UPLOADER, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_ERROR, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_GET_ONLINE_NUMBER, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_SELF_KICKED, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_SELF_BANNED, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_REV_MSG, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_REV_PRIVATE_MSG, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_REV_REALTIME_DATA, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_APPLY_LINK, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_APPLY_LINK_RESULT, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_INVITE_LINK, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_INVITE_LINK_RESULT, this)
        AEvent.addListener(AEvent.AEVENT_LIVE_SELF_COMMANDED_TO_STOP, this)
    }

    fun removeListener() {
        AEvent.removeListener(AEvent.AEVENT_LIVE_ADD_UPLOADER, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_ERROR, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_GET_ONLINE_NUMBER, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_SELF_KICKED, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_SELF_BANNED, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_REV_MSG, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_REV_PRIVATE_MSG, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_REV_REALTIME_DATA, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_APPLY_LINK, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_APPLY_LINK_RESULT, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_INVITE_LINK, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_INVITE_LINK_RESULT, this)
        AEvent.removeListener(AEvent.AEVENT_LIVE_SELF_COMMANDED_TO_STOP, this)
    }

    public override fun onResume() {
        super.onResume()
        MLOC.canPickupVoip = false
    }

    public override fun onPause() {
        super.onPause()
        MLOC.canPickupVoip = true
    }

    public override fun onRestart() {
        super.onRestart()
        addListener()
    }

    public override fun onStop() {
        removeListener()
        super.onStop()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this@MiniClassActivity).setCancelable(true)
                .setTitle("是否退出课堂?")
                .setNegativeButton("取消") { arg0, arg1 -> }.setPositiveButton("确定"
                ) { arg0, arg1 -> stop() }.show()
    }

    private fun addPlayer(addUserID: String) {
        if (mPlayerList!!.size == 4) return
        val newOne = ViewPosition()
        newOne.userId = addUserID
        val player = StarPlayer(this)
        newOne.videoPlayer = player
        mPlayerList!!.add(newOne)
        vPlayerView!!.addView(player)
        resetLayout()
        player.setZOrderMediaOverlay(true)

        if (mPlayerList!!.size == 1) {
            classManager!!.attachPlayerView(addUserID, player, true)
        } else {
            classManager!!.attachPlayerView(addUserID, player, false)
        }
    }

    private fun deletePlayer(removeUserId: String) {
        if (mPlayerList != null && mPlayerList!!.size > 0) {
            for (i in mPlayerList!!.indices) {
                val temp = mPlayerList!![i]
                if (temp.userId == removeUserId) {
                    val remove = mPlayerList!!.removeAt(i)
                    vPlayerView!!.removeView(remove.videoPlayer)
                    resetLayout()
                    classManager!!.changeToBig(mPlayerList!![0].userId)
                    break
                }
            }
        }
    }

    private fun resetLayout() {
        if (isPortrait!!) {
            for (i in mPlayerList!!.indices) {
                val player = mPlayerList!![i].videoPlayer
                val lp = RelativeLayout.LayoutParams(borderW / 2, borderH / 2)
                player.layoutParams = lp
                player.y = (if (i < 2) 0 else borderH / 2).toFloat()
                player.x = (if (i % 2 == 0) 0 else borderW / 2).toFloat()
            }
        } else {
            for (i in mPlayerList!!.indices) {
                val player = mPlayerList!![i].videoPlayer
                val lp = RelativeLayout.LayoutParams(borderW, borderH / 4)
                player.layoutParams = lp
                player.y = (i * borderH / 4).toFloat()
                player.x = 0f
            }
        }
    }

    private fun decodeMiniClassMsgContentData(txt: String): String {
        //        {
        //                listType: _type,
        //                from: _from,
        //                fromAvatar: _fromAvatar,
        //                fromNick: _fromNick,
        //                text: _text
        //        }

        try {
            val jsonObject = JSONObject(txt)
            var msgTxt = ""
            msgTxt = jsonObject.getString("text")
            return msgTxt
        } catch (e: JSONException) {
            e.printStackTrace()
            return txt
        }

    }

    private fun sendChatMsg(msg: String) {
        MLOC.d("XHLiveManager", "sendChatMsg $msg")

        //        {
        //                listType: _type,
        //                from: _from,
        //                fromAvatar: _fromAvatar,
        //                fromNick: _fromNick,
        //                text: _text
        //        }
        var msgTxt = msg
        try {
            val jsonObject = JSONObject()
            jsonObject.put("listType", "text")
            jsonObject.put("from", MLOC.userId)
            jsonObject.put("fromAvatar", "")
            jsonObject.put("fromNick", MLOC.userId)
            jsonObject.put("text", msg)
            msgTxt = jsonObject.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val imMessage = classManager!!.sendMessage(msgTxt, null)
        imMessage.contentData = msg
        mDatas!!.add(imMessage)
        mAdapter!!.notifyDataSetChanged()
    }

    inner class MyChatroomListAdapter : BaseAdapter() {
        private val mInflater: LayoutInflater

        init {
            mInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        override fun getCount(): Int {
            return if (mDatas == null) 0 else mDatas!!.size
        }

        override fun getItem(position: Int): Any? {
            return if (mDatas == null) null else mDatas!![position]
        }

        override fun getItemId(position: Int): Long {
            return if (mDatas == null) 0 else position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: ViewHolder
            if (convertView == null) {
                holder = ViewHolder()
                convertView = mInflater.inflate(R.layout.item_class_msg_list, null)
                holder.vUserId = convertView!!.findViewById<View>(R.id.item_user_id) as TextView
                holder.vMsg = convertView.findViewById<View>(R.id.item_msg) as TextView
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            val msgText = mDatas!![position].contentData
            holder.vMsg!!.text = msgText
            holder.vUserId!!.text = mDatas!![position].fromId
            return convertView
        }
    }

    inner class ViewHolder {
        var vUserId: TextView? = null
        var vMsg: TextView? = null
    }

    override fun dispatchEvent(aEventID: String, success: Boolean, eventObj: Any) {
        super.dispatchEvent(aEventID, success, eventObj)
        when (aEventID) {
            AEvent.AEVENT_LIVE_ADD_UPLOADER -> try {
                val data = eventObj as JSONObject
                val addId = data.getString("actorID")
                addPlayer(addId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            AEvent.AEVENT_LIVE_REMOVE_UPLOADER -> try {
                val data = eventObj as JSONObject
                val removeUserId = data.getString("actorID")
                deletePlayer(removeUserId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            AEvent.AEVENT_LIVE_ERROR -> {
                val errStr = eventObj as String
                MLOC.showMsg(applicationContext, errStr)
                stopAndFinish()
            }
            AEvent.AEVENT_LIVE_GET_ONLINE_NUMBER -> {
            }
            AEvent.AEVENT_LIVE_SELF_KICKED -> {
                MLOC.showMsg(this@MiniClassActivity, "你已被踢出")
                stopAndFinish()
            }
            AEvent.AEVENT_LIVE_SELF_BANNED -> {
            }
            AEvent.AEVENT_LIVE_REV_MSG, AEvent.AEVENT_LIVE_REV_PRIVATE_MSG -> {
                val revMsg = eventObj as XHIMMessage
                revMsg.contentData = decodeMiniClassMsgContentData(revMsg.contentData)
                mDatas!!.add(revMsg)
                mAdapter!!.notifyDataSetChanged()
                if (revMsg.contentData == "打开摄像头") {
                    for (i in mPlayerList!!.indices) {
                        if (mPlayerList!![i].userId == revMsg.fromId) {
                            mPlayerList!![i].videoPlayer.visibility = View.VISIBLE
                        }
                    }
                } else if (revMsg.contentData == "关闭摄像头") {
                    for (i in mPlayerList!!.indices) {
                        if (mPlayerList!![i].userId == revMsg.fromId) {
                            mPlayerList!![i].videoPlayer.visibility = View.INVISIBLE
                        }
                    }
                }
            }
            AEvent.AEVENT_LIVE_REV_REALTIME_DATA -> if (success) {
                try {
                    val jsonObject = eventObj as JSONObject
                    val tData = jsonObject.get("data") as ByteArray
                    val tUpid = jsonObject.getString("upId")
                    vPaintPlayer!!.setPaintData(tData, tUpid)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
            AEvent.AEVENT_LIVE_APPLY_LINK -> AlertDialog.Builder(this@MiniClassActivity).setCancelable(true)
                    .setTitle(eventObj.toString() + "申请互动")
                    .setNegativeButton("拒绝") { arg0, arg1 -> classManager!!.refuseApplyToBroadcaster(eventObj as String) }.setPositiveButton("同意"
                    ) { arg0, arg1 ->
                        sendChatMsg("欢迎新的小伙伴上麦！！！")
                        classManager!!.agreeApplyToBroadcaster(eventObj as String)
                    }.show()
            AEvent.AEVENT_LIVE_APPLY_LINK_RESULT -> {
                isUploader = true
                classManager!!.changeToBroadcaster(object : IXHResultCallback {
                    override fun success(data: Any) {

                    }

                    override fun failed(errMsg: String) {

                    }
                })
                vPaintPlayer!!.publish(classManager, MLOC.userId)
                vLinkBtn!!.text = "停止"
                vCameraBtn!!.visibility = View.VISIBLE
                vMicBtn!!.visibility = View.VISIBLE
                vCleanBtn!!.visibility = View.GONE
                vRevokeBtn!!.visibility = View.VISIBLE
                vSelectColorBtn!!.visibility = View.VISIBLE
                vLaserPenBtn!!.visibility = View.GONE
            }
            AEvent.AEVENT_LIVE_INVITE_LINK -> AlertDialog.Builder(this@MiniClassActivity).setCancelable(true)
                    .setTitle(eventObj.toString() + "邀请您互动")
                    .setNegativeButton("拒绝") { arg0, arg1 -> classManager!!.refuseInviteToBroadcaster(eventObj as String) }.setPositiveButton("同意"
                    ) { arg0, arg1 ->
                        vLinkBtn!!.text = "停止"
                        vCameraBtn!!.visibility = View.VISIBLE
                        vMicBtn!!.visibility = View.VISIBLE
                        vCleanBtn!!.visibility = View.GONE
                        vRevokeBtn!!.visibility = View.VISIBLE
                        vSelectColorBtn!!.visibility = View.VISIBLE
                        vLaserPenBtn!!.visibility = View.GONE
                        isUploader = true
                        classManager!!.agreeInviteToBroadcaster(eventObj as String)
                    }.show()
            AEvent.AEVENT_LIVE_INVITE_LINK_RESULT -> {
                val result = eventObj as XHConstants.XHLiveJoinResult
                when (result) {
                    XHConstants.XHLiveJoinResult.XHLiveJoinResult_accept -> {
                        vLinkBtn!!.text = "停止"
                        vCameraBtn!!.visibility = View.VISIBLE
                        vMicBtn!!.visibility = View.VISIBLE
                        vCleanBtn!!.visibility = View.GONE
                        vRevokeBtn!!.visibility = View.VISIBLE
                        vSelectColorBtn!!.visibility = View.VISIBLE
                        vLaserPenBtn!!.visibility = View.GONE
                    }
                    XHConstants.XHLiveJoinResult.XHLiveJoinResult_refuse -> {
                    }
                    XHConstants.XHLiveJoinResult.XHLiveJoinResult_outtime -> {
                    }
                }
            }
            AEvent.AEVENT_LIVE_SELF_COMMANDED_TO_STOP -> if (isUploader!!) {
                isUploader = false
                vPaintPlayer!!.pause()
                vLinkBtn!!.text = "互动"
                vCameraBtn!!.visibility = View.GONE
                vMicBtn!!.visibility = View.GONE
                vCleanBtn!!.visibility = View.GONE
                vRevokeBtn!!.visibility = View.GONE
                vSelectColorBtn!!.visibility = View.GONE
                vLaserPenBtn!!.visibility = View.GONE
            }
        }
    }

    private fun stopAndFinish() {
        if (starRTCAudioManager != null) {
            starRTCAudioManager!!.stop()
        }
        vPaintPlayer!!.pause()
        removeListener()
        finish()
    }

    companion object {

        var CLASS_ID = "CLASS_ID"             //ID
        var CLASS_NAME = "CLASS_NAME"         //名称
        var CLASS_TYPE = "CLASS_TYPE"         //类型
        var CLASS_CREATOR = "CLASS_CREATOR"   //创建者
    }
}
