package com.anubis.module_webRTC.demo.p2p

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView

import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.demo.BaseActivity
import com.anubis.module_webRTC.utils.AEvent
import com.anubis.module_webRTC.utils.StarNetUtil
import com.starrtc.starrtcsdk.api.XHCustomConfig

class VoipP2PDemoActivity : BaseActivity() {
    private var onListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voip_p2p_main)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        addListener()
        (findViewById<View>(R.id.ip_addr) as TextView).text = StarNetUtil.getIP(this)
        findViewById<View>(R.id.button).setOnClickListener { startActivity(Intent(this@VoipP2PDemoActivity, VoipP2PCreateActivity::class.java)) }
    }

    public override fun onResume() {
        super.onResume()
        if (!onListening) {
            onListening = true
            XHCustomConfig.getInstance(this).initStarDirectLink()
        }
    }

    override fun onBackPressed() {
        removeListener()
        onListening = false
        XHCustomConfig.getInstance(this).stopStarDircetLink()
        finish()
    }

    public override fun onRestart() {
        super.onRestart()
        addListener()
    }

    private fun addListener() {
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING, this)
    }

    private fun removeListener() {
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING, this)
    }
}
