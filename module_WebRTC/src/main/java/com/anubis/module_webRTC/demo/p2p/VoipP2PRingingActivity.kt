package com.anubis.module_webRTC.demo.p2p

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView

import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.demo.BaseActivity
import com.anubis.module_webRTC.demo.MLOC
import com.anubis.module_webRTC.ui.CircularCoverView
import com.anubis.module_webRTC.utils.AEvent
import com.anubis.module_webRTC.utils.ColorUtils
import com.anubis.module_webRTC.utils.DensityUtils
import com.starrtc.starrtcsdk.api.XHClient
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback

class VoipP2PRingingActivity : BaseActivity(), View.OnClickListener {

    private var targetId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_voip_p2p_ringing)
        addListener()

        targetId = intent.getStringExtra("targetId")
        findViewById<View>(R.id.ring_hangoff).setOnClickListener(this)
        findViewById<View>(R.id.ring_pickup).setOnClickListener(this)
        (findViewById<View>(R.id.targetid_text) as TextView).text = targetId
        findViewById<View>(R.id.head_bg).setBackgroundColor(ColorUtils.getColor(this@VoipP2PRingingActivity, targetId))
        (findViewById<View>(R.id.head_cover) as CircularCoverView).setCoverColor(Color.parseColor("#000000"))
        val cint = DensityUtils.dip2px(this@VoipP2PRingingActivity, 45f)
        (findViewById<View>(R.id.head_cover) as CircularCoverView).setRadians(cint, cint, cint, cint, 0)
    }

    fun addListener() {
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_HANGUP, this)
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_ERROR, this)
    }

    fun removeListener() {
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_HANGUP, this)
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_ERROR, this)
    }

    override fun dispatchEvent(aEventID: String, success: Boolean, eventObj: Any) {
        super.dispatchEvent(aEventID, success, eventObj)
        when (aEventID) {
            AEvent.AEVENT_VOIP_REV_HANGUP -> {
                MLOC.d("", "对方已挂断")
                MLOC.showMsg(this@VoipP2PRingingActivity, "对方已挂断")
                finish()
            }
            AEvent.AEVENT_VOIP_REV_ERROR -> {
                MLOC.showMsg(this@VoipP2PRingingActivity, eventObj as String)
                finish()
            }
        }
    }

    public override fun onRestart() {
        super.onRestart()
        addListener()
    }

    public override fun onStop() {
        super.onStop()
        removeListener()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ring_hangoff -> XHClient.getInstance().voipP2PManager.refuse(object : IXHResultCallback {
                override fun success(data: Any) {
                    finish()
                }

                override fun failed(errMsg: String) {
                    finish()
                }
            })
            R.id.ring_pickup -> {
                val intent = Intent(this@VoipP2PRingingActivity, VoipP2PActivity::class.java)
                intent.putExtra("targetId", targetId)
                intent.putExtra(VoipP2PActivity.ACTION, VoipP2PActivity.RING)
                startActivity(intent)
                finish()
            }
        }
    }
}
