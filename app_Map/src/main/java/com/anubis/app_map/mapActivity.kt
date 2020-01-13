package com.anubis.app_map

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.anubis.app_map.sdkdemo.activity.DemoMainActivity
import com.anubis.module_navi.eNavigate
import kotlinx.android.synthetic.main.map.*


class mapActivity : Activity() {
    //    var mLocationClient: LocationClient? = null
//    private val myListener = MyLocationListener()
    var init: eNavigate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map)
        init= eNavigate.eGetInit().eInit(this@mapActivity)
    }

    fun onClick(v: View) {
        when (v.id) {
            mGotoSettingsBtn.id -> startActivity(Intent(this@mapActivity, DemoMainActivity::class.java))
            mstate.id -> init!!.eStart(this@mapActivity, doubleArrayOf(22.8749207398,113.5022857878), doubleArrayOf(22.9354805574,113.5196496745))
        }

    }
}
