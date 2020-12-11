package com.anubis.app_coroutine

import android.annotation.TargetApi
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anubis.module_tts.eTTS
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.onClick

class MainActivity : AppCompatActivity() {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      val   mTTS = eTTS.eInit(application, arrayOf("23094101","BIPWp4mKcgn1AzzCq5eCPUvD","O4QoEmiZThGClT03VVPgl2FaM2z0RGNo"))
        button.onClick {
mTTS.speak("123456")

        }
    }
}
