package com.anubis.module_webRTC.demo.p2p

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.TextView

import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.demo.BaseActivity
import com.anubis.module_webRTC.demo.MLOC
import com.anubis.module_webRTC.ui.CircularCoverView
import com.anubis.module_webRTC.utils.AEvent
import com.anubis.module_webRTC.utils.ColorUtils
import com.anubis.module_webRTC.utils.DensityUtils
import com.starrtc.starrtcsdk.api.XHClient
import com.starrtc.starrtcsdk.api.XHConstants
import com.starrtc.starrtcsdk.api.XHVoipP2PManager
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager
import com.starrtc.starrtcsdk.core.player.StarPlayer
import com.starrtc.starrtcsdk.core.pusher.XHCameraRecorder

class VoipP2PActivity : BaseActivity(), View.OnClickListener {

    internal var TAG = "VOIP P2P VoipP2PActivity"
    private var voipP2PManager: XHVoipP2PManager? = null

    private var targetPlayer: StarPlayer? = null
    private var selfPlayer: StarPlayer? = null
    private var timer: Chronometer? = null
    private var action: String? = null
    private var targetId: String? = null
    private var isTalking: Boolean? = false

    private var starRTCAudioManager: StarRTCAudioManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        starRTCAudioManager = StarRTCAudioManager.create(this)
        starRTCAudioManager!!.start { selectedAudioDevice, availableAudioDevices -> }

        setContentView(R.layout.activity_voip_p2p)
        voipP2PManager = XHClient.getInstance().voipP2PManager
        voipP2PManager!!.setRtcMediaType(XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_VIDEO_AND_AUDIO)
        voipP2PManager!!.setRecorder(XHCameraRecorder())
        addListener()

        targetId = intent.getStringExtra("targetId")
        action = intent.getStringExtra(ACTION)

        MLOC.d(TAG, "targetId " + targetId!!)

        targetPlayer = findViewById<View>(R.id.voip_surface_target) as StarPlayer
        selfPlayer = findViewById<View>(R.id.voip_surface_self) as StarPlayer
        selfPlayer!!.setZOrderMediaOverlay(true)
        timer = findViewById<View>(R.id.timer) as Chronometer
        targetPlayer!!.setOnClickListener {
            if (isTalking!!) {
                findViewById<View>(R.id.talking_view).visibility = if (findViewById<View>(R.id.talking_view).visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
            }
        }

        (findViewById<View>(R.id.targetid_text) as TextView).text = targetId
        findViewById<View>(R.id.head_bg).setBackgroundColor(ColorUtils.getColor(this@VoipP2PActivity, targetId))
        (findViewById<View>(R.id.head_cover) as CircularCoverView).setCoverColor(Color.parseColor("#000000"))
        val cint = DensityUtils.dip2px(this@VoipP2PActivity, 45f)
        (findViewById<View>(R.id.head_cover) as CircularCoverView).setRadians(cint, cint, cint, cint, 0)

        findViewById<View>(R.id.calling_hangup).setOnClickListener(this)
        findViewById<View>(R.id.talking_hangup).setOnClickListener(this)

        if (action == CALLING) {
            showCallingView()
            MLOC.d(TAG, "call")
            voipP2PManager!!.call(this, targetId, object : IXHResultCallback {
                override fun success(data: Any) {
                    MLOC.d(TAG, "call success")
                }

                override fun failed(errMsg: String) {
                    MLOC.d(TAG, "call failed")
                    this@VoipP2PActivity.stopAndFinish()
                }
            })
        } else {
            onPickup()
            showTalkingView()
        }

    }

    fun addListener() {
        AEvent.addListener(AEvent.AEVENT_VOIP_INIT_COMPLETE, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_BUSY, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_REFUSED, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_HANGUP, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CONNECT, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_ERROR, this)
    }

