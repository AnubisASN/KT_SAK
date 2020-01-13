package com.anubis.module_videochat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Handler
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.lzy.okgo.utils.HttpUtils.runOnUiThread

import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration

@SuppressLint("StaticFieldLeak")
object eVideoChat {
    private var mAppID = "02fb80262bcc46dd95d075a88404b24b"
    private var mChannelName = "demoChannel1"
    private var mLocalContainer: FrameLayout? = null
    private var mRemoteContainer: FrameLayout? = null
    private var mContext: Context? = null
    private var mHandler: Handler? = null
    private var mRtcEngine: RtcEngine? = null // Tutorial Step 1
    val HANDLER_FAILURE_CODE = -1  //连接失败
    val HANDLER_ERROR_CODE = -2   //连接错误
    val HANDLER_CLOSE_CODE = 0     //关闭连接
    val HANDLER_CONNECT_CODE = 1  //连接成功
    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() { // Tutorial Step 1
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) { // Tutorial Step 5
            eLog("第一次连接")
            runOnUiThread { setupRemoteVideo(uid, mRemoteContainer!!) }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            eLog("用户加入")
            val msg = mHandler?.obtainMessage()
            msg?.what=HANDLER_CONNECT_CODE
            msg?.obj="连接成功"
            mHandler?.sendMessage(msg)
        }

        override fun onUserOffline(uid: Int, reason: Int) { // Tutorial Step 7
            eLog("用户离线")
            val msg = mHandler?.obtainMessage()
            msg?.what=HANDLER_CLOSE_CODE
            msg?.obj="关闭连接"
            mHandler?.sendMessage(msg)
            runOnUiThread { onRemoteUserLeft(mRemoteContainer!!) }
        }

        override fun onUserMuteVideo(uid: Int, muted: Boolean) { // Tutorial Step 10
            eLog("用户静音")
            runOnUiThread { onRemoteUserVideoMuted(uid, muted, mRemoteContainer!!) }
        }
    }

    fun init(context: Context, localContainer: FrameLayout, remoteContainer: FrameLayout, appID: String? = mAppID, channelName: String? = mChannelName, handler: Handler? = null) {
        if (appID!=null)
        mAppID = appID
        if (channelName!=null)
        mChannelName = channelName
        mContext = context
        mLocalContainer = localContainer
        mRemoteContainer = remoteContainer
        mHandler = handler
        initializeAgoraEngine()
        setupVideoProfile()
        setupLocalVideo(mLocalContainer!!)
        joinChannel()

//        onVideo.visibility = if (init1!!.getBoolean("video")== false) View.GONE else View.VISIBLE
//        onAudio.visibility = if (init1["audio"] == false) View.GONE else View.VISIBLE
//        onWitchCamera.visibility = if (init1["witchCamera"] == false) View.GONE else View.VISIBLE
//        onClose.visibility = if (init1["close"] == false) View.GONE else View.VISIBLE

//        if (i2["video"] == true)
//            onLocalVideoMuteClicked(onVideo)
//        if (i2["audio"] == true)
//            onLocalAudioMuteClicked(onAudio)
    }


    // 频暂停
    fun onVideoPause(iv: ImageView, localContainer: FrameLayout = mLocalContainer!!) {
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(Color.parseColor("#3F51B5"), PorterDuff.Mode.MULTIPLY)
        }
        mRtcEngine!!.muteLocalVideoStream(iv.isSelected)
        val surfaceView = localContainer.getChildAt(0) as SurfaceView
        surfaceView.setZOrderMediaOverlay(!iv.isSelected)
        surfaceView.visibility = if (iv.isSelected) View.GONE else View.VISIBLE
    }

    //音暂停
    fun onAudioPause(iv: ImageView) {
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(Color.parseColor("#3F51B5"), PorterDuff.Mode.MULTIPLY)
        }
        mRtcEngine!!.muteLocalAudioStream(iv.isSelected)
    }

    //相机切换
    fun onSwitchCamera() {
        mRtcEngine!!.switchCamera()
    }

    //挂断
    fun onVideoClose() {
        leaveChannel()
        RtcEngine.destroy()
        mRtcEngine = null
        (mContext as Activity).finish()
    }


    //Step 1 初始化
    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(mContext, mAppID, mRtcEventHandler)
        } catch (e: Exception) {
            eLogE("initializeAgoraEngine", e)
            throw RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e))
        }

    }

    // Step 2  设置视频配置文件
    private fun setupVideoProfile() {
        mRtcEngine!!.enableVideo()
        mRtcEngine!!.setVideoEncoderConfiguration(VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x360, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT))
    }

    // Tutorial Step 3
    private fun setupLocalVideo(localContainer: FrameLayout) {
        val surfaceView = RtcEngine.CreateRendererView(mContext)
        surfaceView.setZOrderMediaOverlay(true)
        localContainer.addView(surfaceView)
        mRtcEngine!!.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    // Step 4 加入频道
    private fun joinChannel() {
        mRtcEngine!!.joinChannel(null, mChannelName, "Extra Optional Data", 0) // if you do not specify the uid, we will generate the uid for you
    }

    //Step 5  设置远程视频
    private fun setupRemoteVideo(uid: Int, remoteContainer: FrameLayout) {
        if (remoteContainer.childCount >= 1) {
            return
        }
        val surfaceView = RtcEngine.CreateRendererView(mContext)
        remoteContainer.addView(surfaceView)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))

        surfaceView.tag = uid // for mark purpose
    }

    //Step 6 离开频道
    private fun leaveChannel() {
        mRtcEngine!!.leaveChannel()
    }

    // Tutorial Step 7
    private fun onRemoteUserLeft(remoteContainer: FrameLayout) {
        remoteContainer.removeAllViews()

    }

    // Tutorial Step 10
    private fun onRemoteUserVideoMuted(uid: Int, muted: Boolean, remoteContainer: FrameLayout) {
        val surfaceView: SurfaceView
        try {
            surfaceView = remoteContainer.getChildAt(0) as SurfaceView
            val tag = surfaceView.tag
            if (tag != null && tag as Int == uid) {
                surfaceView.visibility = if (muted) View.GONE else View.VISIBLE
            }
        } catch (e: Exception) {
            eLogE("onRemoteUserVideoMuted", e)
        }
    }

}
