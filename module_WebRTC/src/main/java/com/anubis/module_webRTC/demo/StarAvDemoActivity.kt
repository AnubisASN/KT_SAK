package com.anubis.module_webRTC.demo


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.anubis.module_webRTC.demo.im.IMDemoActivity
import com.anubis.module_webRTC.demo.setting.SettingActivity
import com.anubis.module_webRTC.demo.voip.VoipListActivity
import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.demo.miniclass.MiniClassListActivity
import com.anubis.module_webRTC.demo.superroom.SuperRoomListActivity
import com.anubis.module_webRTC.demo.videolive.VideoLiveListActivity
import com.anubis.module_webRTC.demo.videomeeting.VideoMeetingListActivity
import com.starrtc.starrtcsdk.api.XHClient


class StarAvDemoActivity : BaseActivity(), View.OnClickListener {

    private var isOnline = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.anubis.module_webRTC.R.layout.activity_star_rtc_main)
        (findViewById<View>(com.anubis.module_webRTC.R.id.title_text) as TextView).setText(com.anubis.module_webRTC.R.string.app_name)
        MLOC.userId = MLOC.loadSharedData(applicationContext, "userId")
        (findViewById<View>(com.anubis.module_webRTC.R.id.userinfo_head) as ImageView).setImageResource(MLOC.getHeadImage(this, MLOC.userId!!))
        (findViewById<View>(com.anubis.module_webRTC.R.id.userinfo_id) as TextView).text = MLOC.userId
        findViewById<View>(com.anubis.module_webRTC.R.id.btn_main_im).setOnClickListener(this)
        findViewById<View>(com.anubis.module_webRTC.R.id.btn_main_voip).setOnClickListener(this)
        findViewById<View>(com.anubis.module_webRTC.R.id.btn_main_meeting).setOnClickListener(this)
        findViewById<View>(com.anubis.module_webRTC.R.id.btn_main_live).setOnClickListener(this)
        findViewById<View>(com.anubis.module_webRTC.R.id.btn_main_setting).setOnClickListener(this)
        findViewById<View>(com.anubis.module_webRTC.R.id.btn_main_class).setOnClickListener(this)
        findViewById<View>(com.anubis.module_webRTC.R.id.btn_main_audio).setOnClickListener(this)
    }

    override fun onBackPressed() {
        finish()
    }

    public override fun onResume() {
        super.onResume()
        if (MLOC.hasLogout!!) {
            finish()
            MLOC.hasLogout = false
            return
        }
        if (MLOC.userId == null) {
            startActivity(Intent(this@StarAvDemoActivity, SplashActivity::class.java))
            finish()
        }
        isOnline = XHClient.getInstance().isOnline
        if (isOnline) {
            findViewById<View>(com.anubis.module_webRTC.R.id.loading).visibility = View.INVISIBLE
        } else {
            findViewById<View>(com.anubis.module_webRTC.R.id.loading).visibility = View.VISIBLE
        }
        findViewById<View>(com.anubis.module_webRTC.R.id.voip_new).visibility = if (MLOC.hasNewVoipMsg) View.VISIBLE else View.INVISIBLE
        findViewById<View>(com.anubis.module_webRTC.R.id.im_new).visibility = if (MLOC.hasNewC2CMsg || MLOC.hasNewGroupMsg) View.VISIBLE else View.INVISIBLE
    }

    public override fun onRestart() {
        super.onRestart()
    }

    override fun onClick(v: View) {
        when (v.id) {
            com.anubis.module_webRTC.R.id.btn_main_voip -> startActivity(Intent(this,
                    VoipListActivity::class.java))
            com.anubis.module_webRTC.R.id.btn_main_meeting -> startActivity(Intent(this, VideoMeetingListActivity::class.java))
            com.anubis.module_webRTC.R.id.btn_main_live -> {
                val intent3 = Intent(this, VideoLiveListActivity::class.java)
                startActivity(intent3)
            }
            com.anubis.module_webRTC.R.id.btn_main_setting -> {
                val intent6 = Intent(this, SettingActivity::class.java)
                startActivity(intent6)
            }
            com.anubis.module_webRTC.R.id.btn_main_im -> {
                val intent7 = Intent(this, IMDemoActivity::class.java)
                startActivity(intent7)
            }
            com.anubis.module_webRTC.R.id.btn_main_class -> {
                val intent8 = Intent(this, MiniClassListActivity::class.java)
                startActivity(intent8)
            }
            com.anubis.module_webRTC.R.id.btn_main_audio -> {
                val intent9 = Intent(this, SuperRoomListActivity::class.java)
                startActivity(intent9)
            }
        }
    }

}