    fun removeListener() {
        AEvent.removeListener(AEvent.AEVENT_VOIP_INIT_COMPLETE, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_BUSY, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_REFUSED, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_HANGUP, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CONNECT, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_ERROR, this)
    }

    public override fun onResume() {
        super.onResume()
        MLOC.canPickupVoip = false
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
        MLOC.canPickupVoip = true
        super.onDestroy()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this@VoipP2PActivity).setCancelable(true)
                .setTitle("是否挂断?")
                .setNegativeButton("取消") { arg0, arg1 -> }.setPositiveButton("确定"
                ) { arg0, arg1 ->
                    timer!!.stop()
                    voipP2PManager!!.hangup(object : IXHResultCallback {
                        override fun success(data: Any) {
                            stopAndFinish()
                        }

                        override fun failed(errMsg: String) {
                            MLOC.d(TAG, "AEVENT_VOIP_ON_STOP errMsg:$errMsg")
                            MLOC.showMsg(this@VoipP2PActivity, errMsg)
                        }
                    })
                }.show()
    }

    override fun dispatchEvent(aEventID: String, success: Boolean, eventObj: Any) {
        super.dispatchEvent(aEventID, success, eventObj)
        when (aEventID) {
            AEvent.AEVENT_VOIP_REV_BUSY -> {
                MLOC.d(TAG, "对方线路忙")
                MLOC.showMsg(this@VoipP2PActivity, "对方线路忙")
                this@VoipP2PActivity.stopAndFinish()
            }
            AEvent.AEVENT_VOIP_REV_REFUSED -> {
                MLOC.d(TAG, "对方拒绝通话")
                MLOC.showMsg(this@VoipP2PActivity, "对方拒绝通话")
                this@VoipP2PActivity.stopAndFinish()
            }
            AEvent.AEVENT_VOIP_REV_HANGUP -> {
                MLOC.d(TAG, "对方已挂断")
                MLOC.showMsg(this@VoipP2PActivity, "对方已挂断")
                timer!!.stop()
                this@VoipP2PActivity.stopAndFinish()
            }
            AEvent.AEVENT_VOIP_REV_CONNECT -> {
                MLOC.d(TAG, "对方允许通话")
                showTalkingView()
            }
            AEvent.AEVENT_VOIP_REV_ERROR -> {
                MLOC.d(TAG, eventObj as String)
                this@VoipP2PActivity.stopAndFinish()
            }
        }
    }

    private fun setupViews() {
        voipP2PManager!!.setupView(selfPlayer, targetPlayer, object : IXHResultCallback {
            override fun success(data: Any) {
                MLOC.d(TAG, "setupView success")
                MLOC.d(TAG, "onPickup")

            }

            override fun failed(errMsg: String) {
                MLOC.d(TAG, "setupView failed")
                this@VoipP2PActivity.stopAndFinish()
            }
        })
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
        voipP2PManager!!.accept(this, targetId, null)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.calling_hangup -> voipP2PManager!!.cancel(object : IXHResultCallback {
                override fun success(data: Any) {
                    MLOC.d(TAG, "cancel success")
                    this@VoipP2PActivity.stopAndFinish()
                }

                override fun failed(errMsg: String) {
                    MLOC.d(TAG, "cancel success")
                    this@VoipP2PActivity.stopAndFinish()
                }
            })
            R.id.talking_hangup -> voipP2PManager!!.hangup(object : IXHResultCallback {
                override fun success(data: Any) {
                    MLOC.d(TAG, "hangup success")
                    this@VoipP2PActivity.stopAndFinish()
                }

                override fun failed(errMsg: String) {
                    MLOC.d(TAG, "hangup failed")
                    this@VoipP2PActivity.stopAndFinish()
                }
            })
        }
    }

    private fun stopAndFinish() {
        if (starRTCAudioManager != null) {
            starRTCAudioManager!!.stop()
        }
        removeListener()
        finish()
    }

    companion object {

        var ACTION = "ACTION"
        var RING = "RING"
        var CALLING = "CALLING"
    }
}
