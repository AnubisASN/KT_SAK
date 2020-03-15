package com.anubis.module_webRTC.demo.voip

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
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
import com.anubis.module_webRTC.ui.CircularCoverView
import com.anubis.module_webRTC.utils.AEvent
import com.anubis.module_webRTC.utils.ColorUtils
import com.anubis.module_webRTC.utils.DensityUtils
import com.starrtc.starrtcsdk.api.XHClient
import com.starrtc.starrtcsdk.api.XHConstants
import com.starrtc.starrtcsdk.api.XHVoipManager
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager
import com.starrtc.starrtcsdk.core.pusher.XHCameraRecorder

import java.text.SimpleDateFormat

class VoipAudioActivity : BaseActivity(), View.OnClickListener {

    private var voipManager: XHVoipManager? = null
    private var timer: Chronometer? = null
    private var action: String? = null
      var targetId: String? = null

    private var starRTCAudioManager: StarRTCAudioManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_voip_audio)

        starRTCAudioManager = StarRTCAudioManager.create(this.applicationContext)
        starRTCAudioManager!!.start { selectedAudioDevice, availableAudioDevices -> MLOC.d("onAudioDeviceChanged ", selectedAudioDevice.name) }

        voipManager = XHClient.getInstance().voipManager
        voipManager!!.rtcMediaType = XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_AUDIO_ONLY
        voipManager!!.setRecorder(XHCameraRecorder())
        addListener()

        targetId = intent.getStringExtra("targetId")
        action = intent.getStringExtra(ACTION)
        timer = findViewById<View>(R.id.timer) as Chronometer

        (findViewById<View>(R.id.targetid_text) as TextView).text = targetId
        (findViewById<View>(R.id.head_img) as ImageView).setImageResource(MLOC.getHeadImage(this@VoipAudioActivity, targetId!!))
        findViewById<View>(R.id.head_bg).setBackgroundColor(ColorUtils.getColor(this@VoipAudioActivity, targetId))
        (findViewById<View>(R.id.head_cover) as CircularCoverView).setCoverColor(Color.parseColor("#000000"))
        val cint = DensityUtils.dip2px(this@VoipAudioActivity, 45f)
        (findViewById<View>(R.id.head_cover) as CircularCoverView).setRadians(cint, cint, cint, cint, 0)

        findViewById<View>(R.id.hangup).setOnClickListener(this)

        if (action == CALLING) {
            showCallingView()
            MLOC.d("newVoip", "call")
            voipManager!!.audioCall(this, targetId, object : IXHResultCallback {
                override fun success(data: Any) {
                    MLOC.d("newVoip", "call success")
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

    private fun setupView() {
        voipManager!!.setupView(null, null, object : IXHResultCallback {
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
    }

    fun removeListener() {
        MLOC.canPickupVoip = true
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
        AlertDialog.Builder(this@VoipAudioActivity).setCancelable(true)
                .setTitle("是否挂断?")
                .setNegativeButton("取消") { arg0, arg1 -> }.setPositiveButton("确定"
                ) { arg0, arg1 ->
                    timer!!.stop()
                    voipManager!!.hangup(object : IXHResultCallback {
                        override fun success(data: Any) {
                            stopAndFinish()
                        }

                        override fun failed(errMsg: String) {
                            MLOC.d("", "AEVENT_VOIP_ON_STOP errMsg:$errMsg")
                            MLOC.showMsg(this@VoipAudioActivity, errMsg)
                        }
                    })
                }.show()
    }

    override fun dispatchEvent(aEventID: String, success: Boolean, eventObj: Any) {
        super.dispatchEvent(aEventID, success, eventObj)
        when (aEventID) {
            AEvent.AEVENT_VOIP_REV_BUSY -> {
                MLOC.d("", "对方线路忙")
                MLOC.showMsg(this@VoipAudioActivity, "对方线路忙")
                stopAndFinish()
            }
            AEvent.AEVENT_VOIP_REV_REFUSED -> {
                MLOC.d("", "对方拒绝通话")
                MLOC.showMsg(this@VoipAudioActivity, "对方拒绝通话")
                stopAndFinish()
            }
            AEvent.AEVENT_VOIP_REV_HANGUP -> {
                MLOC.d("", "对方已挂断")
                MLOC.showMsg(this@VoipAudioActivity, "对方已挂断")
                timer!!.stop()
                stopAndFinish()
            }
            AEvent.AEVENT_VOIP_REV_CONNECT -> {
                MLOC.d("", "对方允许通话")
                showTalkingView()
            }
            AEvent.AEVENT_VOIP_REV_ERROR -> {
                MLOC.d("", eventObj as String)
                stopAndFinish()
            }
        }
    }

    private fun showCallingView() {
        MLOC.d("", "showCallingView")
        findViewById<View>(R.id.calling_txt).visibility = View.VISIBLE
        findViewById<View>(R.id.timer).visibility = View.INVISIBLE
    }

    private fun showTalkingView() {
        MLOC.d("", "showTalkingView")
        findViewById<View>(R.id.calling_txt).visibility = View.INVISIBLE
        findViewById<View>(R.id.timer).visibility = View.VISIBLE
        timer!!.base = SystemClock.elapsedRealtime()
        timer!!.start()
        setupView()
    }

    private fun onPickup() {
        voipManager!!.accept(this, targetId, object : IXHResultCallback {
            override fun success(data: Any) {
                MLOC.d("newVoip", "onPickup OK ")
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
            R.id.hangup -> voipManager!!.hangup(object : IXHResultCallback {
                override fun success(data: Any) {
                    stopAndFinish()
                }

                override fun failed(errMsg: String) {
                    stopAndFinish()
                }
            })
        }
    }

    private fun stopAndFinish() {
        if (starRTCAudioManager != null) {
            starRTCAudioManager!!.stop()
        }
        this@VoipAudioActivity.finish()
    }

    companion object {

        var ACTION = "ACTION"
        var RING = "RING"
        var CALLING = "CALLING"
    }
}
