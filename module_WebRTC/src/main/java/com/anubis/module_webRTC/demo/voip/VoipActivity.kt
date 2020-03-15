package com.anubis.module_webRTC.demo.voip

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView

import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.demo.BaseActivity
import com.anubis.module_webRTC.demo.MLOC
import com.anubis.module_webRTC.database.CoreDB
import com.anubis.module_webRTC.database.HistoryBean
import com.anubis.module_webRTC.utils.AEvent
import com.starrtc.starrtcsdk.api.XHClient
import com.starrtc.starrtcsdk.api.XHConstants
import com.starrtc.starrtcsdk.api.XHCustomConfig
import com.starrtc.starrtcsdk.api.XHSDKHelper
import com.starrtc.starrtcsdk.api.XHVoipManager
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager
import com.starrtc.starrtcsdk.core.player.StarPlayer
import com.starrtc.starrtcsdk.core.pusher.XHCameraRecorder
import com.starrtc.starrtcsdk.core.pusher.XHScreenRecorder

import java.text.SimpleDateFormat

class VoipActivity : BaseActivity(), View.OnClickListener {

    private var voipManager: XHVoipManager? = null
    private var targetPlayer: StarPlayer? = null
    private var selfPlayer: StarPlayer? = null
    private var timer: Chronometer? = null
    private var action: String? = null
    private var targetId: String? = null
    private var isTalking: Boolean? = false
    private var starRTCAudioManager: StarRTCAudioManager? = null
    private var xhsdkHelper: XHSDKHelper? = null
    private var mMediaProjectionManager: MediaProjectionManager? = null
    private var mRecorder: XHScreenRecorder? = null

    //    private PushUVCTest pushUVCTest;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        starRTCAudioManager = StarRTCAudioManager.create(this.applicationContext)
        starRTCAudioManager!!.start { selectedAudioDevice, availableAudioDevices -> MLOC.d("onAudioDeviceChanged ", selectedAudioDevice.name) }
        starRTCAudioManager!!.setDefaultAudioDevice(StarRTCAudioManager.AudioDevice.SPEAKER_PHONE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_voip)
        voipManager = XHClient.getInstance().voipManager
        voipManager!!.setRecorder(XHCameraRecorder())
        voipManager!!.rtcMediaType = XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_VIDEO_AND_AUDIO
        addListener()
        targetId = intent.getStringExtra("targetId")
        action = intent.getStringExtra(ACTION)
        targetPlayer = findViewById<View>(R.id.voip_surface_target) as StarPlayer
        selfPlayer = findViewById<View>(R.id.voip_surface_self) as StarPlayer
        selfPlayer!!.setZOrderMediaOverlay(true)
        timer = findViewById<View>(R.id.timer) as Chronometer
        targetPlayer!!.setOnClickListener {
            if (isTalking!!) {
                findViewById<View>(R.id.talking_view).visibility = if (findViewById<View>(R.id.talking_view).visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
            }
        }

        //        final XHCustomRecorder recorder = new XHCustomRecorder(480,480,0,false);
        //        voipManager.setRecorder(recorder);
        //        pushUVCTest = new PushUVCTest(recorder);
        //        pushUVCTest.startRecoder();


        (findViewById<View>(R.id.targetid_text) as TextView).text = targetId
        (findViewById<View>(R.id.head_img) as ImageView).setImageResource(MLOC.getHeadImage(this@VoipActivity, targetId!!))
        findViewById<View>(R.id.calling_hangup).setOnClickListener(this)
        findViewById<View>(R.id.talking_hangup).setOnClickListener(this)
        findViewById<View>(R.id.switch_camera).setOnClickListener { voipManager!!.switchCamera() }
        findViewById<View>(R.id.screen_btn).setOnClickListener(this)
        findViewById<View>(R.id.mic_btn).isSelected = true
        findViewById<View>(R.id.mic_btn).setOnClickListener(this)
        findViewById<View>(R.id.camera_btn).isSelected = true
        findViewById<View>(R.id.camera_btn).setOnClickListener(this)
        findViewById<View>(R.id.speaker_on_btn).setOnClickListener(this)
        findViewById<View>(R.id.speaker_off_btn).setOnClickListener(this)

        if (action == CALLING) {
            showCallingView()
            MLOC.d("newVoip", "call")
            xhsdkHelper = XHSDKHelper()
            xhsdkHelper!!.setDefaultCameraId(1)
            xhsdkHelper!!.startPerview(this, findViewById<View>(R.id.voip_surface_target) as StarPlayer)

            voipManager!!.call(this, targetId, object : IXHResultCallback {
                override fun success(data: Any) {
                    xhsdkHelper!!.stopPerview()
                    xhsdkHelper = null
                    MLOC.d("newVoip", "call success! RecSessionId:$data")
                }

                override fun failed(errMsg: String) {
                    MLOC.d("newVoip", "call failed")
                    stopAndFinish()
                }
            })
        } else {
            MLOC.d("newVoip", "onPickup")
            onPickup()
        }
    }

