package com.anubis.app_webrtc

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anubis.kt_extends.eFile
import com.anubis.kt_extends.eGetSystemSharedPreferences
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eSetSystemSharedPreferences
import com.anubis.module_webRTC.demo.SplashActivity
import org.jetbrains.anko.startActivity
import java.io.File

class webRtcActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_rtc)
       startActivity(Intent(this, SplashActivity::class.java))
    }

}
