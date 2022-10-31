package com.anubis.module_webRTC.demo.voip

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.anubis.kt_extends.eGetSystemSharedPreferences
import com.anubis.kt_extends.eLog

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
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback
import kotlinx.android.synthetic.main.activity_voip_ringing.*
import org.jetbrains.anko.inputMethodManager

import java.text.SimpleDateFormat

class VoipRingingActivity : BaseActivity(), View.OnClickListener {
    private var status = false
    private var targetId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_voip_ringing)
        addListener()

        targetId = intent.getStringExtra("targetId")
        findViewById<View>(R.id.ring_hangoff).setOnClickListener(this)
        findViewById<View>(R.id.ring_pickup).setOnClickListener(this)
        (findViewById<View>(R.id.targetid_text) as TextView).text = targetId
        (findViewById<View>(R.id.head_img) as ImageView).setImageResource(MLOC.getHeadImage(this@VoipRingingActivity, targetId!!))
        findViewById<View>(R.id.head_bg).setBackgroundColor(ColorUtils.getColor(this@VoipRingingActivity, targetId))
        (findViewById<View>(R.id.head_cover) as CircularCoverView).setCoverColor(Color.parseColor("#000000"))
        val cint = DensityUtils.dip2px(this@VoipRingingActivity, 45f)
        (findViewById<View>(R.id.head_cover) as CircularCoverView).setRadians(cint, cint, cint, cint, 0)

        val historyBean = HistoryBean()
        historyBean.type = CoreDB.HISTORY_TYPE_VOIP
        historyBean.lastTime = SimpleDateFormat("MM-dd HH:mm").format(java.util.Date())
        historyBean.conversationId = targetId
        historyBean.newMsgCount = 1
        MLOC.addHistory(historyBean, true)

        if (this.eGetSystemSharedPreferences<Boolean>("isAutoAnswer", false, getSharedPreferences(this.packageName, Context.MODE_PRIVATE))) {
            onClick(ring_pickup)
        }

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
                MLOC.showMsg(this@VoipRingingActivity, "对方已挂断")
                finish()
            }
            AEvent.AEVENT_VOIP_REV_ERROR -> {
                MLOC.showMsg(this@VoipRingingActivity, eventObj as String)
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
            R.id.ring_hangoff -> XHClient.getInstance().voipManager.refuse(object : IXHResultCallback {
                override fun success(data: Any) {
                    finish()
                }

                override fun failed(errMsg: String) {
                    finish()
                }
            })
            R.id.ring_pickup -> {
                val intent = Intent(this@VoipRingingActivity, VoipActivity::class.java)
                intent.putExtra("targetId", targetId)
                intent.putExtra(VoipActivity.ACTION, VoipActivity.RING)
                startActivity(intent)
                finish()
            }
        }
    }


}