    private fun setupViews() {
        voipManager!!.setupView(selfPlayer, targetPlayer, object : IXHResultCallback {
            override fun success(data: Any) {
                MLOC.d("newVoip", "setupView success")
            }

            override fun failed(errMsg: String) {
                MLOC.d("newVoip", "setupView failed")
                stopAndFinish()
            }
        })
    }

    fun addListener() {
        AEvent.addListener(AEvent.AEVENT_VOIP_INIT_COMPLETE, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_BUSY, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_REFUSED, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_HANGUP, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CONNECT, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_ERROR, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_TRANS_STATE_CHANGED, this)
    }

    fun removeListener() {
        MLOC.canPickupVoip = true
        AEvent.removeListener(AEvent.AEVENT_VOIP_INIT_COMPLETE, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_BUSY, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_REFUSED, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_HANGUP, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CONNECT, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_ERROR, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_TRANS_STATE_CHANGED, this)
    }

    public override fun onResume() {
        super.onResume()
        MLOC.canPickupVoip = false
        val historyBean = HistoryBean()
        historyBean.type = CoreDB.HISTORY_TYPE_VOIP
        historyBean.lastTime = SimpleDateFormat("MM-dd HH:mm").format(java.util.Date())
        historyBean.conversationId = targetId
        historyBean.newMsgCount = 1
        MLOC.addHistory(historyBean, true)
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onRestart() {
        super.onRestart()
        addListener()
    }

    public override fun onDestroy() {
        removeListener()
        super.onDestroy()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this@VoipActivity).setCancelable(true)
                .setTitle("是否挂断?")
                .setNegativeButton("取消") { arg0, arg1 -> }.setPositiveButton("确定"
                ) { arg0, arg1 ->
                    timer!!.stop()
                    voipManager!!.hangup(object : IXHResultCallback {
                        override fun success(data: Any) {
                            removeListener()
                            stopAndFinish()
                        }

                        override fun failed(errMsg: String) {
                            MLOC.d("", "AEVENT_VOIP_ON_STOP errMsg:$errMsg")
                            MLOC.showMsg(this@VoipActivity, errMsg)
                        }
                    })
                }.show()
    }

    override fun dispatchEvent(aEventID: String, success: Boolean, eventObj: Any) {
        super.dispatchEvent(aEventID, success, eventObj)
        when (aEventID) {
            AEvent.AEVENT_VOIP_REV_BUSY -> {
                MLOC.d("", "对方线路忙")
                MLOC.showMsg(this@VoipActivity, "对方线路忙")
                if (xhsdkHelper != null) {
                    xhsdkHelper!!.stopPerview()
                    xhsdkHelper = null
                }
                stopAndFinish()
            }
            AEvent.AEVENT_VOIP_REV_REFUSED -> {
                MLOC.d("", "对方拒绝通话")
                MLOC.showMsg(this@VoipActivity, "对方拒绝通话")
                if (xhsdkHelper != null) {
                    xhsdkHelper!!.stopPerview()
                    xhsdkHelper = null
                }
                stopAndFinish()
            }
            AEvent.AEVENT_VOIP_REV_HANGUP -> {
                MLOC.d("", "对方已挂断")
                MLOC.showMsg(this@VoipActivity, "对方已挂断")
                timer!!.stop()
                stopAndFinish()
            }
            AEvent.AEVENT_VOIP_REV_CONNECT -> {
                MLOC.d("", "对方允许通话")
                showTalkingView()
            }
            AEvent.AEVENT_VOIP_REV_ERROR -> {
                MLOC.d("", eventObj as String)
                if (xhsdkHelper != null) {
                    xhsdkHelper!!.stopPerview()
                    xhsdkHelper = null
                }
                stopAndFinish()
            }
            AEvent.AEVENT_VOIP_TRANS_STATE_CHANGED -> findViewById<View>(R.id.state).setBackgroundColor(if (eventObj as Int == 0) -0x100 else -0xd66bff)
        }
    }


