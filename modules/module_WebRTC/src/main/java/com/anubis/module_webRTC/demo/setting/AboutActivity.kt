package com.anubis.module_webRTC.demo.setting

import android.os.Bundle
import android.view.View
import android.widget.TextView

import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.demo.BaseActivity
import com.starrtc.starrtcsdk.api.XHClient

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        findViewById<View>(R.id.title_left_btn).visibility = View.VISIBLE
        findViewById<View>(R.id.title_left_btn).setOnClickListener { finish() }
        (findViewById<View>(R.id.title_text) as TextView).text = "关于"
        (findViewById<View>(R.id.version) as TextView).text = XHClient.getVersion()
    }
}
