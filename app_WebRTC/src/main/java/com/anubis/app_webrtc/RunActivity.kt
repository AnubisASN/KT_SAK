package com.anubis.app_webrtc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.anubis.kt_extends.eLog

class RunActivity : AppCompatActivity() {
    companion object {
        var mRunActivity: RunActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run)
        mRunActivity = this
//        APP.onlyVoipActivity=this
        eLog("runActivity")
    }


    fun onClick(v: View?) {
        if (v == null) {
            this.finish()
        }
    }
}
