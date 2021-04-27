package com.anubis.app_nertc
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anubis.app_nertc.Utils.token
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.netease.lava.nertc.sdk.NERtcCallback
import com.netease.lava.nertc.sdk.NERtcConstants
import com.netease.lava.nertc.sdk.NERtcEx
import com.netease.lava.nertc.sdk.NERtcParameters
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType
import com.netease.lava.nertc.sdk.video.NERtcVideoView
import java.util.*

//  Created by NetEase on 7/31/20.
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//
class MeetingActivity : AppCompatActivity(), NERtcCallback, View.OnClickListener {
    private var enableLocalVideo = true
    private var enableLocalAudio = true
    private var joinedChannel = false
    private var localUserVv: NERtcVideoView? = null
    private var remoteUserVv: NERtcVideoView? = null
    private var waitHintTv: TextView? = null
    private var enableAudioIb: ImageButton? = null
    private var leaveIb: ImageButton? = null
    private var enableVideoIb: ImageButton? = null
    private var cameraFlipImg: ImageView? = null
    private var localUserBgV: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_meeting)
        initViews()
        setupNERtc()
        val roomId = intent.getStringExtra(EXTRA_ROOM_ID)
        val userId = intent.getLongExtra(EXTRA_USER_ID,123L)
        joinChannel(userId, roomId)
    }

    /**
     * 加入房间
     *
     * @param userID 用户ID
     * @param roomID 房间ID
     */
    private fun joinChannel(userID: Long, roomID: String) {
        Log.i(TAG, "joinChannel userId: $userID")
        NERtcEx.getInstance().joinChannel(token, roomID, userID)
        eLog("Token:$token")
        localUserVv!!.setZOrderMediaOverlay(true)
        localUserVv!!.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_FIT)
        NERtcEx.getInstance().setupLocalVideoCanvas(localUserVv)
    }

    override fun onDestroy() {
        super.onDestroy()
        NERtcEx.getInstance().release()
    }

    override fun onBackPressed() {
        exit()
    }

    private fun initViews() {
        localUserVv = findViewById(R.id.vv_local_user)
        remoteUserVv = findViewById(R.id.vv_remote_user)
        waitHintTv = findViewById(R.id.tv_wait_hint)
        enableAudioIb = findViewById(R.id.ib_audio)
        leaveIb = findViewById(R.id.ib_leave)
        enableVideoIb = findViewById(R.id.ib_video)
        cameraFlipImg = findViewById(R.id.img_camera_flip)
        localUserBgV = findViewById(R.id.v_local_user_bg)
        localUserVv?.setVisibility(View.INVISIBLE)
        enableAudioIb?.setOnClickListener(this)
        leaveIb?.setOnClickListener(this)
        enableVideoIb?.setOnClickListener(this)
        cameraFlipImg?.setOnClickListener(this)
    }

    /**
     * 初始化SDK
     */
    private fun setupNERtc() {
        val parameters = NERtcParameters()
        parameters.set(NERtcParameters.KEY_AUTO_SUBSCRIBE_AUDIO, false)
        NERtcEx.getInstance().setParameters(parameters) //先设置参数，后初始化
        try {
            NERtcEx.getInstance().init(applicationContext, getString(R.string.app_key), this, null)
        } catch (e: Exception) {
            // 可能由于没有release导致初始化失败，release后再试一次
            NERtcEx.getInstance().release()
            try {
                NERtcEx.getInstance().init(applicationContext, getString(R.string.app_key), this, null)
            } catch (ex: Exception) {
                eLogE("SDK初始化失败")
                finish()
                return
            }
        }
        eLog("SDK初始化成功")
        setLocalAudioEnable(true)
        setLocalVideoEnable(true)
    }

    /**
     * 随机生成用户ID
     *
     * @return 用户ID
     */
    private fun generateRandomUserID(): Int {
        return Random().nextInt(100000)
    }

    /**
     * 退出房间
     *
     * @return 返回码
     * @see com.netease.lava.nertc.sdk.NERtcConstants.ErrorCode
     */
    private fun leaveChannel(): Boolean {
        joinedChannel = false
        setLocalAudioEnable(false)
        setLocalVideoEnable(false)
        val ret = NERtcEx.getInstance().leaveChannel()
        return ret == NERtcConstants.ErrorCode.OK
    }

    /**
     * 退出房间并关闭页面
     */
    private fun exit() {
        if (joinedChannel) {
            leaveChannel()
        }
        finish()
    }

    override fun onJoinChannel(result: Int, channelId: Long, elapsed: Long) {
        eLog(TAG, "onJoinChannel result: $result channelId: $channelId elapsed: $elapsed")
        if (result == NERtcConstants.ErrorCode.OK) {
            joinedChannel = true
            // 加入房间，准备展示己方视频
            localUserVv!!.visibility = View.VISIBLE
        }
    }

    override fun onLeaveChannel(result: Int) {
        eLog(TAG, "onLeaveChannel result: $result")
    }

    override fun onUserJoined(uid: Long) {
        eLog(TAG, "onUserJoined uid: $uid")
        // 已经有订阅，就不要变了
        if (remoteUserVv!!.tag != null) {
            return
        }
        // 有用户加入，设置Tag，该用户离开前，只订阅和取消订阅此用户
        remoteUserVv!!.tag = uid
        // 不用等待了
        waitHintTv!!.visibility = View.INVISIBLE
    }

    override fun onUserLeave(uid: Long, reason: Int) {
        eLog(TAG, "onUserLeave uid: $uid reason: $reason")
        // 退出的不是当前订阅的对象，则不作处理
        if (!isCurrentUser(uid)) {
            return
        }
        // 设置TAG为null，代表当前没有订阅
        remoteUserVv!!.tag = null
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, false)

        // 显示在等待用户进入房间
        waitHintTv!!.visibility = View.VISIBLE
        // 不展示远端
        remoteUserVv!!.visibility = View.INVISIBLE
    }

    override fun onUserAudioStart(uid: Long) {
        eLog(TAG, "onUserAudioStart uid: $uid")
        if (!isCurrentUser(uid)) {
            return
        }
        NERtcEx.getInstance().subscribeRemoteAudioStream(uid, true)
    }

    override fun onUserAudioStop(uid: Long) {
        eLog(TAG, "onUserAudioStop, uid=$uid")
    }

    override fun onUserVideoStart(uid: Long, profile: Int) {
        eLog(TAG, "onUserVideoStart uid: $uid profile: $profile")
        if (!isCurrentUser(uid)) {
            return
        }
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true)
        remoteUserVv!!.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_FIT)
        NERtcEx.getInstance().setupRemoteVideoCanvas(remoteUserVv, uid)

        // 更新界面
        remoteUserVv!!.visibility = View.VISIBLE
    }

    override fun onUserVideoStop(uid: Long) {
        eLog(TAG, "onUserVideoStop, uid=$uid")
        if (!isCurrentUser(uid)) {
            return
        }
        // 不展示远端
        remoteUserVv!!.visibility = View.INVISIBLE
    }

    override fun onDisconnect(reason: Int) {
        eLog(TAG, "onDisconnect reason: $reason")
        if (reason != NERtcConstants.ErrorCode.OK) {
            finish()
        }
    }

    override fun onClientRoleChange(p0: Int, p1: Int) {
    }

    /**
     * 判断是否为onUserJoined中，设置了Tag的用户
     *
     * @param uid 用户ID
     * @return 用户ID是否匹配
     */
    private fun isCurrentUser(uid: Long): Boolean {
        val tag = remoteUserVv!!.tag
        eLog(TAG, "isCurrentUser tag=$tag")
        return tag != null && tag == uid
    }

    /**
     * 改变本地音频的可用性
     */
    private fun changeAudioEnable() {
        enableLocalAudio = !enableLocalAudio
        setLocalAudioEnable(enableLocalAudio)
    }

    /**
     * 改变本地视频的可用性
     */
    private fun changeVideoEnable() {
        enableLocalVideo = !enableLocalVideo
        setLocalVideoEnable(enableLocalVideo)
    }

    /**
     * 设置本地音频的可用性
     */
    private fun setLocalAudioEnable(enable: Boolean) {
        enableLocalAudio = enable
        NERtcEx.getInstance().enableLocalAudio(enableLocalAudio)
        enableAudioIb!!.setImageResource(if (enable) R.drawable.selector_meeting_mute else R.drawable.selector_meeting_unmute)
    }

    /**
     * 设置本地视频的可用性
     */
    private fun setLocalVideoEnable(enable: Boolean) {
        enableLocalVideo = enable
        NERtcEx.getInstance().enableLocalVideo(enableLocalVideo)
        enableVideoIb!!.setImageResource(if (enable) R.drawable.selector_meeting_close_video else R.drawable.selector_meeting_open_video)
        localUserVv!!.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        localUserBgV!!.setBackgroundColor(resources.getColor(if (enable) R.color.white else R.color.black))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ib_audio -> changeAudioEnable()
            R.id.ib_leave -> exit()
            R.id.ib_video -> changeVideoEnable()
            R.id.img_camera_flip -> NERtcEx.getInstance().switchCamera()
            else -> {
            }
        }
    }

    companion object {
        private const val TAG = "MeetingActivity"
        private const val EXTRA_ROOM_ID = "extra_room_id"
        private const val EXTRA_USER_ID = "extra_user_id"
        fun startActivity(from: Activity, roomId: String?,userID: Long) {
            val intent = Intent(from, MeetingActivity::class.java)
            intent.putExtra(EXTRA_ROOM_ID, roomId)
            intent.putExtra(EXTRA_USER_ID, userID)
            from.startActivity(intent)
        }
    }
}
