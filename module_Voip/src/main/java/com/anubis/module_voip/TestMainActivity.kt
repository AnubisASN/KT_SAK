package com.anubis.module_voip

import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.anubis.kt_extends.eClean
import com.anubis.kt_extends.eMediaPlayer
import com.anubis.kt_extends.ePlayVoice
import com.anubis.module_eventbus.post.ePostEvent
import com.anubis.module_voip.testAPP.Companion.mVoip
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.onClick

class TestMainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        button.onClick {
            ePostEvent(edit.text.toString(),timeMillis = 1500)
            startActivity(Intent(this,PhoneActivity::class.java))
        }
        start.onClick {
               ePlayVoice(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
        }
        stop.onClick { eMediaPlayer?.eClean()  }
    }
}