    private fun showCallingView() {
        findViewById<View>(R.id.calling_view).visibility = View.VISIBLE
        findViewById<View>(R.id.talking_view).visibility = View.GONE
    }

    private fun showTalkingView() {
        isTalking = true
        findViewById<View>(R.id.calling_view).visibility = View.GONE
        findViewById<View>(R.id.talking_view).visibility = View.VISIBLE
        timer!!.base = SystemClock.elapsedRealtime()
        timer!!.start()
        setupViews()
    }

    private fun onPickup() {
        voipManager!!.accept(this, targetId, object : IXHResultCallback {
            override fun success(data: Any) {
                MLOC.d("newVoip", "onPickup OK! RecSessionId:$data")
            }

            override fun failed(errMsg: String) {
                MLOC.d("newVoip", "onPickup failed ")
                stopAndFinish()
            }
        })
        showTalkingView()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.calling_hangup -> {
                voipManager!!.cancel(object : IXHResultCallback {
                    override fun success(data: Any) {
                        stopAndFinish()
                    }

                    override fun failed(errMsg: String) {
                        stopAndFinish()
                    }
                })
                if (xhsdkHelper != null) {
                    xhsdkHelper!!.stopPerview()
                    xhsdkHelper = null
                }
            }
            R.id.talking_hangup -> voipManager!!.hangup(object : IXHResultCallback {
                override fun success(data: Any) {
                    stopAndFinish()
                }

                override fun failed(errMsg: String) {
                    stopAndFinish()
                }
            })
            R.id.screen_btn -> {
                if (!XHCustomConfig.getInstance(this).hardwareEnable) {
                    MLOC.showMsg(this, "需要打开硬编模式")
                    return
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (mRecorder != null) {
                        findViewById<View>(R.id.screen_btn).isSelected = false
                        voipManager!!.resetRecorder(XHCameraRecorder())
                        mRecorder = null
                    } else {
                        if (mMediaProjectionManager == null) {
                            mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                        }
                        val captureIntent = mMediaProjectionManager!!.createScreenCaptureIntent()
                        startActivityForResult(captureIntent, REQUEST_CODE)
                    }
                } else {
                    MLOC.showMsg(this, "系统版本过低，无法使用录屏功能")
                }
            }
            R.id.camera_btn -> if (findViewById<View>(R.id.camera_btn).isSelected) {
                findViewById<View>(R.id.camera_btn).isSelected = false
                voipManager!!.setVideoEnable(false)
            } else {
                findViewById<View>(R.id.camera_btn).isSelected = true
                voipManager!!.setVideoEnable(true)
            }
            R.id.mic_btn -> if (findViewById<View>(R.id.mic_btn).isSelected) {
                findViewById<View>(R.id.mic_btn).isSelected = false
                voipManager!!.setAudioEnable(false)
            } else {
                findViewById<View>(R.id.mic_btn).isSelected = true
                voipManager!!.setAudioEnable(true)
            }
            R.id.speaker_on_btn -> {
                //                starRTCAudioManager.selectAudioDevice(StarRTCAudioManager.AudioDevice.SPEAKER_PHONE);
                starRTCAudioManager!!.setSpeakerphoneOn(true)
                findViewById<View>(R.id.speaker_on_btn).isSelected = true
                findViewById<View>(R.id.speaker_off_btn).isSelected = false
            }
            R.id.speaker_off_btn -> {
                //                starRTCAudioManager.selectAudioDevice(StarRTCAudioManager.AudioDevice.EARPIECE);
                starRTCAudioManager!!.setSpeakerphoneOn(false)
                findViewById<View>(R.id.speaker_on_btn).isSelected = false
                findViewById<View>(R.id.speaker_off_btn).isSelected = true
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        mRecorder = XHScreenRecorder(this, resultCode, data)
        voipManager!!.resetRecorder(mRecorder)
        findViewById<View>(R.id.screen_btn).isSelected = true
    }

    private fun stopAndFinish() {
        if (starRTCAudioManager != null) {
            starRTCAudioManager!!.stop()
        }
        this@VoipActivity.finish()
    }

    companion object {
        var ACTION = "ACTION"
        var RING = "RING"
        var CALLING = "CALLING"

        private val REQUEST_CODE = 1
    }

}
